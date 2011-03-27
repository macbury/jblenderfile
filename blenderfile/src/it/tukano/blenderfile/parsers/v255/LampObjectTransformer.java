package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileSdna;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderLamp;
import it.tukano.blenderfile.elements.BlenderLamp.LampType;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.io.IOException;

/**
 * Transforms the content of a lamp object
 * @author pgi
 */
public class LampObjectTransformer implements BlenderObjectTransformer {

    private static LampType lampTypeForCode(Number code) {
        LampType type = null;
        for (int i = 0; i < LampType.values().length && type == null; i++) {
            LampType lampType = LampType.values()[i];
            if(lampType.getCode() == code.intValue()) type = lampType;
        }
        return type;
    }

    public Object transform(ObjectDataWrapper object, BlenderSceneImpl scene) throws IOException {
        final BlenderFile blenderFile = object.getBlenderFile();
        final BlenderFileSdna sdna = blenderFile.getBlenderFileSdna();
        final BlenderObjectImpl blenderObject = object.toBlenderObject();
        final Number lampFilePosition = object.getObjectData().getPositionOfDataBlockInBlenderFile();
        final SDNAStructure lampStructure = (SDNAStructure) sdna.getStructureByName("Lamp", lampFilePosition);
        final String lampName = (String) ((SDNAStructure) lampStructure.getFieldValue("id", blenderFile)).getFieldValue("name", blenderFile);
        final LampType lampType = lampTypeForCode((Number) lampStructure.getFieldValue("type", blenderFile));
        final Number colorRed = (Number) lampStructure.getFieldValue("r", blenderFile);
        final Number colorGreen = (Number) lampStructure.getFieldValue("g", blenderFile);
        final Number colorBlue = (Number) lampStructure.getFieldValue("b", blenderFile);
        final Number shadowRed = (Number) lampStructure.getFieldValue("shdwr", blenderFile);
        final Number shadowGreen = (Number) lampStructure.getFieldValue("shdwg", blenderFile);
        final Number shadowBlue = (Number) lampStructure.getFieldValue("shdwb", blenderFile);
        final BlenderTuple3 colorRgb = new BlenderTuple3(colorRed, colorGreen, colorBlue);
        final BlenderTuple3 shadowColorRgb = new BlenderTuple3(shadowRed, shadowGreen, shadowBlue);
        final BlenderLamp blenderLamp = new BlenderLamp() {

            public LampType getType() {
                return lampType;
            }

            public String getName() {
                return lampName;
            }

            public BlenderTuple3 getRgb() {
                return colorRgb;
            }

            public BlenderTuple3 getShadowRgb() {
                return shadowColorRgb;
            }

            @Override
            public String toString() {
                return String.format("%s TYPE[%s] COLOR[%s] SHADOW[%s]", getName(), getType(), getRgb(), getShadowRgb());
            }
        };
        blenderObject.addObjectData(blenderLamp);
        scene.getOrCreateLayer(object.getLay()).add(blenderObject);
        return blenderObject;
    }

}
