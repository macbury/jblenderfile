package com.ardor3d.extension.model.blender;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.image.util.AWTTextureUtil;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;
import it.tukano.blenderfile.elements.BlenderImage;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMaterial.BlendType;
import it.tukano.blenderfile.elements.BlenderMaterial.MapTo;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderTexture;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transforms a blender material into a TextureState
 * @author pgi
 */
public class TextureStateBuilder {

    /**
     * Transforms a blender material into a texture state
     * @param mesh the mesh that ownes the uv layers
     * @param blenderMaterial the material to transform
     * @param texSource the resource source for texture images
     * @return the texture state or null if the material has no texture data
     */
    public TextureState blenderMaterialToTextureState(BlenderMesh mesh, BlenderMaterial blenderMaterial, ResourceSource texSource) {
        if(blenderMaterial == null) {
            Log.log("no material found for texture state");
            return null;
        }

        Log.log("Reading texture state...");

        TextureState textureState = (TextureState) RenderState.createState(RenderState.StateType.Texture);
        textureState.setEnabled(true);

        List<String> texCoordSetNames = mesh.getTexCoordSetNames();
        int textureSlotsCount = blenderMaterial.getTextureSlotsCount().intValue();

        Log.log("Texture Slots Count: ", textureSlotsCount);

        for (int i= 0; i < textureSlotsCount; i++) {
            BlenderTexture blenderTexture = blenderMaterial.getTexture(i);
            if(blenderTexture != null) {
                Log.log("Slot: ", i, " has texture: ", blenderTexture);
                BlenderImage blenderTextureImage = blenderTexture.getBlenderImage();
                if(blenderTextureImage != null) {
                    String uvName = blenderTexture.getUVName();
                    int textureIndex = Math.max(0, texCoordSetNames.indexOf(uvName));
                    String imagePath = blenderTextureImage.getImagePath();
                    if(imagePath.startsWith("\\\\")) imagePath = imagePath.substring(2, imagePath.length());
                    if(imagePath.startsWith("//")) imagePath = imagePath.substring(2, imagePath.length());
                    File imageFile = new File(imagePath);

                    ResourceSource textureImageSource = null;

                    if(imageFile.isAbsolute() && imageFile.exists()) {
                        try {
                            textureImageSource = new URLResourceSource(imageFile.toURI().toURL());
                        } catch (MalformedURLException ex) {
                            Log.log(ex);
                        }
                    } else {
                        File file = new File(blenderTextureImage.getImagePath());
                        String path = file.getPath().substring(file.getParent().length() + 1);
                        Log.log("Relative image path: " + path);
                        textureImageSource = texSource.getRelativeSource(path);
                    }

                    Texture texture = null;

                    Log.log("loading blender texture image ", blenderTextureImage);
                    if(textureImageSource != null) {
                        Log.log("loading texture from file:", textureImageSource);
                        texture = TextureManager.load(textureImageSource, Texture.MinificationFilter.Trilinear, true);
                    } else if(blenderTextureImage.getJavaImage() != null) {
                        Log.log("loading texture from java image");
                        texture = AWTTextureUtil.loadTexture(blenderTextureImage.getJavaImage(), Texture.MinificationFilter.Trilinear, TextureStoreFormat.RGBA8, true);
                    } else {
                        Log.log("cannot load texture (", textureImageSource, ")(", blenderTextureImage,")");
                    }

                    if(texture != null) {
                        Texture.ApplyMode applyMode = Texture.ApplyMode.Decal;

                        List<MapTo> mapTo = blenderTexture.getMapTo();
                        BlendType blendType = blenderTexture.getBlendType();
                        if(blendType == BlendType.ADD) {
                            applyMode = Texture.ApplyMode.Add;
                        } else if(blendType == BlendType.BLEND) {
                            applyMode = Texture.ApplyMode.Modulate;
                        } else if(blendType == BlendType.MUL) {
                            applyMode = Texture.ApplyMode.Combine;
                        } else {
                            Log.log("MapTo", blendType, "using Decal default");
                        }
                        texture.setApply(applyMode);
                        textureState.setTexture(texture, textureIndex);
                    } else {
                        Log.log("cannot load texture");
                    }
                }
            }
        }
        return textureState;
    }
}
