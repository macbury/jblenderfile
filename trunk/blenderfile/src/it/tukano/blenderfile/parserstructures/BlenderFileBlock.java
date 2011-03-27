package it.tukano.blenderfile.parserstructures;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.ConstantValues.BlockCode;
import it.tukano.blenderfile.io.BinaryDataReader;
import it.tukano.blenderfile.io.MemBufferDataReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A blender file block
 * @author pgi
 */
public class BlenderFileBlock {

    private final Number oldMemoryAddress;
    private final Number dataSize;
    private final Number sdnaIndex;
    private final Number structuresCount;
    private final ConstantValues.BlockCode code;
    private final BlenderFileHeader blenderFileHeader;
    private final Number positionInBlenderFile;
    private final Number positionOfDataBlockInBlenderFile;

    /*
     * Initializes this block using the header parameters. The reader of the
     * header is supposed to point at the start offset of a block.
     */
    public BlenderFileBlock(BlenderFileHeader header) throws IOException {
        BinaryDataReader reader = header.getReader();
        positionInBlenderFile = reader.getCurrentPosition();
        String asciiCode = header.nextAsciiString(4, reader).trim();
        dataSize = header.nextUnsignedInteger(reader);
        oldMemoryAddress = header.nextPointer(reader);
        sdnaIndex = header.nextUnsignedInteger(reader);
        structuresCount = header.nextUnsignedInteger(reader);
        code = ConstantValues.BlockCode.valueOf(asciiCode);
        positionOfDataBlockInBlenderFile = reader.getCurrentPosition();
        long newOffset = reader.getCurrentPosition().longValue() + dataSize.longValue();
        reader.jumpTo(newOffset);
        blenderFileHeader = header;
    }

    /**
     * Returns the name of the structures contained in this block.
     * @param file the blender file that this block belongs to
     * @return the name of the structures contained in this block
     */
    public String getStructuresType(BlenderFile file) {
        return file.getBlenderFileSdna().getStructureByIndex(sdnaIndex).getType();
    }

    /**
     * Returns the number of structures that this block contains
     * @return how many structure instances this block contains
     */
    public Number getStructuresCount() {
        return structuresCount;
    }

    /**
     * Returns the position of the data section of this block in the blender file
     * @return the position of the data section of this block in the blender file
     */
    public Number getPositionOfDataBlockInBlenderFile() {
        return positionOfDataBlockInBlenderFile;
    }

    /**
     * Returns the position of this block in the blender file
     * @return the position of this block in the blender file
     */
    public Number getPositionInBlenderFile() {
        return positionInBlenderFile;
    }

    /**
     * Returns the header structure of the blender file of this block.
     * @return the header structure of the blender file of this block.
     */
    public BlenderFileHeader getBlenderFileHeader() {
        return blenderFileHeader;
    }

    /**
     * Returns the original memory address of this block.
     * @return the original memory address of this block.
     */
    public Number getOldMemoryAddress() {
        return oldMemoryAddress;
    }

    /**
     * Returns the code that identifies the type of this block
     * @return the code of this block
     */
    public BlockCode getCode() {
        return code;
    }

    /**
     * Returns the size of the data section of this block
     * @return the size of the data section of this block.
     */
    public Number getDataSize() {
        return dataSize;
    }

    /**
     * Returns a data reader that points to the data section of this block. The
     * reader is independent from the blender file data reader.
     * @return a reader that reads the data section of this block
     * @throws IOException if a read error occurs.
     */
    public BinaryDataReader getSubDataReader() throws IOException {
        BinaryDataReader reader = blenderFileHeader.getReader();
        Number mark = reader.getCurrentPosition();
        BinaryDataReader dataReader = reader.jumpTo(getPositionOfDataBlockInBlenderFile());
        ByteBuffer buffer = ByteBuffer.allocate(getDataSize().intValue());
        dataReader.fill(buffer);
        buffer.flip();
        reader.jumpTo(mark);
        return new MemBufferDataReader(buffer);
    }

    /**
     * Returns a debug reprensentation of this block
     * @return a string describing this block
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("CODE: ").append(getCode()).append(" STRUCTURES: ").append(getStructuresCount()).append(" DATA SIZE: ").append(getDataSize()).
                append(" MEM: ").append(oldMemoryAddress).
                append(" OFF: ").append(this.positionInBlenderFile);
        return buffer.toString();
    }

    /**
     * This method reads the data section of a file block as a list of structures. The structure
     * type is identified by the given name (eg. MVert to get a list of vertices or Mesh to get a list
     * of Mesh and so on)
     * @param structureTypeName the name of the structure type to get (eg Mesh or MCol)
     * @return a list of the structures contained in the data section of this file block
     * @throws IOException if something goes wrong with the io
     */
    public List<SDNAStructure> listStructures(String structureTypeName) throws IOException {
        final List<SDNAStructure> list = new ArrayList<SDNAStructure>(getStructuresCount().intValue());
        final BlenderFileSdna sdna = blenderFileHeader.getSdna();
        final SDNAStructure type = sdna.getStructureByName(structureTypeName, 0);
        final Number structureSize = type.getSize();
        long offset = getPositionOfDataBlockInBlenderFile().longValue();
        for(int i = 0; i < getStructuresCount().intValue(); i++) {
            list.add(sdna.getStructureByName(structureTypeName, offset + structureSize.longValue() * i));
        }
        return list;
    }

    /**
     * Returns the block that comes after this one in the given blender file
     * @param file the blender file of this block
     * @return the block that comes after this one or null if this is the last
     * block in the file.
     */
    public BlenderFileBlock getNextBlock(BlenderFile file) {
        long nextOffset = getPositionOfDataBlockInBlenderFile().longValue() + getDataSize().longValue();
        return file.getBlockByFilePosition(nextOffset);
    }
}
