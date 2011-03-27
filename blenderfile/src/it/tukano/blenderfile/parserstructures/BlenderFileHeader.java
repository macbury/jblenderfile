package it.tukano.blenderfile.parserstructures;

import it.tukano.blenderfile.exceptions.BlenderFileParsingException;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * The header of a blender file contains info about the byte ordering, pointer size
 * and file version, required by the parser to read the data
 * @author pgi
 */
public class BlenderFileHeader {

    private final Charset ascii = Charset.forName("ascii");

    /* Identifier of a blender file header */
    private final String identifier;

    /* The pointer size token. '_' means 4 bytes, '-' means 8 bytes */
    private final String pointerSizeToken;

    /* The endianness token. 'v' means little endian, 'V' means big endian */
    private final String endiannessToken;

    /* The version number token of the source file */
    private final String versionNumber;

    /* Used to read pointer values from a binary data reader */
    private final ByteBuffer pointerBuffer;

    /* The data reader of the blender file */
    private final BinaryDataReader dataReader;

    private BlenderFileSdna sdna;

    /**
     * Initializes this blender file header from a binary data reader.
     * @param data the source of the data of the blender file
     * @throws IOException if a 
     */
    public BlenderFileHeader(BinaryDataReader data) throws IOException {
        identifier = readAsciiString(7, data);
        if(!ConstantValues.IDENTIFIER.equals(identifier)) {
            throw new BlenderFileParsingException("Unrecognized identifier, expected " + ConstantValues.IDENTIFIER + " found " + identifier);
        }
        pointerSizeToken = readAsciiString(1, data);
        if(ConstantValues.POINTER_SIZE_4_BYTES.equals(pointerSizeToken)) {
            pointerBuffer = ByteBuffer.allocate(4);
        } else if(ConstantValues.POINTER_SIZE_8_BYTES.equals(pointerSizeToken)) {
            pointerBuffer = ByteBuffer.allocate(8);
        } else {
            throw new BlenderFileParsingException("Unrecognized pointer size token: " + pointerSizeToken);
        }
        endiannessToken = readAsciiString(1, data);
        if(ConstantValues.LITTLE_ENDIAN_TOKEN.equals(endiannessToken)) {
            pointerBuffer.order(ByteOrder.LITTLE_ENDIAN);
        } else if(ConstantValues.BIG_ENDIAN_TOKEN.equals(endiannessToken)) {
            pointerBuffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            throw new BlenderFileParsingException("Unrecognized endianness token: " + endiannessToken);
        }
        versionNumber = readAsciiString(3, data);
        dataReader = data;
    }

    /**
     * Returns the version number of the blender file (3 digits)
     * @return the version number of the blender file
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Reads an ascii encoded, fixed length string from the given reader
     * @param charCount how many chars to read
     * @param reader the reader pointed at the start of the string
     * @return the requested string
     * @throws IOException if a io error occurs
     */
    private String readAsciiString(int charCount, BinaryDataReader reader) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(charCount);
        reader.fill(buffer).flip();
        return ascii.decode(buffer).toString();
    }

    /**
     * Read the next ascii string from the reader current position
     * @param charCount how many chars to read
     * @param reader the reader to get data from
     * @return the requested string
     * @throws IOException if a io error occurs
     */
    public String nextAsciiString(int charCount, BinaryDataReader reader) throws IOException {
        return readAsciiString(charCount, reader);
    }

    /**
     * Read the next pointer from the current reader position
     * @param reader the data source
     * @return the requeste pointer
     * @throws IOException if a io error occurs
     */
    public Number nextPointer(BinaryDataReader reader) throws IOException {
        pointerBuffer.clear();
        reader.fill(pointerBuffer).flip();
        switch(pointerBuffer.capacity()) {
            case 4:
                return pointerBuffer.getInt();
            case 8:
                return pointerBuffer.getLong();
            default:
                throw new IOException("Unexpected pointer size: " + pointerBuffer.capacity());
        }
    }

    /**
     * Reads the next unsigned integer from the given reader
     * @param reader the data source
     * @return the requested value
     * @throws IOException if a io error occurs
     */
    public Number nextUnsignedInteger(BinaryDataReader reader) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(pointerBuffer.order());
        reader.fill(buffer);
        buffer.flip();
        long value = buffer.getInt();
        return 0x00000000FFFFFFFF & value;
    }

    /**
     * Returns the reader bound to this header
     * @return the reader bound to this header
     */
    public BinaryDataReader getReader() {
        return dataReader;
    }

    /**
     * Returns the next zero terminating ascii string in the given reader
     * @param reader the data source
     * @return the requested string
     * @throws IOException if a io error occurs
     */
    public String nextZeroedAsciiString(BinaryDataReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for(byte b = reader.readByte().byteValue(); b != 0; b = reader.readByte().byteValue()) {
            buffer.append((char) b);
        }
        return buffer.toString().trim();
    }

    /**
     * Returns the next unsigned short in the reader
     * @param reader the data source
     * @return the requested number
     * @throws IOException if a io error occurs
     */
    public Number nextUnsignedShort(BinaryDataReader reader) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(pointerBuffer.order());
        reader.fill(buffer);
        buffer.flip();
        int value = buffer.getShort();
        return 0x0000FFFF & value;
    }

    /**
     * Returns the size of a pointer (in bytes)
     * @return the size of a pointer (in bytes)
     */
    public Number getPointerSize() {
        return pointerBuffer.capacity();
    }

    /**
     * Returns the next signed short in the reader
     * @param reader the data source
     * @return the requested value
     * @throws IOException if a io error occurs
     */
    public Number nextShort(BinaryDataReader reader) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(pointerBuffer.order());
        reader.fill(buffer);
        buffer.flip();
        return buffer.getShort();
    }

    /**
     * Returns the next signed integer in the reader
     * @param binaryDataReader the data source
     * @return the requested value
     * @throws IOException if a io error occurs
     */
    public Number nextInt(BinaryDataReader binaryDataReader) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(pointerBuffer.order());
        binaryDataReader.fill(buffer);
        buffer.flip();
        return buffer.getInt();
    }

    /**
     * Returns the next floating point number in the reader
     * @param binaryDataReader the data source
     * @return the requested float
     * @throws IOException if a io error occurs
     */
    public Number nextFloat(BinaryDataReader binaryDataReader) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(pointerBuffer.order());
        binaryDataReader.fill(buffer);
        buffer.flip();
        return buffer.getFloat();
    }

    /**
     * Sets the sdna field of this header.
     * @param sdna the sdna to set
     */
    public void setSdna(BlenderFileSdna sdna) {
        this.sdna = sdna;
    }

    /**
     * Returns the sdna field of this header
     * @return the sdna field of this header or null if setSdna has not been
     * called yet (or the passed value was null).
     */
    public BlenderFileSdna getSdna() {
        return sdna;
    }
}
