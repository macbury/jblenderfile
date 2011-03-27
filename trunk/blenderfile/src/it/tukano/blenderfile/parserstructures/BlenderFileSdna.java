package it.tukano.blenderfile.parserstructures;

import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.exceptions.BlenderFileParsingException;
import it.tukano.blenderfile.io.BinaryDataReader;
import java.io.IOException;
import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The parsed sdna data of a blender file
 * @author pgi
 */
public class BlenderFileSdna {
    
    private final SDNAStructure[] sdnaStructures;
    private final BlenderFileHeader blenderFileHeader;
    private final Map<String, SDNAStructure> sdnaStructureTypes;

    /**
     * Initializes the sdna reading data from the given block (supposedly of DNA1 type)
     * @param block the DNA1 block of the blender file
     * @throws IOException if a io error occurs
     */
    public BlenderFileSdna(BlenderFileBlock block) throws IOException {
        BlenderFileHeader header = block.getBlenderFileHeader();
        BinaryDataReader reader = header.getReader();
        Number markedPosition = reader.getCurrentPosition();
        reader.jumpTo(block.getPositionOfDataBlockInBlenderFile());
        String sdnaIdentifier = header.nextAsciiString(4, reader);
        if(!ConstantValues.SDNA_IDENTIFIER.equals(sdnaIdentifier)) {
            throw new BlenderFileParsingException("Cannot find sdna identifier (expected " + ConstantValues.SDNA_IDENTIFIER + " found " + sdnaIdentifier + ")");
        }
        String nameIdentifier = header.nextAsciiString(4, reader);
        if(!ConstantValues.NAME_IDENTIFIER.equals(nameIdentifier)) {
            throw new BlenderFileParsingException("Cannot find name identifier (expected " + ConstantValues.NAME_IDENTIFIER + " found " + nameIdentifier + ")");
        }
        int namesCount = header.nextUnsignedInteger(reader).intValue();
        String[] names = new String[namesCount];
        for(int i = 0; i < namesCount; i++) {
            names[i] = header.nextZeroedAsciiString(reader);
        }
        reader.align(4);
        String typeIdentifier = header.nextAsciiString(4, reader);
        if(!ConstantValues.TYPE_IDENTIFIER.equals(typeIdentifier)) {
            throw new BlenderFileParsingException("Cannot find type identifier (expected " + ConstantValues.TYPE_IDENTIFIER + " found " + typeIdentifier + ")");
        }
        int typesCount = header.nextUnsignedInteger(reader).intValue();
        String[] types = new String[typesCount];
        for (int i = 0; i < types.length; i++) {
            types[i] = header.nextZeroedAsciiString(reader);
        }
        reader.align(4);
        String lengthIdentifier = header.nextAsciiString(4, reader);
        if(!ConstantValues.LENGTH_IDENTIFIER.equals(lengthIdentifier)) {
            throw new BlenderFileParsingException("Cannot find length identifier (expected " + ConstantValues.LENGTH_IDENTIFIER + " found " + lengthIdentifier + ")");
        }
        Number[] typeLengths = new Number[typesCount];
        for(int i = 0; i < typesCount; i++) {
            typeLengths[i] = header.nextUnsignedShort(reader);
        }
        reader.align(4);
        String structureIdentifier = header.nextAsciiString(4, reader);
        if(!ConstantValues.STRUCTURE_IDENTIFIER.equals(structureIdentifier)) {
            throw new BlenderFileParsingException("Cannot find structure identifier (expected " + ConstantValues.STRUCTURE_IDENTIFIER + " found " + structureIdentifier + ")");
        }
        Number structuresCount = header.nextUnsignedInteger(reader);
        SDNAStructure[] structures = new SDNAStructure[structuresCount.intValue()];
        for(int i = 0; i < structuresCount.intValue(); i++) {
            Number structureTypeIndex = header.nextUnsignedShort(reader);
            Number structureFieldCount = header.nextUnsignedShort(reader);
            String structureType = types[structureTypeIndex.intValue()];
            Number structureSize = typeLengths[structureTypeIndex.intValue()];
            SDNAField[] fields = new SDNAField[structureFieldCount.intValue()];
            for(int j = 0; j < structureFieldCount.intValue(); j++) {
                Number fieldTypeIndex = header.nextUnsignedShort(reader);
                Number fieldNameIndex = header.nextUnsignedShort(reader);
                String fieldType = types[fieldTypeIndex.intValue()];
                String fieldName = names[fieldNameIndex.intValue()];
                Number fieldSize = typeLengths[fieldTypeIndex.intValue()];
                fields[j] = new SDNAField(fieldType, fieldName, fieldSize, header.getPointerSize());
            }
            structures[i] = new SDNAStructure(structureType, structureSize, fields, header);
        }
        HashMap<String, SDNAStructure> structureMap = new HashMap<String, SDNAStructure>();
        for (int i = 0; i < structures.length; i++) {
            SDNAStructure structure = structures[i];
            structureMap.put(structure.getType(), structure);
        }
        sdnaStructures = structures;
        blenderFileHeader = header;
        sdnaStructureTypes = structureMap;
        reader.jumpTo(markedPosition);
    }

    /**
     * Returns an html document that contains the description of the sdna
     * structures
     * @return a html document with the description of the sdna structures
     */
    public String createHtmlDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><title>");
        buffer.append("blender file sdna version ").append(blenderFileHeader.getVersionNumber());
        buffer.append("</title>");
        buffer.append("<body>");
        buffer.append("<h1>").append("SDNA V.").append(blenderFileHeader.getVersionNumber()).append("</h1>");
        int indexLocation = buffer.length();
        for (int i = 0; i < sdnaStructures.length; i++) {
            SDNAStructure structure = sdnaStructures[i];
            buffer.append("<p>");
            buffer.append("<h2>").append(i).append(") ");
            buffer.append("<a name=\"").append(structure.getType()).append("\"</a>");
            buffer.append(structure.getType());
            buffer.append("</h2>");
            
            for (int j = 0; j < structure.getFieldCount(); j++) {
                SDNAField field = structure.getField(j);
                String fieldType = field.getType();
                if(sdnaStructureTypes.containsKey(fieldType)) {
                    buffer.append("<a href=\"#").append(fieldType).append("\">").append(fieldType).append("</a> ");
                } else {
                    buffer.append(fieldType).append(" ");
                }
                buffer.append(field.getQualifiedName()).
                        append(" <i>(").append(field.getSize()).append(")</i> ").append("</br>");
            }
            buffer.append("</p>");
        }
        buffer.append("</body></html>");
        StringBuilder index = new StringBuilder();
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        Set<String> structureNames = new TreeSet<String>(collator);
        structureNames.addAll(this.sdnaStructureTypes.keySet());
        index.append("<hr>");
        for (String structureName : structureNames) {
            index.append("<a href=\"#").append(structureName).append("\">").append(structureName).append("</a> ");
        }
        index.append("<hr>");
        buffer.insert(indexLocation, index);
        return buffer.toString();
    }

    /**
     * Returns a named structure from the dna catalog. The returned structure is
     * set so that it will read data from the given position.
     * @param structureName the blender name of the structure to get. Eg. "Bone" or
     * "Object" or "bAnimation"
     * @param structureStartOffset the position in the blender file of the first byte
     * of the structure instance.
     * @return a structure set so that it will read the values of an instance located
     * at the given start offset.
     */
    public SDNAStructure getStructureByName(String structureName, Number structureStartOffset) {
        return sdnaStructureTypes.get(structureName).setStartingOffset(structureStartOffset);
    }

    /**
     * Checks if a given name if a recognized structure name for this sdna. Used to
     * distinguish primitive types from structured types.
     * @param typeName the name to check
     * @return true if the type name denotes a structure, false otherwise.
     */
    public boolean isStructureType(String typeName) {
        return sdnaStructureTypes.containsKey(typeName);
    }

    /**
     * Returns a structure given its index in the sdna table
     * @param sdnaIndex the index of the structure to return
     * @return the requested structure.
     */
    public SDNAStructure getStructureByIndex(Number sdnaIndex) {
        return sdnaStructures[sdnaIndex.intValue()];
    }
}
