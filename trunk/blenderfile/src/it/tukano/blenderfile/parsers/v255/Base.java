package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileSdna;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.io.IOException;

/**
 * Reads the content of a base structure.
 * @author pgi
 */
public class Base {
    
    private final Number next;
    private final Number prev;
    private final Number lay;
    private final Number selcol;
    private final Number flag;
    private final Number startPosition;
    private final Number sx;
    private final Number sy;
    private final Number object;
    private final BlenderFile file;

    public Base(BlenderFile file, Number startPosition) throws IOException {
        BlenderFileSdna sdna = file.getBlenderFileSdna();
        SDNAStructure structure = sdna.getStructureByName("Base", startPosition);
        next = structure.getPointerFieldValue("next", file);
        prev = structure.getPointerFieldValue("prev", file);
        lay = (Number) structure.getFieldValue("lay", file);
        selcol = (Number) structure.getFieldValue("selcol", file);
        flag = (Number) structure.getFieldValue("flag", file);
        sx = (Number) structure.getFieldValue("sx", file);
        sy = (Number) structure.getFieldValue("sy", file);
        object = structure.getPointerFieldValue("object", file);
        this.startPosition = startPosition;
        this.file = file;
    }

    public Base getNext() throws IOException {
        Base base = null;
        if(next != null && next.longValue() != 0) {
            BinaryDataReader reader = file.getBinaryDataReader();
            Number nextBlockData = file.getBlockByOldMemAddress(next).getPositionOfDataBlockInBlenderFile();
            base = new Base(file, nextBlockData);
        }
        return base;
    }

    public ObjectDataWrapper getObject() throws IOException {
        ObjectDataWrapper value = null;
        if(object != null && object.longValue() != 0) {
            BlenderFileBlock objectBlock = file.getBlockByOldMemAddress(object);
            if(objectBlock.getStructuresCount().intValue() != 1) throw new UnsupportedOperationException("1 object expected here");
            value = new ObjectDataWrapper(file, objectBlock.getPositionOfDataBlockInBlenderFile());
        }
        return value;
    }
}
