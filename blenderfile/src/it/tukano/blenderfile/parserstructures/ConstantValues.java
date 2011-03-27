package it.tukano.blenderfile.parserstructures;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A set of constant values used in the blender files
 * @author pgi
 */
public interface ConstantValues {

    /**
     * The name of the char type
     */
    String CHAR_TYPE_NAME = "char";
    /**
     * The name of the float type
     */
    String FLOAT_TYPE_NAME = "float";
    /**
     * The name of the int type
     */
    String INT_TYPE_NAME = "int";
    /**
     * The name of the short type
     */
    String SHORT_TYPE_NAME = "short";
    /**
     * The SDNA string
     */
    String SDNA_IDENTIFIER = "SDNA";
    /**
     * The NAME string
     */
    String NAME_IDENTIFIER = "NAME";
    /**
     * The TYPE string
     */
    String TYPE_IDENTIFIER = "TYPE";
    /**
     * The TLEN string
     */
    String LENGTH_IDENTIFIER = "TLEN";
    /**
     * The STRC string
     */
    String STRUCTURE_IDENTIFIER = "STRC";
    /**
     * The BLENDER string
     */
    String IDENTIFIER = "BLENDER";
    /**
     * The token that identifies the pointer size in the blender header as a 4
     * bytes pointer
     */
    String POINTER_SIZE_4_BYTES = "_";
    /**
     * The token that identifies the pointer size in the blender header as a 8
     * bytes pointer
     */
    String POINTER_SIZE_8_BYTES = "-";
    /**
     * The token that identifies the byte order as little endian
     */
    String LITTLE_ENDIAN_TOKEN = "v";
    /**
     * The token that identifies the byte order as big endian
     */
    String BIG_ENDIAN_TOKEN = "V";

    /**
     * Wraps the code of a blender file block
     */
    class BlockCode {

        /**
         * The code that identifies the last block of a blender file
         */
        public static final BlockCode ENDB = new BlockCode("ENDB");
        /**
         * The code that identifies the sdna of the blender file
         */
        public static final BlockCode DNA1 = new BlockCode("DNA1");
        /**
         * The code that identifies a scene in the blender file
         */
        public static final BlockCode SC = new BlockCode("SC");
        /**
         * The list of block codes supported by this decoder
         */
        public static final List<BlockCode> values = Collections.unmodifiableList((Arrays.asList(
                ENDB, DNA1, SC)));
        /**
         * Maps ascii strings to block code constants
         */
        public static final Map<String, BlockCode> map = Collections.unmodifiableMap(new HashMap<String, BlockCode>() {

            {
                for (BlockCode blockCode : values) {
                    put(blockCode.getAsciiCode(), blockCode);
                }
            }
        });
        private final String asciiCode;
        private final boolean supported;

        /**
         * Initializes this block code with the given ascii code
         * @param asciiCode the code of the block
         */
        public BlockCode(String asciiCode) {
            this(asciiCode, true);
        }

        /**
         * Initializes this block with the given code and an optional flag to
         * check if the block data need to be parsed on the first pass
         * @param asciiCode the ascii code of the block
         * @param supported true if the block has a dedicated parser
         */
        public BlockCode(String asciiCode, boolean supported) {
            this.asciiCode = asciiCode;
            this.supported = supported;
        }

        /**
         * Returns the hashcode of the block code
         * @return an hashcode for this block code
         */
        @Override
        public int hashCode() {
            return asciiCode.hashCode();
        }

        /**
         * Returns the ascii code of the block
         * @return the ascii code of this block
         */
        public String getAsciiCode() {
            return asciiCode;
        }

        /**
         * Checks if this block has a direct parser.
         * @return true if this block has a direct parser
         */
        public boolean isSupported() {
            return supported;
        }

        @Override
        public String toString() {
            return "[" + getAsciiCode() + "]";
        }

        @Override
        public boolean equals(Object that) {
            return that instanceof BlockCode && ((BlockCode) that).getAsciiCode().equals(this.getAsciiCode());
        }

        /**
         * Returns the BlockCode for the given code value
         * @param code the code read from the blender file
         * @return the BlockCode of that ascii code
         */
        public static BlockCode valueOf(String code) {
            BlockCode supportedCode = map.get(code);
            return supportedCode == null ? new BlockCode(code, false) : supportedCode;
        }
    }

    /**
     * Wraps the code of a CustomData layer
     */
    class CustomDataType {

        /**
         * Custom data of type mesh vertex
         */
        public static final CustomDataType CD_MVERT = new CustomDataType(0);

        /**
         * Custom data of type mesh face
         */
        public static final CustomDataType CD_MFACE = new CustomDataType(4);

        /**
         * Custom data of type mesh face texture coordinates
         */
        public static final CustomDataType CD_MTFACE = new CustomDataType(5);

        /**
         * Custom data of type mesh normals
         */
        public static final CustomDataType CD_NORMAL = new CustomDataType(8);

        /**
         * Custom data of type mesh tangents
         */
        public static final CustomDataType CD_TANGENT = new CustomDataType(18);

        /**
         * Immutable set of predefined CustomDataType instances
         */
        public static final List<CustomDataType> values = Collections.unmodifiableList(Arrays.asList(
                CD_MVERT, CD_MFACE, CD_MTFACE, CD_TANGENT));

        /**
         * Returns the CustomData type that matches the given code
         * @param code the code read from the blender file
         * @return the matching CustomDataType instance of null if no match exists
         */
        public static CustomDataType valueOf(Number code) {
            for (CustomDataType customDataType : values) {
                if (customDataType.getCode() == code.intValue()) {
                    return customDataType;
                }
            }
            return null;
        }

        private final int code;

        /**
         * Initializes this data type with the given code
         * @param code the code of the data type (read from the blender file)
         */
        public CustomDataType(Number code) {
            this.code = code.intValue();
        }

        /**
         * Returns che code of this data type
         * @return an hash code for this instance
         */
        @Override
        public int hashCode() {
            return code;
        }

        /**
         * Checks for equality against another CustomDataType
         * @param obj some reference
         * @return true if obj is a CustomDataType with the same code of this
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CustomDataType other = (CustomDataType) obj;
            if (this.code != other.code) {
                return false;
            }
            return true;
        }

        /**
         * Returns the code of this data type
         * @return the code of this data type
         */
        public int getCode() {
            return code;
        }
    }
}
