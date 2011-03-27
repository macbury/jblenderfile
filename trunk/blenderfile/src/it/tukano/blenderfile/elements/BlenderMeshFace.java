package it.tukano.blenderfile.elements;

/**
 * The face of a mesh.
 * @author pgi
 */
public interface BlenderMeshFace {

    /**
     * The index of the material used by this face in the material's list of its
     * mesh
     * @return the index of the material used by this face
     */
    Number getMaterialIndex();

    /**
     * The index of the first vertex of this face in the vertex list of its mesh
     * @return the index of the first vertex of this face
     */
    Number getV1Index();

    /**
     * The index of the second vertex of this face in the vertex list of its mesh
     * @return the index of the second vertex of this face.
     */
    Number getV2Index();

    /**
     * The index of the third vertex of this face in the vertex list of its mesh
     * @return the index of the third vertex of this face.
     */
    Number getV3Index();

    /**
     * The index of the fourth vertex of this face in the vertex list of its mesh
     * @return the index of the fourth vertex of this face
     */
    Number getV4Index();

    /**
     * The number of vertices used by this face. Can be 2, 3 or 4. If this face
     * has 2 vertices, only getV1Index and getV2Index have meaningful values. If
     * is has 3 vertices, the triangle is formed by the first three vertices. If
     * is has 4 vertice, the quad is formed by all the vertices.
     * @return how many vertices composes this face
     */
    Number getVertexCount();
}