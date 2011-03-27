package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.parserstructures.BlenderFileSdna;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderObject.ObjectType;
import it.tukano.blenderfile.elements.UnitSettings;
import it.tukano.blenderfile.io.BinaryDataReader;
import it.tukano.blenderfile.parsers.BlenderFileBlockParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser for a blender scene v. 255.
 * @author pgi
 */
public class BlenderFileSceneParser implements BlenderFileBlockParser {
    private final Map<ObjectType, BlenderObjectTransformer> transformers;

    public BlenderFileSceneParser() {
        HashMap<ObjectType, BlenderObjectTransformer> map = new HashMap<ObjectType, BlenderObjectTransformer>();
        map.put(ObjectType.CAMERA, new CameraObjectTransformer());
        map.put(ObjectType.MESH, new MeshObjectTransformer());
        map.put(ObjectType.ARMATURE, new ArmatureObjectTransformer());
        map.put(ObjectType.EMPTY, new EmptyObjectTransformer());
        map.put(ObjectType.LAMP, new LampObjectTransformer());
        transformers = map;
    }

    public Object parse(BlenderFile blenderFile, BlenderFileBlock fileBlock) throws IOException {
        BlenderSceneImpl scene = new BlenderSceneImpl();
        BlenderFileHeader blenderFileHeader = fileBlock.getBlenderFileHeader();
        BinaryDataReader reader = blenderFileHeader.getReader();
        Number startOffset = fileBlock.getPositionOfDataBlockInBlenderFile();
        Number mark = reader.getCurrentPosition();
        reader.jumpTo(startOffset);
        parseScene(scene, blenderFile, fileBlock, reader);
        reader.jumpTo(mark);
        return scene;
    }

    private void parseScene(BlenderSceneImpl scene, BlenderFile blenderFile, BlenderFileBlock sceneBlock, BinaryDataReader reader) throws IOException {
        final BlenderFileHeader header = blenderFile.getBlenderFileHeader();
        final BlenderFileSdna sdna = blenderFile.getBlenderFileSdna();
        final SDNAStructure sceneStructure = sdna.getStructureByName("Scene", reader.getCurrentPosition());

        //scene id
        final SDNAStructure sceneIdData = (SDNAStructure) sceneStructure.getFieldValue("id", blenderFile);
        final String sceneName = (String) sceneIdData.getFieldValue("name", blenderFile);
        scene.setName(sceneName);

        //scene unit settings
        final SDNAStructure unitSettingsData = (SDNAStructure) sceneStructure.getFieldValue("unit", blenderFile);
        if(unitSettingsData != null) {
            final Number scaleLength = (Number) unitSettingsData.getFieldValue("scale_length", blenderFile);
            final Number system = (Number) unitSettingsData.getFieldValue("system", blenderFile);
            UnitSettings.System unitSystem = null;
            switch(system.intValue()) {
                case 0:
                    unitSystem = UnitSettings.System.NONE;
                    break;
                case 1:
                    unitSystem = UnitSettings.System.METRIC;
                    break;
                case 2:
                    unitSystem = UnitSettings.System.IMPERIAL;
                    break;
            }
            final UnitSettingsImpl unitSettings = new UnitSettingsImpl(unitSystem, scaleLength);
            scene.setUnitSettings(unitSettings);
        }

        //let's find the objects
        SDNAStructure baseData = (SDNAStructure) sceneStructure.getFieldValue("base", blenderFile);
        Number first = baseData.getPointerFieldValue("first", blenderFile);
        Base base = new Base(blenderFile, blenderFile.getBlockByOldMemAddress(first).getPositionOfDataBlockInBlenderFile());
        do {
            ObjectDataWrapper object = base.getObject();
            parseObject(scene, object);
            base = base.getNext();
        } while(base != null);
    }

    private void parseObject(BlenderSceneImpl scene, ObjectDataWrapper object) throws IOException {
        BlenderObjectTransformer transformer = transformers.get(object.getType());
        if(transformer != null) {
            transformer.transform(object, scene);
        } else {
            Logger.getLogger(BlenderFileSceneParser.class.getName()).log(Level.INFO, "Object Type {0} not supported yet", object.getType());
        }
    }

}
