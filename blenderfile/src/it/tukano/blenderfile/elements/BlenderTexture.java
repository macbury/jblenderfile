package it.tukano.blenderfile.elements;

import it.tukano.blenderfile.elements.BlenderMaterial.BlendType;
import it.tukano.blenderfile.elements.BlenderMaterial.MapTo;
import it.tukano.blenderfile.elements.BlenderMaterial.TexCo;
import java.util.List;

/**
 * A texture.
 * @author pgi
 */
public interface BlenderTexture {

    public String getTextureImageName();

    public String getUVName();

    public TexCo getTexCo();

    public BlendType getBlendType();

    public List<MapTo> getMapTo();

    /**
     * Returns the image used by this texture. The image can be null.
     * @return the image used by this texture.
     */
    public BlenderImage getBlenderImage();
}