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
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(BlenderImporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(BlenderImporter.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "Parse ObjectType: {0}", blenderObject.getType());
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
                Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "Parse Lamp " + type);
            }
        }
    }

    private void parseMeshObjectWithLayerNode(BlenderObject blenderObject, Node layerNode) {
        Node blenderObjectNode = new Node(blenderObject.getName());

        blenderObjectNode.setTranslation(MathTypeConversions.Vector3(blenderObject.getLocation()));
        blenderObjectNode.setScale(MathTypeConversions.Vector3(blenderObject.getScale()));
        blenderObjectNode.setRotation(MathTypeConversions.Quaternion(blenderObject.getRotation()));

        layerNode.attachChild(blenderObjectNode);
        
        List<BlenderMesh> blenderMeshList = blenderObject.getObjectData(BlenderMesh.class);
        for (BlenderMesh blenderMesh : blenderMeshList) {
            parseBlenderMeshWithObjectNode(blenderMesh, blenderObjectNode);
        }
    }

    private void parseBlenderMeshWithObjectNode(BlenderMesh blenderMesh, Node blenderObjectNode) {
        Map<BlenderMaterial, List<BlenderMeshTriangle>> trianglesByMaterial = blenderMesh.getTrianglesByMaterial();
        for (Map.Entry<BlenderMaterial, List<BlenderMeshTriangle>> entry : trianglesByMaterial.entrySet()) {
            List<RenderState> renderStates = blenderMaterialToRenderStateList(blenderMesh, entry.getKey());
            MeshData meshData = blenderTriangleListToMeshData(blenderMesh, entry.getValue());
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

    private List<RenderState> blenderMaterialToRenderStateList(BlenderMesh mesh, BlenderMaterial blenderMaterial) {
        List<RenderState> renderStateList = new ArrayList<RenderState>();
        MaterialState materialState = blenderMaterialToMaterialState(blenderMaterial);
        TextureState textureState = blenderMaterialToTextureState(mesh, blenderMaterial);
        BlendState blendState = blenderMaterialToBlendState(mesh, blenderMaterial);
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

    private MeshData blenderTriangleListToMeshData(BlenderMesh mesh, List<BlenderMeshTriangle> triangleList) {
        FloatBuffer vertices = triangleListToVertexBuffer(triangleList);
        FloatBuffer normals = triangleListToNormalBuffer(triangleList);
        IntBuffer indices = triangleListToIndexBuffer(triangleList);
        List<FloatBufferData> texcoords = triangleListToTexCoordBufferList(mesh, triangleList);
        MeshData data = new MeshData();
        if(vertices != null) {
            data.setVertexBuffer(vertices);
        }
        if(normals != null) {
            data.setNormalBuffer(normals);
        }
        if(texcoords != null) {
            data.setTextureCoords(texcoords);
        }
        if(indices != null) {
            data.setIndexBuffer(indices);
            data.setIndexMode(IndexMode.Triangles);
        }
        return data;
    }

    private FloatBuffer triangleListToVertexBuffer(List<BlenderMeshTriangle> triangleList) {
        int triangleCount = triangleList.size();
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(triangleCount * 3 * 3);
        for (BlenderMeshTriangle blenderMeshTriangle : triangleList) {
            BlenderMeshVertex v1 = blenderMeshTriangle.getV1();
            BlenderMeshVertex v2 = blenderMeshTriangle.getV2();
            BlenderMeshVertex v3 = blenderMeshTriangle.getV3();
            BlenderTuple3 p1 = v1.getPosition();
            BlenderTuple3 p2 = v2.getPosition();
            BlenderTuple3 p3 = v3.getPosition();
            putTuples3IntoFloatBuffer(vertexBuffer, p1, p2, p3);
        }
        vertexBuffer.flip();
        return vertexBuffer;
    }

    private void putTuples3IntoFloatBuffer(FloatBuffer vertexBuffer, BlenderTuple3... tuples) {
        if(tuples == null) throw new IllegalArgumentException("tuples cannot be null");
        for (int i = 0; i < tuples.length; i++) {
            BlenderTuple3 tuple = tuples[i];
            vertexBuffer.put(tuple.getX().floatValue()).put(tuple.getY().floatValue()).put(tuple.getZ().floatValue());
        }
    }

    private FloatBuffer triangleListToNormalBuffer(List<BlenderMeshTriangle> triangleList) {
        int triangleCount = triangleList.size();
        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(triangleCount * 3 * 3);
        for (BlenderMeshTriangle blenderMeshTriangle : triangleList) {
            BlenderTuple3 n1 = blenderMeshTriangle.getV1().getNormal();
            BlenderTuple3 n2 = blenderMeshTriangle.getV2().getNormal();
            BlenderTuple3 n3 = blenderMeshTriangle.getV3().getNormal();
            putTuples3IntoFloatBuffer(normalBuffer, n1, n2, n3);
        }
        normalBuffer.flip();
        return normalBuffer;
    }

    private IntBuffer triangleListToIndexBuffer(List<BlenderMeshTriangle> triangleList) {
        int triangleCount = triangleList.size();
        IntBuffer indices = BufferUtils.createIntBuffer(triangleCount * 3);
        while(indices.hasRemaining()) indices.put(indices.position());
        indices.flip();
        return indices;
    }

    private List<FloatBufferData> triangleListToTexCoordBufferList(BlenderMesh mesh, List<BlenderMeshTriangle> triangleList) {
        int trianglesCount = triangleList.size();
        int texCoordCount = trianglesCount * 3;
        int texCoordBufferSize = texCoordCount * 2;
        Collection<String> texCoordSetNames = mesh.getTexCoordSetNames();
        Map<String, FloatBufferData> textureCoordinateBuffers = createTextureCoordinatesBuffer(texCoordSetNames, texCoordBufferSize);
        for (BlenderMeshTriangle blenderMeshTriangle : triangleList) {
            for (String string : texCoordSetNames) {
                FloatBufferData texCoordBuffer = textureCoordinateBuffers.get(string);
                BlenderTuple2 t1 = blenderMeshTriangle.getT1(string);
                BlenderTuple2 t2 = blenderMeshTriangle.getT2(string);
                BlenderTuple2 t3 = blenderMeshTriangle.getT3(string);
                putTuples2IntoFloatBuffer(texCoordBuffer.getBuffer(), t1, t2, t3);
            }
        }
        for (FloatBufferData floatBufferData : textureCoordinateBuffers.values()) {
            floatBufferData.getBuffer().flip();
        }
        return new ArrayList<FloatBufferData>(textureCoordinateBuffers.values());
    }

    private Map<String, FloatBufferData> createTextureCoordinatesBuffer(Collection<String> texCoordSetNames, int texCoordBufferSize) {
        Map<String, FloatBufferData> map = new HashMap<String, FloatBufferData>();
        for (String string : texCoordSetNames) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(texCoordBufferSize);
            FloatBufferData bufferData = new FloatBufferData(buffer, 2);
            map.put(string, bufferData);
        }
        return map;
    }

    private void putTuples2IntoFloatBuffer(FloatBuffer texCoordBuffer, BlenderTuple2... tuples) {
        if(tuples == null) throw new IllegalArgumentException("tuples cannot be null");
        for (int i = 0; i < tuples.length; i++) {
            BlenderTuple2 blenderTuple2 = tuples[i];
            texCoordBuffer.put(blenderTuple2.getX().floatValue()).put(blenderTuple2.getY().floatValue());
        }
    }

    private MaterialState blenderMaterialToMaterialState(BlenderMaterial blenderMaterial) {
        if(blenderMaterial == null) return null;
        
        MaterialState state = new MaterialState();
        state.setEnabled(true);
        state.setAmbient(MathTypeConversions.ColorRGBA(blenderMaterial.getAmbientRgb()));
        state.setDiffuse(MathTypeConversions.ColorRGBA(blenderMaterial.getRgb()));
        state.setSpecular(MathTypeConversions.ColorRGBA(blenderMaterial.getSpecularRgb()));
        state.setShininess(clamp(blenderMaterial.getSpecFactor().floatValue(), 128f, 0f));
        return state;
    }

    private float clamp(float factor, float min, float max) {
        factor *= 128f;
        return factor > max ? max : factor < min ? min : factor;
    }

    private TextureState blenderMaterialToTextureState(BlenderMesh mesh, BlenderMaterial blenderMaterial) {
        if(blenderMaterial == null) return null;

        TextureState textureState = (TextureState) RenderState.createState(RenderState.StateType.Texture);
        textureState.setEnabled(true);

        List<String> texCoordSetNames = mesh.getTexCoordSetNames();
        int textureSlotsCount = blenderMaterial.getTextureSlotsCount().intValue();
        for (int i= 0; i < textureSlotsCount; i++) {
            BlenderTexture blenderTexture = blenderMaterial.getTexture(i);
            if(blenderTexture != null) {
                BlenderImage blenderTextureImage = blenderTexture.getBlenderImage();
                if(blenderTextureImage != null) {
                    String uvName = blenderTexture.getUVName();
                    int textureIndex = Math.max(0, texCoordSetNames.indexOf(uvName));
                    String imagePath = blenderTextureImage.getImagePath();
                    if(imagePath.startsWith("\\\\")) imagePath = imagePath.substring(2, imagePath.length());
                    if(imagePath.startsWith("//")) imagePath = imagePath.substring(2, imagePath.length());
                    File imageFile = new File(imagePath);

                    ResourceSource texSource = getTextureSource();
                    ResourceSource textureImageSource = null;
                    
                    if(imageFile.isAbsolute()) {
                        try {
                            textureImageSource = new URLResourceSource(imageFile.toURI().toURL());
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(BlenderImporter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                       textureImageSource = texSource.getRelativeSource(blenderTextureImage.getImagePath());
                    }

                    Texture texture = null;

                    if(textureImageSource != null) {
                        texture = TextureManager.load(textureImageSource, Texture.MinificationFilter.Trilinear, true);
                    } else if(blenderTextureImage.getJavaImage() != null) {
                        texture = AWTTextureUtil.loadTexture(blenderTextureImage.getJavaImage(), Texture.MinificationFilter.Trilinear, TextureStoreFormat.RGBA8, true);
                    } else {
                        Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "cannot load texture");
                    }

                    if(texture != null) {
                        Texture.ApplyMode applyMode = Texture.ApplyMode.Decal;

                        List<MapTo> mapTo = blenderTexture.getMapTo();
                        BlendType blendType = blenderTexture.getBlendType();
                        if(blendType == BlendType.ADD) {
                            applyMode = Texture.ApplyMode.Add;
                        } else if(blendType == BlendType.BLEND) {
                            applyMode = Texture.ApplyMode.Modulate;
                        } else if(blendType == BlendType.MUL) {
                            applyMode = Texture.ApplyMode.Combine;
                        } else {
                            Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "MapTo {0}, using Decal default", blendType);
                        }
                        texture.setApply(applyMode);
                        textureState.setTexture(texture, textureIndex);
                    } else {
                        Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "cannot load texture");
                    }
                }
            }
        }
        return textureState;
    }

    /**
     * Checks if the given material requires a blend state
     * @param mesh the mesh whose material is parsed (unused...)
     * @param blenderMaterial the blender material
     * @return a BlendState or null, depending on the transparency settings of the material
     */
    private BlendState blenderMaterialToBlendState(BlenderMesh mesh, BlenderMaterial blenderMaterial) {
        if(blenderMaterial == null) {
            Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "no material -> no blend state");
            return null;
        }
        if(!blenderMaterial.isModeOn(BlenderMaterial.Mode.TRANSP)) {
            Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "material transparency is off");
            return null;
        }
        float alpha = blenderMaterial.getAlpha().floatValue();
        if(alpha == 1.0f) {
            Logger.getLogger(BlenderImporter.class.getName()).log(Level.INFO, "alpha == 1, no blend state");
            return null;
        }
        boolean hasDiffuseTexture = false;
        for (BlenderTexture blenderTexture : blenderMaterial.getActiveTextureUnits().values()) {
            if(blenderTexture.getMapTo().contains(MapTo.COL)) {
                hasDiffuseTexture = true;
                break;
            }
        }
        BlendState blendState = new BlendState();
        blendState.setBlendEnabled(true);
        blendState.setTestEnabled(true);
        if(hasDiffuseTexture) {
            blendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
            blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        } else {
            ColorRGBA transparent = MathTypeConversions.ColorRGBA(blenderMaterial.getRgb());
            transparent.setAlpha(alpha);
            blendState.setConstantColor(transparent);
            blendState.setSourceFunction(BlendState.SourceFunction.ConstantAlpha);
            blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusConstantAlpha);
        }
        return blendState;
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

    private boolean renderStateListContainsBlendState(List<RenderState> renderStates) {
        for (RenderState renderState : renderStates) {
            if(renderState.getType() == StateType.Blend) return true;
        }
        return false;
    }
}
