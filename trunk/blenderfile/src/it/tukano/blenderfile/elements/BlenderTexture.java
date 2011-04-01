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

    /**
     * Returns the name of the texture
     * @return the name of this texture
     */
    public String getTextureImageName();

    /**
     * Returns the name of the uv layer this texture is mapped to (if TexCo is UV)
     * @return the name of the uv layer this texture is mapped to
     */
    public String getUVName();

    /**
     * Returns the texture coordinate mapping (uv, cube, cylinder...)
     * @return the texture coordinate mapping
     */
    public TexCo getTexCo();

    /**
     * Returns the blend type for this texture (add, sub, mul ...)
     * @return the blend type for this texture
     */
    public BlendType getBlendType();

    /**
     * Returns the set of mappings of this texture (ambient, specular, reflection...)
     * @return the set of mappings of this texture
     */
    public List<MapTo> getMapTo();

    /**
     * Returns the image used by this texture. The image can be null.
     * @return the image used by this texture.
     */
    public BlenderImage getBlenderImage();
}