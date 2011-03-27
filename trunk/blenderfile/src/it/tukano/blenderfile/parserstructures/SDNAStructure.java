package it.tukano.blenderfile.parserstructures;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Structure defined in the sdna of a blender file
 * @author pgi
 */
public class SDNAStructure {
    
    private final String type;
    private final Number size;
    private final SDNAField[] fields;
    private Number startOffsetInBlenderFile;
    private final BlenderFileHeader header;

    public SDNAStructure(String structureType, Number structureSize, SDNAField[] structureFields, BlenderFileHeader header) {
        this.header = header;
        type = structureType;
        size = structureSize;
        fields = Arrays.copyOf(structureFields, structureFields.length);
    }

    /**
     * Get the value of a field that is known to be a pointer.
     * @param fieldSimpleName the name of the field to get (no * )
     * @param file the blender file that contains the data to read
     * @return the value of the pointer field, null for a 0 pointer
     * @throws IOException
     * @throws ClassCastException if the field wasn't a pointer
     */
    public Number getPointerFieldValue(String fieldSimpleName, BlenderFile file) throws IOException, ClassCastException {
        checkStartOffsetDefined();
        SDNAField field = getField(fieldSimpleName, file);
        Number value = null;
        if(field != null) {
            value = field.readPointerValueFrom(file);
        }
        return value;
    }

    /**
     * Read the value of a field of this structure. The reader of the blender file
     * should be located at the starting offset of the structure.
     * @param fieldSimpleName the simple name of the field (ie with no * or [])
     * @param file the blender file with the data to read
     * @return the value of the requested field or null if no such field exists
     * @throws IOException in case of read failure
     */
    public Object getFieldValue(String fieldSimpleName, BlenderFile file) throws IOException {
        Object fieldValue = null;
        SDNAField requestedField = getField(fieldSimpleName, file);
        if(requestedField != null) {
            fieldValue = requestedField.readValueFrom(file);
        }
        return fieldValue;
    }

    /**
     * Read the value of a field as a numeric value.
     * @param fieldSimpleName the name of the field to read
     * @param file the blender file that contains the data to read
     * @return the numeric value of the requested field or null if the field doesn't exist
     * @throws IOException in case of io failure
     */
    public Number getNumericFieldValue(String fieldSimpleName, BlenderFile file) throws IOException {
        return (Number) getFieldValue(fieldSimpleName, file);
    }

    /**
     * Read the value of a field as an array of numbers
     * @param fieldSimpleName the name of the field to read
     * @param file the blender file that contains the data to read
     * @return the value of the field as an array of numbers or null if the field doesn't exist
     * @throws IOException in case of io failure
     */
    public Number[] getNumericArrayFieldValue(String fieldSimpleName, BlenderFile file) throws IOException {
        return (Number[]) getFieldValue(fieldSimpleName, file);
    }

    /**
     * Returns a field by name.
     * @param fieldSimpleName
     * @param file
     * @return the field with the given simple name or null if no such field exists.
     * @throws IOException
     */
    public SDNAField getField(String fieldSimpleName, BlenderFile file) throws IOException {
        checkStartOffsetDefined();
        int offset = 0;
        SDNAField requestedField = null;
        for (int i = 0; i < fields.length && requestedField == null; i++) {
            SDNAField field = fields[i];
            if(field.getSimpleName().equals(fieldSimpleName)) {
                requestedField = field;
            } else {
                offset += field.getSize().intValue();
            }
        }
        if(requestedField != null) {
            BinaryDataReader reader = file.getBinaryDataReader();
            reader.jumpTo(startOffsetInBlenderFile.longValue() + offset);
        }
        return requestedField;
    }

    public String getType() {
        return type;
    }

    public Number getSize() {
        return size;
    }

    public int getFieldCount() {
        return fields.length;
    }

    public SDNAField getField(int index) {
        return fields[index];
    }

    public SDNAStructure setStartingOffset(Number structureStartOffset) {
        SDNAStructure s = new SDNAStructure(getType(), getSize(), fields, header);
        s.startOffsetInBlenderFile = structureStartOffset;
        return s;
    }

    private void checkStartOffsetDefined() {
        if(startOffsetInBlenderFile == null) throw new IllegalStateException("Structure has no start offset");
    }

    public BlenderFileBlock getPointedBlock(String string, BlenderFile blenderFile) throws IOException {
        final Number pointedBlock = getPointerFieldValue(string, blenderFile);
        if(pointedBlock == null || pointedBlock.longValue() == 0) return null;
        return blenderFile.getBlockByOldMemAddress(pointedBlock);
    }
}
