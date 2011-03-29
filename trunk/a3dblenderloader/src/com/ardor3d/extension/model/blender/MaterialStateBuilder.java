package com.ardor3d.extension.model.blender;

import com.ardor3d.math.ColorRGBA;
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

        final MaterialState state = new MaterialState();
        state.setEnabled(true);
        
        if(blenderMaterial.hasDiffuseShader(BlenderMaterial.DiffuseShader.LAMBERT)) {
            setupLambert(state, blenderMaterial);
        } else {
            Log.log("Implement this");
            setupLambert(state, blenderMaterial);
        }

        if(blenderMaterial.hasSpecularShader(BlenderMaterial.SpecularShader.PHONG)) {
            setupPhong(state, blenderMaterial);
        } else {
            Log.log("Implement this");
            setupPhong(state, blenderMaterial);
        }
        return state;
    }

    /** Applies lambert diffuse shading values to the material state */
    private void setupLambert(MaterialState state, BlenderMaterial blenderMaterial) {
        final float refFactor = blenderMaterial.getRefFactor().floatValue(); // 0.0 ... 1.0
        final ColorRGBA diffuse = MathTypeConversions.ColorRGBA(blenderMaterial.getRgb());
        final ColorRGBA ambient = MathTypeConversions.ColorRGBA(blenderMaterial.getAmbientRgb());

        diffuse.multiplyLocal(refFactor);
        state.setDiffuse(diffuse);
        state.setAmbient(ambient);
        state.setShininess(0);
    }

    /** Applies phong specular shading values to the material state */
    private void setupPhong(MaterialState state, BlenderMaterial blenderMaterial) {
        final float specFactor = blenderMaterial.getSpecFactor().floatValue(); //0.0 ... 2.0
        final int hardFactor = blenderMaterial.getHardnessFactor().intValue(); //1 ... 511
        final ColorRGBA ambient = MathTypeConversions.ColorRGBA(blenderMaterial.getAmbientRgb());
        final ColorRGBA specular = MathTypeConversions.ColorRGBA(blenderMaterial.getSpecularRgb());
        final float shininess = hardFactor / 511f * 128f;

        state.setAmbient(ambient);
        state.setSpecular(specular.multiplyLocal(specFactor / 2f));
        state.setShininess(shininess);
    }
}
