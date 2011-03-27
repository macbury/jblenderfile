package it.tukano.blenderfile.elements;

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
}