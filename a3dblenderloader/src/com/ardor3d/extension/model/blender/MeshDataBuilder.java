package com.ardor3d.extension.model.blender;

import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderTuple2;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates the mesh data from a blender mesh
 * @author pgi
 */
public class MeshDataBuilder {

    /**
     * Transforms a set of blender mesh triangles into a mesh data. This is called for each material-triangles group in
     * the mesh (blender maps materials to faces, a3d materials to meshes)
     * @param mesh the blender mesh holding the triangles (and the uv layers)
     * @param triangleList the list of triangles to transform
     * @return the transformed mesh data
     */
    public MeshData blenderTriangleListToMeshData(BlenderMesh mesh, List<BlenderMeshTriangle> triangleList) {
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

    /**
     * Packs the positions of the given list of triangles into a FloatBuffer.
     * @param triangleList the list of triangles to pack
     * @return a float buffer with the positions of the vertices of the triangles in the given list. Each 9 entries
     * defines a triangle.
     */
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

    /**
     * A convenience method to put a set of tuples into a float buffer, starting from the
     * current position of the buffer
     * @param buffer the buffer to fill
     * @param tuples the tuples to push into the buffer
     */
    private void putTuples3IntoFloatBuffer(FloatBuffer buffer, BlenderTuple3... tuples) {
        if(tuples == null) throw new IllegalArgumentException("tuples cannot be null");
        for (int i = 0; i < tuples.length; i++) {
            BlenderTuple3 tuple = tuples[i];
            buffer.put(tuple.getX().floatValue()).put(tuple.getY().floatValue()).put(tuple.getZ().floatValue());
        }
    }

    /**
     * Creates a float buffer with the components of the normals of the triangles in the given list
     * @param triangleList the list of triangles
     * @return a float buffer with the normals of the given triangle's list. 3 float per vertex, 3 normals per
     * triangle.
     */
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

    /**
     * Generates the indexing buffer for the given sequence of triangles.
     * @param triangleList the list of triangles
     * @return the index buffer. 1 index per vertex, 3 index per triangle.
     */
    private IntBuffer triangleListToIndexBuffer(List<BlenderMeshTriangle> triangleList) {
        int triangleCount = triangleList.size();
        IntBuffer indices = BufferUtils.createIntBuffer(triangleCount * 3);
        while(indices.hasRemaining()) indices.put(indices.position());
        indices.flip();
        return indices;
    }

    /**
     * Generates the set of uv coords taking data from a triangle list (contains the uvs) and
     * the mesh (owning the triangle's list). The mesh contains the name of the available uv layers, the
     * triangles contains the uv coords for all the layers.
     * @param mesh the mesh containing the triangle list
     * @param triangleList the list of triangles to convert into uv sets
     * @return the uv layers.
     */
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

    /**
     * helper method used by triangleListToTexCoordBufferList, initializes the buffers used to hold the uv layers of a triangle's list
     * @param texCoordSetNames the names of the uv layers
     * @param texCoordBufferSize the size to use for the new buffers
     * @return a map that associates a uv layer name to an empty buffer.
     */
    private Map<String, FloatBufferData> createTextureCoordinatesBuffer(Collection<String> texCoordSetNames, int texCoordBufferSize) {
        Map<String, FloatBufferData> map = new HashMap<String, FloatBufferData>();
        for (String string : texCoordSetNames) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(texCoordBufferSize);
            FloatBufferData bufferData = new FloatBufferData(buffer, 2);
            map.put(string, bufferData);
        }
        return map;
    }

    /**
     * helper method used to fill a float buffer with a set of tuples
     * @param buffer the buffer to fill
     * @param tuples the tuples to push into the buffer
     */
    private void putTuples2IntoFloatBuffer(FloatBuffer buffer, BlenderTuple2... tuples) {
        if(tuples == null) throw new IllegalArgumentException("tuples cannot be null");
        for (int i = 0; i < tuples.length; i++) {
            BlenderTuple2 blenderTuple2 = tuples[i];
            buffer.put(blenderTuple2.getX().floatValue()).put(blenderTuple2.getY().floatValue());
        }
    }
}
