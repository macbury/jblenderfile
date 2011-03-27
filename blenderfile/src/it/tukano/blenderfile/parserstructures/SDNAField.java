package it.tukano.blenderfile.parserstructures;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A field of a sdna structure
 * @author pgi
 */
public class SDNAField {

    private final String type;
    private final String qualifiedName;
    private final Number typeSize;
    private final Number pointerSize;//used to compute the size of the field if it is a pointer
    private final String simpleName;

    /**
     * Initializes this field
     * @param fieldType the type of the field
     * @param fieldName the name of the field
     * @param fieldSize the size of the type of field
     * @param pointerSize the size of a pointer (used to compute something i don't remember)
     */
    public SDNAField(String fieldType, String fieldName, Number fieldSize, Number pointerSize) {
        type = fieldType;
        qualifiedName = fieldName;
        typeSize = fieldSize;
        this.pointerSize = pointerSize;
        String temp = fieldName.replace("*", "");
        if(temp.indexOf("[") >= 0) {
            temp = temp.substring(0, temp.indexOf("["));
        }
        simpleName = temp;
    }

    /**
     * Returns the name of the type of this field
     * @return the name of the type of this field
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the name of this field with pointer/array modifier tokens
     * @return the qualified name of this field
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * Returns the simple name of this field (no pointer/array tokens)
     * @return the simple name of this field
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Returns the size of this field, counting array size modifiers
     * @return the size of this field.
     */
    public Number getSize() {
        if(getQualifiedName().contains("*")) return pointerSize;
        return typeSize.intValue() * getArrayComponentCount().intValue();
    }

    /**
     * Get the total size of the array (x[1] = 1, x[2][2] = 4)
     * @return the component count of the array
     */
    public Number getArrayComponentCount() {
        String reg = "(\\[(\\d+)\\])";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(qualifiedName);
        int size = 1;
        while (m.find()) {
            int start = m.start(2);
            int end = m.end(2);
            int index = Integer.parseInt(qualifiedName.substring(start, end));
            size *= index;
        }
        return size;
    }

    /**
     * Checks if this is a pointer
     * @return true if this is a pointer
     */
    public boolean isPointer() {
        return getQualifiedName().contains("*");
    }

    /**
     * Checks if this is a float
     * @return true if this is a float
     */
    public boolean isFloat() {
        return getType().equals(ConstantValues.FLOAT_TYPE_NAME);
    }

    /**
     * Checks if this is an int
     * @return true if this is an int
     */
    public boolean isInt() {
        return getType().equals(ConstantValues.INT_TYPE_NAME);
    }

    /**
     * Checks if this is a short
     * @return true if this is a short
     */
    public boolean isShort() {
        return getType().equals(ConstantValues.SHORT_TYPE_NAME);
    }

    /**
     * Checks if this is an array
     * @return true if this is an array
     */
    public boolean isArray() {
        return getQualifiedName().contains("[");
    }

    /**
     * Checks if this is a byte
     * @return true if this is a byte
     */
    public boolean isByte() {
        return getType().equals(ConstantValues.CHAR_TYPE_NAME);
    }

    /**
     * Checks if this is a string
     * @return true if this is a string
     */
    public boolean isString() {
        return isByte() && isArray();
    }

    /**
     * Checks if this is an array of pointers
     * @return true if this is an array of pointers.
     */
    public boolean isArrayOfPointers() {
        return isArray() && isPointer();
    }

    /**
     * Read the value of this field from the binary data reader of the given blender file.
     * The reader should be positioned at the starting offset of this field. If the field value is
     * a structure then the reader of the file is located at the start of that structure, so that
     * values can be read from that structure too.
     * @param file the blender file that contains the value of this field
     * @return the value of this field.<br>
     * if this field is a null pointer (0) returns 0<br>
     * if this field is a non null pointer returns the pointer value as a Number or the pointed structure if the pointer is of structure type<br>
     * if this field is a float, short or int value, returns the numeric value as a Number<br>
     * if this field is an array of float, short or int values, returns an array of Number<br>
     * if this field is of a structure type, returns the structure<br>
     * if this field is a a char array returns the value as a string<br>
     * @throws IOException if a read error occurs
     */
    public Object readValueFrom(BlenderFile file) throws IOException {
        Object value = null;
        BlenderFileHeader blenderFileHeader = file.getBlenderFileHeader();
        BinaryDataReader binaryDataReader = file.getBinaryDataReader();
        if(isArrayOfPointers()) {
            int count = this.getArrayComponentCount().intValue();
            Number[] pointerValues = new Number[count];
            for(int i = 0; i < count; i++) {
                pointerValues[i] = blenderFileHeader.nextPointer(binaryDataReader);
                if(pointerValues[i].longValue() == 0) pointerValues[i] = null;
            }
            value = pointerValues;
        } else if(isPointer()) {
            Number pointerValue = blenderFileHeader.nextPointer(binaryDataReader);
            if(pointerValue.longValue() != 0 && file.getBlenderFileSdna().isStructureType(getType())) {
                BlenderFileBlock pointedBlock = file.getBlockByOldMemAddress(pointerValue);
                value = file.getBlenderFileSdna().getStructureByName(getType(), pointedBlock.getPositionOfDataBlockInBlenderFile());
            } else {
                value = pointerValue.longValue() == 0 ? null : pointerValue;
            }
        } else if(isString()) {//xxx handle char* type
            int len = getSize().intValue();
            ByteBuffer buffer = ByteBuffer.allocate(len);
            while(buffer.hasRemaining()) {
                byte c = binaryDataReader.readByte().byteValue();
                if(c != 0) {
                    buffer.put(c);
                } else {
                    break;
                }
            }
            buffer.flip();
            value = Charset.forName("ascii").decode(buffer).toString().trim();
        } else if (isArray()) {
            value = readArrayValue(blenderFileHeader);
        } else if(isShort()) {
            value = blenderFileHeader.nextShort(binaryDataReader);
        } else if(isFloat()) {
            value = blenderFileHeader.nextFloat(binaryDataReader);
        } else if(isInt()) {
            value = blenderFileHeader.nextInt(binaryDataReader);
        } else if(isByte()) {
            value = binaryDataReader.readByte();
        } else {//a structure?
            value = file.getBlenderFileSdna().getStructureByName(getType(), binaryDataReader.getCurrentPosition());
        }
        return value;
    }

    private Object readArrayValue(BlenderFileHeader header) throws IOException {
        Object value = null;
        if(isShort()) {
            value = readShortArrayValue(header);
        } else if(isFloat()) {
            value = readFloatArrayValue(header);
        } else if(isInt()) {
            value = readIntArrayValue(header);
        } else {
            throw new UnsupportedOperationException(getQualifiedName() + " as array of " + getType() + " not supported yet.");
        }
        return value;
    }

    private Object readShortArrayValue(BlenderFileHeader header) throws IOException {
        BinaryDataReader reader = header.getReader();
        Number mark = reader.getCurrentPosition();
        Number[] array = new Number[getArrayComponentCount().intValue()];
        for (int i = 0; i < array.length; i++) {
            array[i] = header.nextShort(reader);
        }
        reader.jumpTo(mark);
        return array;
    }

    private Object readFloatArrayValue(BlenderFileHeader header) throws IOException {
        BinaryDataReader reader = header.getReader();
        Number mark = reader.getCurrentPosition();
        Number[] array = new Number[getArrayComponentCount().intValue()];
        for (int i = 0; i < array.length; i++) {
            array[i] = header.nextFloat(reader);
        }
        reader.jumpTo(mark);
        return array;
    }

    private Object readIntArrayValue(BlenderFileHeader header) throws IOException {
        BinaryDataReader reader = header.getReader();
        Number mark = reader.getCurrentPosition();
        Number[] array = new Number[getArrayComponentCount().intValue()];
        for (int i = 0; i < array.length; i++) {
            array[i] = header.nextInt(reader);
        }
        reader.jumpTo(mark);
        return array;
    }

    /**
     * Read the value of this field as a pointer
     * @param file the blender file that contains the data
     * @return the pointer numeric value of this field
     * @throws IOException if a read error occurs
     */
    public Number readPointerValueFrom(BlenderFile file) throws IOException {
        return file.getBlenderFileHeader().nextPointer(file.getBinaryDataReader());
    }
}
