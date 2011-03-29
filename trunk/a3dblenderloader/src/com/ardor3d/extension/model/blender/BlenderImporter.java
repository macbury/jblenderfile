package com.ardor3d.extension.model.blender;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.image.util.AWTTextureUtil;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.geom.BufferUtils;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;
import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.BlenderFileParameters;
import it.tukano.blenderfile.elements.BlenderImage;
import it.tukano.blenderfile.elements.BlenderLamp;
import it.tukano.blenderfile.elements.BlenderLamp.LampType;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMaterial.BlendType;
import it.tukano.blenderfile.elements.BlenderMaterial.MapTo;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderScene;
import it.tukano.blenderfile.elements.BlenderSceneLayer;
import it.tukano.blenderfile.elements.BlenderTexture;
import it.tukano.blenderfile.elements.BlenderTuple2;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
        List<BlenderSceneLayer> layers = blenderScene.getLayers();
        for (BlenderSceneLayer blenderSceneLayer : layers) {
            Node blenderLayer = blenderSceneLayerToArdor3dNode(blenderSceneLayer);
            sceneNode.attachChild(blenderLayer);
        }
        sceneNode.updateGeometricState(0);
        sceneNode.updateWorldTransform(true);
        sceneNode.updateWorldBound(true);
        sceneNode.updateWorldRenderStates(true);
        return sceneNode;
    }

    /**
     * Called during the conversion of a blender scene, transforms a layer into a node
     * @param blenderSceneLayer the layer to transform
     * @return the node with the layer contents
     */
    private Node blenderSceneLayerToArdor3dNode(BlenderSceneLayer blenderSceneLayer) {
        Node layerNode = new Node("Layer " + blenderSceneLayer.getIndex());
        List<BlenderObject> blenderObjectsInLayer = blenderSceneLayer.getBlenderObjects();
        for (BlenderObject blenderObject : blenderObjectsInLayer) {
            parseBlenderObjectWithLayerNode(blenderObject, layerNode);
        }
        return layerNode;
    }

    /**
     * Parses elements contained in a blender layer. Transforms a blender object into an ardor3d entity and
     * add it (if possible) to the node that represents the blender layer.
     * @param blenderObject the blender object to transform
     * @param layerNode the layer node where to add the transformed object
     */
    private void parseBlenderObjectWithLayerNode(BlenderObject blenderObject, Node layerNode) {
        switch(blenderObject.getType()) {
            case LAMP:
                parseLampObjectWithLayerNode(blenderObject, layerNode);
                break;
            case MESH:
                parseMeshObjectWithLayerNode(blenderObject, layerNode);
                break;
            default:
                Log.log("Parse ObjectType: ", blenderObject.getType());
        }
    }

    /**
     * Parses a lamp object into a light and adds it to the layerNode
     * @param blenderObject the blender object that contains the light to transform
     * @param layerNode the layer node
     */
    private void parseLampObjectWithLayerNode(BlenderObject blenderObject, Node layerNode) {
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

                LightState lightState = (LightState) layerNode.getLocalRenderState(RenderState.StateType.Light);
                if(lightState == null) {//add the light state if missing (in case of multiple lights)
                    layerNode.setRenderState(lightState = new LightState());
                }
                lightState.setEnabled(true);
                lightState.attach(light);
            } else {
                Log.log("Parse Lamp: ", type);
            }
        }
    }

    /**
     * Transforms a blender object that wraps a mesh into a node with an ardor3d mesh
     * @param blenderObject the blender object to transform
     * @param layerNode the node where to attach the transformed data
     */
    private void parseMeshObjectWithLayerNode(BlenderObject blenderObject, Node layerNode) {
        Node blenderObjectNode = new Node(blenderObject.getName());
        blenderObjectNode.setTranslation(MathTypeConversions.Vector3(blenderObject.getLocation()));
        blenderObjectNode.setScale(MathTypeConversions.Vector3(blenderObject.getScale()));
        blenderObjectNode.setRotation(MathTypeConversions.Matrix3(blenderObject.getRotation()));
        layerNode.attachChild(blenderObjectNode);
        List<BlenderMesh> blenderMeshList = blenderObject.getObjectData(BlenderMesh.class);
        for (BlenderMesh blenderMesh : blenderMeshList) {
            parseBlenderMeshWithObjectNode(blenderMesh, blenderObjectNode);
        }
    }

    /**
     * Called by parseMeshObject, transforms a blender mesh into a spatial and attached it to
     * the given ardor3d node
     * @param blenderMesh the blender mesh to transform
     * @param blenderObjectNode the node where to add the a3d mesh
     */
    private void parseBlenderMeshWithObjectNode(BlenderMesh blenderMesh, Node blenderObjectNode) {
        final MeshDataBuilder meshDataBuilder = new MeshDataBuilder();
        final Map<BlenderMaterial, List<BlenderMeshTriangle>> trianglesByMaterial = blenderMesh.getTrianglesByMaterial();
        for (Map.Entry<BlenderMaterial, List<BlenderMeshTriangle>> entry : trianglesByMaterial.entrySet()) {
            List<RenderState> renderStates = blenderMaterialToRenderStateList(blenderMesh, entry.getKey());
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
     * Used for debug purposes
     * @param mesh the mesh to debug
     */
    private void dumpMesh(Mesh mesh) {
        MeshData meshData = mesh.getMeshData();
        FloatBuffer vertexBuffer = meshData.getVertexBuffer();
        FloatBuffer normalBuffer = meshData.getNormalBuffer();
        IndexBufferData<?> indices = meshData.getIndices();
        if(vertexBuffer != null) for (int i= 0; i < vertexBuffer.capacity(); i+=3) {
            System.out.printf("v %.4f %.4f %.4f%n", vertexBuffer.get(i), vertexBuffer.get(i+1), vertexBuffer.get(i+2));
        }
        if(normalBuffer != null) for (int i= 0; i < normalBuffer.capacity(); i+=3) {
            System.out.printf("vn %.4f %.4f %.4f%n", normalBuffer.get(i), normalBuffer.get(i+1), normalBuffer.get(i+2));
        }
        List<FloatBufferData> textureCoords = meshData.getTextureCoords();
        if(textureCoords.size() > 0) {
            FloatBuffer buffer = textureCoords.get(0).getBuffer();
            for (int i= 0; i < buffer.capacity(); i+=2) {
                System.out.printf("vt %.4f %.4f%n", buffer.get(i), buffer.get(i+1));
            }
        }
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
}
