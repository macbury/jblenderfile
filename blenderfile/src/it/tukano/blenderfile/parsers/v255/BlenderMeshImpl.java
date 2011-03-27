package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderDeformVert;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderMeshFace;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderTuple2;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * BlenderMesh implementation
 * @author pgi
 */
public class BlenderMeshImpl implements BlenderMesh {
    private final BlenderTuple3 location, rotation, scale;
    private final String name;
    private final List<BlenderMeshVertex> vertices;
    private final List<BlenderMeshFace> faces;
    private final Map<String, List<MTFace>> texCoordSets;
    private final BlenderMaterial[] materials;
    private ArrayList<BlenderMeshTriangle> meshTriangles;
    private final List<BlenderDeformVertImpl> deformData;
    private final BlenderObject container;

    public BlenderMeshImpl(
            BlenderObject parentObject,
            BlenderTuple3 location,
            BlenderTuple3 rotation,
            BlenderTuple3 scale,
            String name,
            List<BlenderMeshVertex> vertices,
            List<BlenderMeshFace> faces,
            Map<String, List<MTFace>> texCoordSets,
            BlenderMaterial[] materials,
            List<BlenderDeformVertImpl> deform) {
        Set<String> texCoordSetsName = texCoordSets.keySet();
        this.container = parentObject;
        this.materials = materials;
        this.texCoordSets = texCoordSets;
        this.location = location;
        this.rotation = rotation;
        this.scale = scale;
        this.name = name;
        this.vertices = vertices;
        this.faces = faces;
        this.deformData = deform;
    }

    public BlenderObject getContainer() {
        return container;
    }

    public List<BlenderDeformVert> getVertexBoneAssignments() {
        return Collections.<BlenderDeformVert>unmodifiableList(deformData);
    }

    public Number getMaterialCount() {
        return materials.length;
    }

    public BlenderMaterial getMaterial(Number index) {
        return materials[index.intValue()];
    }

    public Map<BlenderMaterial, List<BlenderMeshTriangle>> getTrianglesByMaterial() {
        List<BlenderMeshTriangle> triangles = getTriangles();
        Map<BlenderMaterial, List<BlenderMeshTriangle>> map = new HashMap<BlenderMaterial, List<BlenderMeshTriangle>>();
        for(int i = 0; i < triangles.size(); i++) {
            BlenderMeshTriangle triangle = triangles.get(i);
            List<BlenderMeshTriangle> mappedList = map.get(triangle.getMaterial());
            if(mappedList == null) {
                map.put(triangle.getMaterial(), mappedList = new LinkedList<BlenderMeshTriangle>());
            }
            mappedList.add(triangle);
        }
        return map;
    }

    public synchronized List<BlenderMeshTriangle> getTriangles() {
        if(meshTriangles != null) return meshTriangles;

        meshTriangles = new ArrayList<BlenderMeshTriangle>();
        for(int faceIndex = 0; faceIndex < getFacesCount(); faceIndex++) {
            BlenderMeshFaceImpl face = (BlenderMeshFaceImpl) getFace(faceIndex);
            BlenderMaterial faceMaterial;
            if(face.getMaterialIndex().intValue() < getMaterialCount().intValue()) {
                faceMaterial = getMaterial(face.getMaterialIndex().intValue());
            } else {
                faceMaterial = null;
            }
            Number v1Index = face.getV1Index();
            Number v2Index = face.getV2Index();
            Number v3Index = face.getV3Index();
            Number v4Index = face.getV4Index();
            int faceVertexCount = face.getVertexCount().intValue();
            if(faceVertexCount > 2) {
                Map<String, BlenderTuple2[]> textureCoordinates = new HashMap<String, BlenderTuple2[]>();
                for (String string : texCoordSets.keySet()) {
                    MTFace faceTexData = texCoordSets.get(string).get(face.getFaceIndex().intValue());
                    BlenderTuple2[] set = {
                        faceTexData.getUV1(),
                        faceTexData.getUV2(),
                        faceTexData.getUV3()
                    };
                    textureCoordinates.put(string, set);
                }
                BlenderMeshTriangle t = new BlenderMeshTriangleImpl(
                        getVertex(v1Index.intValue()),
                        getVertex(v2Index.intValue()),
                        getVertex(v3Index.intValue()),
                        textureCoordinates,
                        faceMaterial);
                meshTriangles.add(t);
            }
            if(faceVertexCount == 4) {
                Map<String, BlenderTuple2[]> textureCoordinates = new HashMap<String, BlenderTuple2[]>();
                for (String string : texCoordSets.keySet()) {
                    MTFace faceTexData = texCoordSets.get(string).get(face.getFaceIndex().intValue());
                    BlenderTuple2[] set = {
                        faceTexData.getUV3(),
                        faceTexData.getUV4(),
                        faceTexData.getUV1()
                    };
                    textureCoordinates.put(string, set);
                }
                BlenderMeshTriangle t = new BlenderMeshTriangleImpl(
                        getVertex(v3Index.intValue()),
                        getVertex(v4Index.intValue()),
                        getVertex(v1Index.intValue()),
                        textureCoordinates,
                        faceMaterial);
                meshTriangles.add(t);
            }
        }
        return meshTriangles;
    }

    public BlenderMeshFace getFace(int index) {
        return faces.get(index);
    }

    public int getFacesCount() {
        return faces.size();
    }

    public BlenderMeshVertex getVertex(int index) {
        return vertices.get(index);
    }

    public int getVerticesCount() {
        return vertices.size();
    }

    public BlenderTuple3 getLocation() {
        return location;
    }

    public BlenderTuple3 getRotation() {
        return rotation;
    }

    public BlenderTuple3 getScale() {
        return scale;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s Vertices Count: %s", getName(), getVerticesCount());
    }

    public List<String> getTexCoordSetNames() {
        return new ArrayList<String>(texCoordSets.keySet());
    }
}
