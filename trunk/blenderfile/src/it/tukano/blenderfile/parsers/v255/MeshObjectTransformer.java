package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.Log;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.parserstructures.BlenderFileSdna;
import it.tukano.blenderfile.parserstructures.ConstantValues.CustomDataType;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMeshFace;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderTuple3;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transforms the raw blender file data into a BlenderMesh instance. 
 * @author pgi
 */
public class MeshObjectTransformer implements BlenderObjectTransformer {

    public MeshObjectTransformer() {
    }

    public Object transform(ObjectDataWrapper object, BlenderSceneImpl scene) throws IOException {
        final BlenderObjectImpl blenderObject = object.toBlenderObject();

        final BlenderFile blenderFile = object.getBlenderFile();
        final BlenderFileBlock objectData = object.getObjectData();
        final BlenderFileSdna sdna = blenderFile.getBlenderFileSdna();
        final SDNAStructure meshStructure = sdna.getStructureByName("Mesh", objectData.getPositionOfDataBlockInBlenderFile());
        final BlenderTuple3 meshLocation = new BlenderTuple3(meshStructure.getFieldValue("loc", blenderFile));
        final BlenderTuple3 meshRotation = new BlenderTuple3(meshStructure.getFieldValue("rot", blenderFile));
        final BlenderTuple3 meshScale = new BlenderTuple3(meshStructure.getFieldValue("size", blenderFile));
        final String meshName = (String) ((SDNAStructure) meshStructure.getFieldValue("id", blenderFile)).getFieldValue("name", blenderFile);
        final List<BlenderMeshVertex> meshVertices = BlenderMeshVertexImpl.readList(blenderFile, blenderFile.getBlockByOldMemAddress(meshStructure.getPointerFieldValue("mvert", blenderFile)));
        final List<BlenderMeshFace> meshFaces = BlenderMeshFaceImpl.readList(blenderFile,
                blenderFile.getBlockByOldMemAddress(meshStructure.getPointerFieldValue("mface", blenderFile)));

        //load texture uv sets
        final SDNAStructure faceDataCustomData = (SDNAStructure) meshStructure.getFieldValue("fdata", blenderFile);
        final Map<String, List<MTFace>> texCoordSets = new HashMap<String, List<MTFace>>();
        if(faceDataCustomData != null) {
            final Number faceDataAddress = faceDataCustomData.getPointerFieldValue("layers", blenderFile);
            final List<SDNAStructure> faceDataCustomDataLayers = blenderFile.getBlockByOldMemAddress(faceDataAddress).listStructures("CustomDataLayer");
            for (SDNAStructure customDataLayer : faceDataCustomDataLayers) {
                final String name = (String) customDataLayer.getFieldValue("name", blenderFile);
                final BlenderFileBlock dataBlock = customDataLayer.getPointedBlock("data", blenderFile);
                final Number dataTypeCode = (Number) customDataLayer.getFieldValue("type", blenderFile);
                final CustomDataType dataType = CustomDataType.valueOf(dataTypeCode);
                if(CustomDataType.CD_MTFACE.equals(dataType)) {
                    final List<SDNAStructure> mtFaceStructures = dataBlock.listStructures("MTFace");
                    final List<MTFace> mtFaces = MTFace.listMtFaces(mtFaceStructures, blenderFile);
                    texCoordSets.put(name, mtFaces);
                    assignTexCoordsToFaces(name, meshFaces, mtFaces);
                }
            }
        } else {
            Log.info("no face custom data, load texture uvs from ... ?");
        }

        //load materials
        BlenderFileBlock materialBlock = meshStructure.getPointedBlock("mat", blenderFile);
        BlenderMaterial[] materials = new BlenderMaterial[0];
        if(materialBlock != null) {
            BlenderFileHeader header = blenderFile.getBlenderFileHeader();
            BinaryDataReader reader = blenderFile.getBinaryDataReader();
            reader.jumpTo(materialBlock.getPositionOfDataBlockInBlenderFile());
            int materialCount = materialBlock.getDataSize().intValue() / header.getPointerSize().intValue();
            materials = new BlenderMaterial[materialCount];
            for(int i = 0; i < materialCount; i++) {
                Number pointer = header.nextPointer(reader);
                Number mark = reader.getCurrentPosition();
                BlenderFileBlock mb = blenderFile.getBlockByOldMemAddress(pointer);
                if(mb != null) {
                    materials[i] = new BlenderMaterialImpl(blenderFile, mb);
                }
                reader.jumpTo(mark);
            }
        }

        //parse deform data
        final ArrayList<String> meshDeformGroupNames = blenderObject.getMeshDeformGroupNames();
        final BlenderFileBlock mDeformVert = meshStructure.getPointedBlock("dvert", blenderFile);
        final List<BlenderDeformVertImpl> meshDeformVertList = new ArrayList<BlenderDeformVertImpl>();
        if(mDeformVert != null) {
            
            //1 value for each vertex? let's hope so
            List<SDNAStructure> mdlist = mDeformVert.listStructures("MDeformVert");
            for (int i= 0; i < mdlist.size(); i++) {
                SDNAStructure md = mdlist.get(i);
                Number vertexIndex = i;
                Number flag = md.getNumericFieldValue("flag", blenderFile);
                Number totWeight = md.getNumericFieldValue("totweight", blenderFile);
                BlenderDeformVertImpl vertexDeformData = new BlenderDeformVertImpl(vertexIndex);
                //totweight = number of structures in wdwblock
                BlenderFileBlock dwblock = md.getPointedBlock("dw", blenderFile);
                if(dwblock != null) {
                    List<SDNAStructure> weights = dwblock.listStructures("MDeformWeight");
                    for (SDNAStructure w : weights) {
                        Number def_nr = w.getNumericFieldValue("def_nr", blenderFile);
                        Number weight = w.getNumericFieldValue("weight", blenderFile);
                        String boneName = meshDeformGroupNames.get(def_nr.intValue());
                        BlenderDeformWeightImpl bdw = new BlenderDeformWeightImpl(def_nr, weight, boneName);
                        vertexDeformData.add(bdw);
                    }
                    meshDeformVertList.add(vertexDeformData);
                } else {
                    Log.info("null dw block");
                }
                
            }
        }

        final BlenderMeshImpl mesh = new BlenderMeshImpl(
                blenderObject,
                meshLocation,
                meshRotation,
                meshScale,
                meshName,
                meshVertices,
                meshFaces,
                texCoordSets,
                materials,
                meshDeformVertList);
        blenderObject.addObjectData(mesh);
        scene.getOrCreateLayer(object.getLay()).add(blenderObject);

        //check modifiers
        SDNAStructure objectStructure = object.getStructure();
        SDNAStructure modifiers = (SDNAStructure) objectStructure.getFieldValue("modifiers", blenderFile);
        BlenderListBase modifiersList = new BlenderListBase(modifiers, blenderFile);
        for (BlenderFileBlock modifierBlock : modifiersList.getElements()) {
            parseModifierBlock(blenderObject, modifierBlock, blenderFile);
        }
        return blenderObject;
    }

    private void assignTexCoordsToFaces(String texCoordSetName, List<BlenderMeshFace> meshFaces, List<MTFace> mtFaces) {
        for(int i = 0; i < meshFaces.size(); i++) {
            BlenderMeshFaceImpl face = (BlenderMeshFaceImpl) meshFaces.get(i);
            MTFace texCoordData = mtFaces.get(i);
            face.addTexCoord(texCoordSetName, texCoordData);
        }
    }

    private void parseModifierBlock(BlenderObjectImpl object, BlenderFileBlock block, BlenderFile file) throws IOException {
        String structuresType = block.getStructuresType(file);
        if(structuresType.equals("ArmatureModifierData")) {
            for (SDNAStructure struct : block.listStructures("ArmatureModifierData")) {
                parseArmatureModifierData(object, struct, block,file);
            }
        }
    }

    private void parseArmatureModifierData(BlenderObjectImpl object, SDNAStructure struct, BlenderFileBlock block, BlenderFile file) throws IOException {
        SDNAStructure modifierData = (SDNAStructure) struct.getFieldValue("modifier", file);
        SDNAStructure modifierDataNext = (SDNAStructure) modifierData.getFieldValue("next", file);
        SDNAStructure modifierDataPrev = (SDNAStructure) modifierData.getFieldValue("prev", file);
        BlenderFileBlock armatureObject = struct.getPointedBlock("object", file);
        if(modifierDataNext != null) {
            Log.info("modifier data has next element");
        }
        if(modifierDataPrev != null) {
            Log.info("modifier data has prev element");
        }
        if(armatureObject != null) {
            List<SDNAStructure> objects = armatureObject.listStructures("Object");
            for (SDNAStructure objectStructure : objects) {
                SDNAStructure id = (SDNAStructure) objectStructure.getFieldValue("id", file);
                String name = (String) id.getFieldValue("name", file);
                object.addArmatureObjectName(name);
            }
        }
    }

}
