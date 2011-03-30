package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.elements.BlenderMatrix4;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderObject.ObjectType;
import it.tukano.blenderfile.elements.BlenderTuple3;
import it.tukano.blenderfile.elements.BlenderTuple4;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for the content of an instance of an Object structure.
 * @author pgi
 */
public class ObjectDataWrapper {
    private final ObjectType type;
    private final String name;
    private final Number dataPointer;
    private final BlenderFile file;
    private final Number materialPointer;
    private final BlenderTuple3 loc;
    private final BlenderTuple3 dloc;
    private final BlenderTuple3 orig;
    private final BlenderTuple3 size;
    private final BlenderTuple3 dsize;
    private final BlenderTuple3 rot;
    private final BlenderTuple3 drot;
    private final BlenderTuple4 quat;
    private final BlenderTuple4 dquat;
    private final Number lay;
    private final SDNAStructure structure;
    private final ArrayList<String> meshDeformGroupNames;
    private final BlenderMatrix4 objectMatrix;
    private final String parentObjectName;
    private BlenderObjectImpl blenderObject;

    public ObjectDataWrapper(BlenderFile file, Number startPosition) throws IOException {
        SDNAStructure struct = file.getBlenderFileSdna().getStructureByName("Object", startPosition);
        SDNAStructure id = (SDNAStructure) struct.getFieldValue("id", file);
        name = (String) id.getFieldValue("name", file);
        type = ObjectType.fromCode(((Number) struct.getFieldValue("type", file)).intValue());
        dataPointer = struct.getPointerFieldValue("data", file);
        materialPointer = struct.getPointerFieldValue("mat", file);
        loc = new BlenderTuple3(struct.getFieldValue("loc", file));
        dloc = new BlenderTuple3(struct.getFieldValue("dloc", file));
        orig = new BlenderTuple3(struct.getFieldValue("orig", file));
        size = new BlenderTuple3(struct.getFieldValue("size", file));
        dsize = new BlenderTuple3(struct.getFieldValue("dsize", file));
        rot = new BlenderTuple3(struct.getFieldValue("rot", file));
        drot = new BlenderTuple3(struct.getFieldValue("drot", file));
        quat = new BlenderTuple4(struct.getFieldValue("quat", file));//255
        dquat = new BlenderTuple4(struct.getFieldValue("dquat", file));//255
        lay = (Number) struct.getFieldValue("lay", file);
        SDNAStructure parentObject = (SDNAStructure) struct.getFieldValue("parent", file);
        if(parentObject != null) {
            SDNAStructure parentObjectId = (SDNAStructure) parentObject.getFieldValue("id", file);
            parentObjectName = (String) parentObjectId.getFieldValue("name", file);
        } else {
            parentObjectName = null;
        }
        Number[] obmatrix = (Number[]) struct.getFieldValue("obmat", file);
        if(obmatrix != null) {
            objectMatrix = new BlenderMatrix4(obmatrix);
        } else {
            objectMatrix = null;
        }
        BlenderListBase meshDeformListBase = new BlenderListBase((SDNAStructure) struct.getFieldValue("defbase", file), file);
        List<BlenderFileBlock> meshDeformListBaseBlocks = meshDeformListBase.getElements();
        ArrayList<String> deformGroups = new ArrayList<String>();
        for (BlenderFileBlock bDeformGroupBlock : meshDeformListBaseBlocks) {
            SDNAStructure bDeformGroupStructure = bDeformGroupBlock.listStructures("bDeformGroup").get(0);
            String deformGroupName = (String) bDeformGroupStructure.getFieldValue("name", file);
            deformGroups.add(deformGroupName);
        }
        this.structure = struct;
        this.file = file;
        this.meshDeformGroupNames = deformGroups;
    }

    public SDNAStructure getStructure() {
        return structure;
    }

    public synchronized BlenderObjectImpl toBlenderObject(BlenderSceneImpl scene) {
        return blenderObject == null ? blenderObject = createBlenderObject(scene) : blenderObject;
    }

    private BlenderObjectImpl createBlenderObject(BlenderSceneImpl scene) {
        BlenderObjectImpl o = new BlenderObjectImpl(parentObjectName);
        o.setObjectMatrix(objectMatrix);
        o.setName(name);
        o.setLocation(loc);
        o.setRotation(rot);
        o.setScale(size);
        o.setType(type);
        o.setDeformGroupNames(meshDeformGroupNames);
        scene.registerBlenderObject(o);
        return o;
    }

    public BlenderFile getBlenderFile() {
        return file;
    }

    public BlenderFileBlock getObjectData() {
        BlenderFileBlock block = file.getBlockByOldMemAddress(dataPointer);
        return block;
    }

    public String getName() {
        return name;
    }

    public ObjectType getType() {
        return type;
    }

    public BlenderTuple3 getLoc() {
        return loc;
    }

    public BlenderTuple3 getDloc() {
        return dloc;
    }

    public BlenderTuple3 getOrig() {
        return orig;
    }

    public BlenderTuple3 getSize() {
        return size;
    }

    public BlenderTuple3 getDsize() {
        return dsize;
    }

    public BlenderTuple3 getRot() {
        return rot;
    }

    public BlenderTuple3 getDrot() {
        return drot;
    }

    public BlenderTuple4 getQuat() {
        return quat;
    }

    public BlenderTuple4 getDquat() {
        return dquat;
    }

    public Number getLay() {
        return lay;
    }

    @Override
    public String toString() {
        return String.format("T(%s) N(%s) P%s R%s L(%s)", getType(), getName(), getLoc(), getRot(), getLay());
    }

}
