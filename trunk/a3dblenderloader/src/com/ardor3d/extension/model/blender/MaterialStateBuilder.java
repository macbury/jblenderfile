package com.ardor3d.extension.model.blender;

import com.ardor3d.renderer.state.MaterialState;
import it.tukano.blenderfile.elements.BlenderMaterial;

/**
 * Transforms a blender material into a material state
 * @author pgi
 */
public class MaterialStateBuilder {

    /**
     * Transforms a blender material into a material state
     * @param blenderMaterial the material to transform
     * @return the material state or null if the blender material has no material data
     */
    public MaterialState transformBlenderMaterial(BlenderMaterial blenderMaterial) {
        if(blenderMaterial == null) return null;

        MaterialState state = new MaterialState();
        state.setEnabled(true);
        state.setAmbient(MathTypeConversions.ColorRGBA(blenderMaterial.getAmbientRgb()));
        state.setDiffuse(MathTypeConversions.ColorRGBA(blenderMaterial.getRgb()));
        state.setSpecular(MathTypeConversions.ColorRGBA(blenderMaterial.getSpecularRgb()));
        state.setShininess(MathTypeConversions.clamp(blenderMaterial.getSpecFactor().floatValue(), 128f, 0f));
        return state;
    }
}
