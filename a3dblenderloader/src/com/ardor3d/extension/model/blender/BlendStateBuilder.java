package com.ardor3d.extension.model.blender;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMaterial.MapTo;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderTexture;

/**
 * Transforms a blender material into a BlendState
 * @author pgi
 */
public class BlendStateBuilder {

    /**
     * Checks if the given material requires a blend state
     * @param mesh the mesh whose material is parsed (unused...)
     * @param blenderMaterial the blender material
     * @return a BlendState or null, depending on the transparency settings of the material
     */
    public BlendState blenderMaterialToBlendState(BlenderMesh mesh, BlenderMaterial blenderMaterial) {
        if(blenderMaterial == null) {
            Log.log("no material -> no blend state");
            return null;
        }
        if(!blenderMaterial.isModeOn(BlenderMaterial.Mode.TRANSP)) {
            Log.log("material transparency is off");
            return null;
        }
        float alpha = blenderMaterial.getAlpha().floatValue();
        if(alpha == 1.0f) {
            Log.log("alpha == 1, no blend state");
            return null;
        }
        boolean hasDiffuseTexture = false;
        for (BlenderTexture blenderTexture : blenderMaterial.getActiveTextureUnits().values()) {
            if(blenderTexture.getMapTo().contains(MapTo.COL)) {
                hasDiffuseTexture = true;
                break;
            }
        }
        BlendState blendState = new BlendState();
        blendState.setBlendEnabled(true);
        blendState.setTestEnabled(true);
        if(hasDiffuseTexture) {
            blendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
            blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        } else {
            ColorRGBA transparent = MathTypeConversions.ColorRGBA(blenderMaterial.getRgb());
            transparent.setAlpha(alpha);
            blendState.setConstantColor(transparent);
            blendState.setSourceFunction(BlendState.SourceFunction.ConstantAlpha);
            blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusConstantAlpha);
        }
        return blendState;
    }
}
