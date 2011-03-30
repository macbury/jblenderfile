package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderArmature;
import it.tukano.blenderfile.elements.BlenderMatrix3;
import it.tukano.blenderfile.elements.BlenderMatrix4;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArmatureObjectTransformer implements BlenderObjectTransformer {
    public static class BlenderArmatureList extends LinkedList<BlenderArmature> {}

    public ArmatureObjectTransformer() {
    }

    public BlenderArmatureList transform(ObjectDataWrapper object, BlenderSceneImpl scene) throws IOException {
        BlenderFile file = object.getBlenderFile();
        BlenderFileBlock block = object.getObjectData();
        BlenderArmatureList armatures = new BlenderArmatureList();
        BlenderObjectImpl parsedBlenderObject = object.toBlenderObject(scene);
        for (SDNAStructure struct : block.listStructures("bArmature")) {
            BlenderArmatureImpl armature = new BlenderArmatureImpl();

            SDNAStructure id = (SDNAStructure) struct.getFieldValue("id", file);
            String name = (String) id.getFieldValue("name", file);
            armature.setName(name);
            BlenderFileBlock pointedBlock = struct.getPointedBlock("bonebase", file);
            SDNAStructure boneBase = (SDNAStructure) struct.getFieldValue("bonebase", file);
            BlenderListBase listBase = new BlenderListBase(boneBase, file);
            Map<Number, BlenderBoneImpl> boneMap = new HashMap<Number, BlenderBoneImpl>();
            List<BlenderBoneImpl> roots = new LinkedList<BlenderBoneImpl>();
            ArrayList<BlenderFileBlock> boneBlocks = new ArrayList<BlenderFileBlock>(listBase.getElements());
            //parse the bones
            for(int i = 0; i < boneBlocks.size(); i++) {
                BlenderFileBlock blenderFileBlock = boneBlocks.get(i);
                SDNAStructure boneStruct = blenderFileBlock.listStructures("Bone").get(0);
                BlenderFileBlock parentBlock = boneStruct.getPointedBlock("parent", file);
                String boneName = (String) boneStruct.getFieldValue("name", file);
                Number roll = (Number) boneStruct.getFieldValue("roll", file);
                Number[] head = (Number[]) boneStruct.getFieldValue("head", file);
                Number[] tail = (Number[]) boneStruct.getFieldValue("tail", file);
                Number[] boneMat = (Number[]) boneStruct.getFieldValue("bone_mat", file);
                Number flag = (Number) boneStruct.getFieldValue("flag", file);
                Number[] arm_head = (Number[]) boneStruct.getFieldValue("arm_head", file);
                Number[] arm_tail = boneStruct.getNumericArrayFieldValue("arm_tail", file);
                Number[] arm_mat = boneStruct.getNumericArrayFieldValue("arm_mat", file);
                Number dist = boneStruct.getNumericFieldValue("dist", file);
                Number weight = boneStruct.getNumericFieldValue("weight", file);
                Number xwidth = boneStruct.getNumericFieldValue("xwidth", file);
                Number length = boneStruct.getNumericFieldValue("length", file);
                Number zwidth = boneStruct.getNumericFieldValue("zwidth", file);
                Number ease1 = boneStruct.getNumericFieldValue("ease1", file);
                Number ease2 = boneStruct.getNumericFieldValue("ease2", file);
                Number rad_head = boneStruct.getNumericFieldValue("rad_head", file);
                Number rad_tail = boneStruct.getNumericFieldValue("rad_tail", file);
                Number[] size = boneStruct.getNumericArrayFieldValue("size", file);
                Number layer = boneStruct.getNumericFieldValue("layer", file);
                Number segments = boneStruct.getNumericFieldValue("segments", file);
                SDNAStructure next = null, prev = null;
                BlenderFileBlock nextBlock = boneStruct.getPointedBlock("next", file);
                if(nextBlock != null) next = nextBlock.listStructures("Bone").get(0);
                BlenderFileBlock prevBlock = boneStruct.getPointedBlock("prev", file);
                if(prevBlock != null) prev = prevBlock.listStructures("Bone").get(0);
                
                //System.out.println("Bone " + boneName + " layer " + layer + " flag " + flag);

                BlenderBoneImpl bone = new BlenderBoneImpl(layer);
                if(next != null) bone.setNextBoneName((String) next.getFieldValue("name", file));
                if(prev != null) bone.setPrevBoneName((String) prev.getFieldValue("name", file));
                bone.setUid(blenderFileBlock.getOldMemoryAddress());
                bone.setParentUid(parentBlock != null ? parentBlock.getOldMemoryAddress() : null);
                bone.setRoll(roll);
                bone.setHead(new BlenderTuple3(head));
                bone.setTail(new BlenderTuple3(tail));
                bone.setMatrix(new BlenderMatrix3(boneMat));
                bone.setArmatureHead(new BlenderTuple3(arm_head));
                bone.setArmatureTail(new BlenderTuple3(arm_tail));
                bone.setArmatureMatrix(new BlenderMatrix4(arm_mat));
                bone.setDist(dist);
                bone.setWeight(weight);
                bone.setXWidth(xwidth);
                bone.setLength(length);
                bone.setZWidth(zwidth);
                bone.setName(boneName);
                boneMap.put(bone.getUid(), bone);

                BlenderListBase children = new BlenderListBase((SDNAStructure) boneStruct.getFieldValue("childbase", file), file);
                List<BlenderFileBlock> childBones = children.getElements();
                boneBlocks.addAll(childBones);
            }

            //rebuild the branches and the bone indexed sequence. Hopefully.
            LinkedList<BlenderBoneImpl> sequence = new LinkedList<BlenderBoneImpl>();
            for (BlenderBoneImpl bone : boneMap.values()) {
                if(bone.getParentUid() == null) {
                    roots.add(bone);
                } else {
                    BlenderBoneImpl parent = boneMap.get(bone.getParentUid());
                    parent.addChild(bone);
                }
            }

            armature.setRoots(roots);
            parsedBlenderObject.addObjectData(armature);
        }
        scene.getOrCreateLayer(object.getLay()).add(parsedBlenderObject);
        return armatures;
    }

}
