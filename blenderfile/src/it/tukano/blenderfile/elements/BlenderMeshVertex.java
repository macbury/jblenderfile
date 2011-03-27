package it.tukano.blenderfile.elements;

/**
 * The vertex of a mesh
 * @author pgi
 */
public interface BlenderMeshVertex {

    /**
     * Index of the vertex relative to the original mesh vertex data.
     * @return the index of this vertex
     */
    Number getIndex();

    /**
     * The normal of this vertex
     * @return the normal of this vertex
     */
    BlenderTuple3 getNormal();

    /**
     * The position of this vertex (3 float)
     * @return the position of this vertex
     */
    BlenderTuple3 getPosition();

    /**
     * The material index of this vertex
     * @return the material index of this vertex
     */
    Number getMaterialIndex();
}