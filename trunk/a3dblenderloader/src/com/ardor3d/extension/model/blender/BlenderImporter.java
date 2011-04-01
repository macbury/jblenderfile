package com.ardor3d.extension.model.blender;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.util.resource.ResourceSource;
import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.BlenderFileParameters;
import it.tukano.blenderfile.elements.BlenderLamp;
import it.tukano.blenderfile.elements.BlenderLamp.LampType;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderScene;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Converts a blender scene into an ardor3d node list (one node per scene)
 * @author pgi
 */
public class BlenderImporter {
    
    private ResourceSource modelSource;
    private ResourceSource textureSource;

    /**
     * Instance initializer
     */
    public BlenderImporter() {
    }

    private ResourceSource getModelSource() {
        return modelSource;
    }

    private ResourceSource getTextureSource() {
        return textureSource == null ? getModelSource() : textureSource;
    }

    /**
     * Load a blender scene from the given resource.
     * @param resource the .blend resource
     * @return a list of nodes (one node per scene) wrapped into a BlenderStorage
     */
    public BlenderStorage load(ResourceSource resource) {
        this.modelSource = resource;
        InputStream stream = null;
        List<Node> sceneNodes = new LinkedList<Node>();
        try {
            stream = resource.openStream();
            BlenderFileParameters parameters = new BlenderFileParameters(stream);
            BlenderFile blenderFile = new BlenderFile(parameters);
            List<BlenderScene> scenes = blenderFile.getScenes();
            for (BlenderScene blenderScene : scenes) {
                Node node = blenderSceneToArdor3dNode(blenderScene);
                sceneNodes.add(node);
            }
        } catch(IOException ex) {
            Log.log(ex);
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Log.log(ex);
                }
            }
        }
        return new BlenderStorage(sceneNodes);
    }

    /**
     * Called once for each blender scene in the load method: parses the scene into an Ardor3D node
     * @param blenderScene the scene to parse
     * @return the node with the parsed scene
     */
    private Node blenderSceneToArdor3dNode(BlenderScene blenderScene) {
        Node sceneNode = new Node(blenderScene.getName());
        List<BlenderObject> blenderSceneRoots = blenderScene.findSceneRoots();
        for (BlenderObject blenderObject : blenderSceneRoots) {
            parseBlenderObject(blenderObject, sceneNode);
        }
        sceneNode.updateGeometricState(0);
        sceneNode.updateWorldTransform(true);
        sceneNode.updateWorldBound(true);
        sceneNode.updateWorldRenderStates(true);
        return sceneNode;
    }

    /**
     * Parses elements contained in a blender layer. Transforms a blender object into an ardor3d entity and
     * add it (if possible) to the node that represents the blender layer.
     * @param blenderObject the blender object to transform
     * @param parentNode the layer node where to add the transformed object
     */
    private void parseBlenderObject(BlenderObject blenderObject, Node parentNode) {
        switch(blenderObject.getType()) {
            case LAMP:
                parseLampObject(blenderObject, parentNode);
                break;
            case MESH:
                parseMeshObject(blenderObject, parentNode);
                break;
            case EMPTY:
                parseEmptyObject(blenderObject, parentNode);
                break;
            case ARMATURE:
                parseArmatureObject(blenderObject, parentNode);
                break;
            default:
                Log.log("Parse ObjectType: ", blenderObject.getType());
        }
    }

    /**
     * Parses a lamp object into a light and adds it to the layerNode
     * @param blenderObject the blender object that contains the light to transform
     * @param parent the layer node
     */
    private void parseLampObject(BlenderObject blenderObject, Node parent) {
        List<BlenderLamp> lampList = blenderObject.getObjectData(BlenderLamp.class);
        for (BlenderLamp blenderLamp : lampList) {
            LampType type = blenderLamp.getType();
            if(type == LampType.LOCAL) {
                PointLight light = new PointLight();
                light.setName(blenderLamp.getName());
                light.setLocation(MathTypeConversions.Vector3(blenderObject.getLocation()));
                light.setAmbient(MathTypeConversions.ColorRGBA(blenderLamp.getRgb()));
                light.setDiffuse(MathTypeConversions.ColorRGBA(blenderLamp.getRgb()));
                light.setEnabled(true);

                LightState lightState = (LightState) parent.getLocalRenderState(RenderState.StateType.Light);
                if(lightState == null) {//add the light state if missing (in case of multiple lights)
                    parent.setRenderState(lightState = new LightState());
                }
                lightState.setEnabled(true);
                lightState.attach(light);
            } else {
                Log.log("Parse Lamp: ", type);
            }
        }
        List<? extends BlenderObject> children = blenderObject.getChildren();
        if(!children.isEmpty()) {
            Node node = new Node(blenderObject.getName());
            parent.attachChild(node);
            setSpatialTransform(node, blenderObject);
            for (BlenderObject child : children) {
                parseBlenderObject(child, node);
            }
        }
    }

    /**
     * Transforms a blender object that wraps a mesh into a node with an ardor3d mesh
     * @param blenderObject the blender object to transform
     * @param layerNode the node where to attach the transformed data
     */
    private void parseMeshObject(BlenderObject blenderObject, Node layerNode) {
        Node blenderObjectNode = new Node(blenderObject.getName());
        blenderObjectNode.setTranslation(MathTypeConversions.Vector3(blenderObject.getLocation()));
        blenderObjectNode.setScale(MathTypeConversions.Vector3(blenderObject.getScale()));
        blenderObjectNode.setRotation(MathTypeConversions.Matrix3(blenderObject.getRotation()));
        layerNode.attachChild(blenderObjectNode);
        List<BlenderMesh> blenderMeshList = blenderObject.getObjectData(BlenderMesh.class);
        for (BlenderMesh blenderMesh : blenderMeshList) {
            parseBlenderMeshWithObjectNode(blenderMesh, blenderObjectNode);
        }
        for (BlenderObject child : blenderObject.getChildren()) {
            parseBlenderObject(child, blenderObjectNode);
        }
    }

    /**
     * Called by parseMeshObject, transforms a blender mesh into a spatial and attaches it to
     * the given ardor3d node
     * @param blenderMesh the blender mesh to transform
     * @param blenderObjectNode the node where to add the a3d mesh
     */
    private void parseBlenderMeshWithObjectNode(BlenderMesh blenderMesh, Node blenderObjectNode) {
        final MeshDataBuilder meshDataBuilder = new MeshDataBuilder();
        final Map<BlenderMaterial, List<BlenderMeshTriangle>> trianglesByMaterial = blenderMesh.getTrianglesByMaterial();
        for (Map.Entry<BlenderMaterial, List<BlenderMeshTriangle>> entry : trianglesByMaterial.entrySet()) {
            final BlenderMaterial blenderMaterial = entry.getKey();
            List<RenderState> renderStates = blenderMaterialToRenderStateList(blenderMesh, blenderMaterial);
            MeshData meshData = meshDataBuilder.blenderTriangleListToMeshData(blenderMesh, entry.getValue());
            Mesh mesh = new Mesh(blenderMesh.getName());
            for (RenderState renderState : renderStates) {
                mesh.setRenderState(renderState);
            }
            mesh.setMeshData(meshData);
            mesh.setModelBound(new BoundingBox());
            if(renderStateListContainsBlendState(renderStates)) {
                mesh.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
            }
            if(blenderMaterial != null && blenderMaterial.isModeOn(BlenderMaterial.Mode.SHADELESS)) {
                ColorRGBA solidColor = MathTypeConversions.ColorRGBA(blenderMaterial.getRgb());
                Log.log("shadeless material found, applying solid color: ", solidColor);
                mesh.clearRenderState(StateType.Material);
                mesh.setDefaultColor(MathTypeConversions.ColorRGBA(blenderMaterial.getRgb()));
                mesh.getSceneHints().setLightCombineMode(LightCombineMode.Off);
            }
            blenderObjectNode.attachChild(mesh);
        }
    }

    /**
     * Converts a blender material into a set of a3d render states
     * @param mesh the blender mesh that uses the material (the mesh has the uv coords sets to check for
     * texture mapping)
     * @param blenderMaterial the material to transform
     * @return the set of render states (can be emtpy)
     */
    private List<RenderState> blenderMaterialToRenderStateList(BlenderMesh mesh, BlenderMaterial blenderMaterial) {
        final MaterialStateBuilder materialBuilder = new MaterialStateBuilder();
        final TextureStateBuilder textureBuilder = new TextureStateBuilder();
        final BlendStateBuilder blendBuilder = new BlendStateBuilder();

        final List<RenderState> renderStateList = new ArrayList<RenderState>();

        final MaterialState materialState = materialBuilder.transformBlenderMaterial(blenderMaterial);
        final TextureState textureState = textureBuilder.blenderMaterialToTextureState(mesh, blenderMaterial, getTextureSource());
        final BlendState blendState = blendBuilder.blenderMaterialToBlendState(mesh, blenderMaterial);

        if(materialState != null) {
            renderStateList.add(materialState);
        }
        if(textureState != null) {
            renderStateList.add(textureState);
        }
        if(blendState != null) {
            renderStateList.add(blendState);
        }
        return renderStateList;
    }

    /**
     * Checks if the given set of render states contains a BlendState
     * @param renderStates the render state list to scan
     * @return true if renderStates contains at least one BlendState
     */
    private boolean renderStateListContainsBlendState(List<RenderState> renderStates) {
        for (RenderState renderState : renderStates) {
            if(renderState.getType() == StateType.Blend) return true;
        }
        return false;
    }

    private void parseEmptyObject(BlenderObject blenderObject, Node parentNode) {
        Node node = new Node(blenderObject.getName());
        node.setTranslation(MathTypeConversions.Vector3(blenderObject.getLocation()));
        node.setScale(MathTypeConversions.Vector3(blenderObject.getScale()));
        node.setRotation(MathTypeConversions.Matrix3(blenderObject.getRotation()));
        parentNode.attachChild(node);
        for (BlenderObject child : blenderObject.getChildren()) {
            parseBlenderObject(blenderObject, node);
        }
    }

    private void setSpatialTransform(Spatial spatial, BlenderObject blenderObject) {
        spatial.setTranslation(MathTypeConversions.Vector3(blenderObject.getLocation()));
        spatial.setScale(MathTypeConversions.Vector3(blenderObject.getScale()));
        spatial.setRotation(MathTypeConversions.Matrix3(blenderObject.getRotation()));
    }

    private void parseArmatureObject(BlenderObject blenderObject, Node parentNode) {
        Log.log("parse armature object");
        Node node = new Node(blenderObject.getName());
        setSpatialTransform(node, blenderObject);
        parentNode.attachChild(node);
        for (BlenderObject child : blenderObject.getChildren()) {
            parseBlenderObject(child, node);
        }
    }
}
