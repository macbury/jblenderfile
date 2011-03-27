package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderImage;
import it.tukano.blenderfile.elements.BlenderMaterial.BlendType;
import it.tukano.blenderfile.elements.BlenderMaterial.MapTo;
import it.tukano.blenderfile.elements.BlenderMaterial.TexCo;
import it.tukano.blenderfile.elements.BlenderTexture;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Blender texture implementation
 * @author pgi
 */
public class BlenderTextureImpl implements BlenderTexture {
    private final BlendType BLEND_TYPE;
    private final TexCo texco;
    private final List<MapTo> MAPTO;
    private final String uvName;
    private final String textureName;
    private final String textureImageName;
    private final BlenderImage blenderImage;

    public BlenderTextureImpl(BlenderFile file, BlenderFileBlock block) throws IOException {
        BlenderImageImpl image = null;
        SDNAStructure data = block.listStructures("MTex").get(0);
        texco = new TexCo((Number) data.getFieldValue("texco", file));
        List<MapTo> mapToValues = new LinkedList<MapTo>();
        Number mapToMask = (Number) data.getFieldValue("mapto", file);
        for (MapTo mapTo : MapTo.values) {
            if(mapTo.isOn(mapToMask)) mapToValues.add(mapTo);
        }
        BLEND_TYPE = BlendType.valueOf((Number) data.getFieldValue("blendtype", file));
        this.MAPTO = Collections.unmodifiableList(mapToValues);
        uvName = (String) data.getFieldValue("uvname", file);
        SDNAStructure texStructure = (SDNAStructure) data.getFieldValue("tex", file);

        SDNAStructure idStructure = (SDNAStructure) texStructure.getFieldValue("id", file);
        textureName = (String) idStructure.getFieldValue("name", file);
        String imageName = null;
        SDNAStructure imageStructure = (SDNAStructure) texStructure.getFieldValue("ima", file);
        if(imageStructure != null) {
            String imageId = (String) ((SDNAStructure) texStructure.getFieldValue("id", file)).getFieldValue("name", file);
            imageName = (String) imageStructure.getFieldValue("name", file);
            image = new BlenderImageImpl().setImageLocation(imageName);
            SDNAStructure packedFile = (SDNAStructure) imageStructure.getFieldValue("packedfile", file);
            if(packedFile != null) {
                BlenderFileBlock dataBlock = packedFile.getPointedBlock("data", file);
                Number dataSize = (Number) packedFile.getFieldValue("size", file);
                if(dataBlock.getDataSize().intValue() == dataSize.intValue()) {
                    BinaryDataReader blockData = dataBlock.getSubDataReader();
                    ByteBuffer buffer = ByteBuffer.allocate(dataSize.intValue());
                    blockData.fill(buffer);
                    buffer.flip();
                    blockData.jumpTo(0);
                    image.setImageData(buffer);
                    try {
                        BufferedImage javaImage = ImageIO.read(blockData.asInputStream());
                        image.setJavaImage(javaImage);
                    } catch(IOException ex) {
                        Logger.getLogger(BlenderTextureImpl.class.getName()).log(Level.INFO, "cannot read packed image as java image", ex);
                    }
                } else {
                    Logger.getLogger(BlenderTextureImpl.class.getName()).log(Level.INFO, "cannot read data pack");
                }
            }
        } else {
            Logger.getLogger(BlenderTextureImpl.class.getName()).log(Level.INFO, "no ima structure found");
        }
        textureImageName = imageName;
        blenderImage = image;
    }

    public BlenderImage getBlenderImage() {
        return blenderImage;
    }
    
    public String getTextureImageName() {
        return textureImageName;
    }

    public String getUVName() {
        return uvName;
    }

    public TexCo getTexCo() {
        return texco;
    }

    public List<MapTo> getMapTo() {
        return MAPTO;
    }

    @Override
    public String toString() {
        return String.format("BlenderTexture %s TexCo: %s MapTo: %s", getTextureImageName(), getTexCo(), getMapTo());
    }

    public BlendType getBlendType() {
        return BLEND_TYPE;
    }
}
