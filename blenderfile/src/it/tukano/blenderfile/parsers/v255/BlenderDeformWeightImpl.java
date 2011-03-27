package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderDeformWeight;

/**
 * Blender deform weight.
 * @author pgi
 */
public class BlenderDeformWeightImpl implements BlenderDeformWeight {
    private final Number boneIndex;
    private final Number weight;
    private final String boneName;

    public BlenderDeformWeightImpl(Number def_nr, Number weight, String boneName) {
        boneIndex = def_nr;
        this.weight = weight;
        this.boneName = boneName;
    }

    public String getBoneName() {
        return boneName;
    }

    public Number getBoneIndex() {
        return boneIndex;
    }

    public Number getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return getBoneName() + " " + getBoneIndex() + " " + getWeight();
    }
}
