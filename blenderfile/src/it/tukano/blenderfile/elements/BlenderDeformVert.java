package it.tukano.blenderfile.elements;

import java.util.List;

/**
 * Vertex-Bone assignment data
 * @author pgi
 */
public interface BlenderDeformVert {

    /**
     * Returns the index of the deformed vertex
     * @return the index of the deformed vertex
     */
    Number getDeformedVertexIndex();

    /**
     * Returns the set of bone-weight parameters for this vertex
     * @return a list of bone-weight parameters. Can be empty, never null.
     */
    List<? extends BlenderDeformWeight> getWeights();
}