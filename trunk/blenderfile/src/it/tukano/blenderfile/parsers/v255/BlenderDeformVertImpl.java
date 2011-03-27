package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderDeformVert;
import it.tukano.blenderfile.elements.BlenderDeformWeight;
import java.util.LinkedList;
import java.util.List;

/**
 * BlenderDeformVert implementation
 * @author pgi
 */
public class BlenderDeformVertImpl implements BlenderDeformVert {
    
    private final Number deformedVertexIndex;
    private final List<BlenderDeformWeightImpl> weights = new LinkedList<BlenderDeformWeightImpl>();

    BlenderDeformVertImpl(Number vertexIndex) {
        this.deformedVertexIndex = vertexIndex;
    }

    public Number getDeformedVertexIndex() {
        return deformedVertexIndex;
    }

    synchronized void add(BlenderDeformWeightImpl bdw) {
        weights.add(bdw);
    }

    public List<? extends BlenderDeformWeight> getWeights() {
        return weights;
    }
}
