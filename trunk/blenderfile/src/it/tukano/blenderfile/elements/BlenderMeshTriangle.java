package it.tukano.blenderfile.elements;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * A triangle of a blender mesh
 * @author pgi
 */
public interface BlenderMeshTriangle {

    /**
     * The first vertex of this triangle
     * @return the first vertex of this triangle
     */
    BlenderMeshVertex getV1();

    /**
     * The second vertex of this triangle
     * @return the second vertex of this triangle
     */
    BlenderMeshVertex getV2();

    /**
     * The third vertex of this triangle
     * @return the third vertex of this triangle
     */
    BlenderMeshVertex getV3();

    /**
     * The texture coordinate of the first vertex of this triangle for the given
     * texture coord set
     * @param setName the name of the texture coord set
     * @return the texture coordinate from the requested set or null if no such
     * set exists
     */
    BlenderTuple2 getT1(String setName);

    /**
     * The texture coordinate of the second vertex of this triangle for the given
     * texture coord set
     * @param setName the name of the texture coord set
     * @return the texture coordinate from the requested set or null if no such
     * set exists
     */
    BlenderTuple2 getT2(String setName);

    /**
     * The texture coordinate of the third vertex of this triangle for the given
     * texture coord set
     * @param setName the name of the texture coord set
     * @return the texture coordinate from the requested set or null if no such
     * set exists
     */
    BlenderTuple2 getT3(String setName);

    /**
     * Returns the material of this triangle or null if no material is set.
     * @return the material of this triangle.
     */
    BlenderMaterial getMaterial();

    /**
     * Fills the given buffer with the position of the vertices of this triangle.
     * Starts at the current position of the buffer. The buffer remaining space
     * must be at least 9 (3 values per vertex)
     * @param coordBuffer the buffer to fill
     */
    void pushVerticesCoordinates(FloatBuffer coordBuffer);

    /**
     * Fills the given buffer with the position of the normals of the vertices
     * of this triangle. Of.
     * @param normalBuffer the buffer to fill
     */
    void pushNormalsCoordinates(FloatBuffer normalBuffer);

    /**
     * Fills the given buffer's array with the uv coords of the vertices of this
     * triangle.
     * @param texCoordSetNames the list of texture coordinates set layers to use
     * @param uvBuffer the array of buffers to fill. The length of the array must
     * be at least texCoordSetNames.size(). The remaining space of the buffers must
     * be at least 6 (2 values per vertex)
     */
    void pushTexCoordSetsCoordinates(List<String> texCoordSetNames, FloatBuffer[] uvBuffer);
}