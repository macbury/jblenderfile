package it.tukano.blenderfile;

import it.tukano.blenderfile.parserstructures.BlenderFileSdna;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.parserstructures.ConstantValues.BlockCode;
import it.tukano.blenderfile.elements.BlenderScene;
import it.tukano.blenderfile.exceptions.BlenderFileParsingException;
import it.tukano.blenderfile.io.MemBufferDataReader;
import it.tukano.blenderfile.io.BinaryDataReader;
import it.tukano.blenderfile.parsers.BlenderFileBlockParser;
import it.tukano.blenderfile.parsers.ParserFactory;
import it.tukano.blenderfile.parserstructures.ConstantValues;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * A blender file holds the parsed content of a .blend file.
 * @author pgi
 */
public class BlenderFile {

    private final BlenderFileHeader blenderFileHeader;
    private final BlenderFileSdna blenderFileSdna;
    private final Map<Number, BlenderFileBlock> blenderFileBlocksForMemAddress;
    private final Map<Number, BlenderFileBlock> blenderFileBlocksForFilePosition;
    private final BinaryDataReader binaryDataReader;
    private List<BlenderScene> parsedScenes;

    /**
     * Initializes this blender file using the given url as input stream
     * @param blenderFileUrl the path of the blender file to load
     * @throws IOException if a read error occurs
     */
    public static BlenderFile newInstance(URL blenderFileUrl) {
        BlenderFile file = null;
        InputStream in = null;
        try {
            in = blenderFileUrl.openStream();
            file = new BlenderFile(new BlenderFileParameters(in));
        } catch(IOException ex) {
            Logger.getLogger(BlenderFile.class.getName()).log(Level.SEVERE, "Error reading blender file", ex);
        } finally {
            if(in != null) try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(BlenderFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return file;
    }

    /**
     * Initializes this blender file from the given input stream
     * @param param the parameters required to parse a file (includes a pointer to
     * the file to be parsed)
     * @throws IOException
     */
    public BlenderFile(BlenderFileParameters param) throws IOException {
        BinaryDataReader reader = new MemBufferDataReader(param.getInputStream());
        if(BlenderFile.isCompressed(reader)) {
            reader = BlenderFile.decompress(reader);
        }
        BlenderFileHeader header = new BlenderFileHeader(reader);
        Map<Number, BlenderFileBlock> blockForMemAddress = new LinkedHashMap<Number, BlenderFileBlock>();
        Map<Number, BlenderFileBlock> blockForFilePosition = new LinkedHashMap<Number, BlenderFileBlock>();
        BlenderFileSdna sdna = null;
        for(BlenderFileBlock block = new BlenderFileBlock(header); ConstantValues.BlockCode.ENDB != block.getCode(); block = new BlenderFileBlock(header)) {
            blockForMemAddress.put(block.getOldMemoryAddress(), block);
            blockForFilePosition.put(block.getPositionInBlenderFile(), block);
            if(block.getCode() == ConstantValues.BlockCode.DNA1) {
                if(sdna != null) throw new BlenderFileParsingException("Found two DNA1 file blocks?...");
                sdna = new BlenderFileSdna(block);
            }
        }
        if (sdna == null) {
            throw new BlenderFileParsingException("No DNA1 file block found.");
        }
        header.setSdna(sdna);
        blenderFileHeader = header;
        blenderFileSdna = sdna;
        blenderFileBlocksForMemAddress = blockForMemAddress;
        binaryDataReader = reader;
        blenderFileBlocksForFilePosition = blockForFilePosition;
        Logger.getLogger(BlenderFile.class.getName()).log(Level.INFO, "BlenderFile V. 0.0.2, .blend version number: {0}", header.getVersionNumber());
    }

    private static boolean isCompressed(BinaryDataReader reader) throws IOException {
        Number mark = reader.getCurrentPosition();
        reader.jumpTo(0);
        final Number ID1 = reader.readByte();
        reader.jumpTo(mark);
        final boolean gzipped = (ID1.intValue() == 0x1f);
        if(gzipped) {
            Logger.getLogger(BlenderFile.class.getName()).log(Level.INFO, "gzip file found (hopefully)...");
        }
        return gzipped;
    }

    private static BinaryDataReader decompress(BinaryDataReader source) throws IOException {
        Logger.getLogger(BlenderFile.class.getName()).log(Level.INFO, "Decompressing gzip...");
        GZIPInputStream in = null;
        try {
            in = new GZIPInputStream(source.asInputStream());
            BufferedInputStream inBuffer = new BufferedInputStream(in);
            File tempFile = File.createTempFile("blenderdecompressed", "data");
            tempFile.deleteOnExit();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(tempFile);
                BufferedOutputStream outBuffer = new BufferedOutputStream(out);
                int read = 0;
                while((read = inBuffer.read()) != -1) outBuffer.write(read);
                outBuffer.flush();
                outBuffer.close();
                return new MemBufferDataReader(new FileInputStream(tempFile));
            } finally {
                if(out != null) out.close();
            }
        } finally {
            if(in != null) in.close();
        }
    }

    /**
     * Returns a list of the BlenderScene contained in the blender file.
     * @return the list of blender scene contained in this file
     * @throws IOException if a read error happens while trying to
     * parse the scene.
     */
    public synchronized List<BlenderScene> getScenes() throws IOException {
        if(parsedScenes == null) {
            parsedScenes = parseScenes();
        }
        return parsedScenes;
    }

    /**
     * Called by getScenes the first time that method is invoked.
     * @return a list of the scenes contained in this blender file
     * @throws IOException if a read error occurs
     */
    private List<BlenderScene> parseScenes() throws IOException {
        List<BlenderScene> scenes = new LinkedList<BlenderScene>();
        ParserFactory fac = ParserFactory.getInstance(this);
        for (BlenderFileBlock blenderFileBlock : blenderFileBlocksForMemAddress.values()) {
            if(blenderFileBlock.getCode() == BlockCode.SC) {
                BlenderFileBlockParser parser = fac.getBlenderFileBlockParser(blenderFileBlock);
                BlenderScene scene = (BlenderScene) parser.parse(this, blenderFileBlock);
                scenes.add(scene);
            }
        }
        return Collections.unmodifiableList(scenes);
    }

    /**
     * Returns the header of this blender file
     * @return the header of the file
     */
    public BlenderFileHeader getBlenderFileHeader() {
        return blenderFileHeader;
    }

    /**
     * Returns the sdna of the blender file
     * @return the sdna of the blender file
     */
    public BlenderFileSdna getBlenderFileSdna() {
        return blenderFileSdna;
    }

    /**
     * Returns the binary data reader used to read contents from this blender file
     * @return the binary data reader for this blender file
     */
    public BinaryDataReader getBinaryDataReader() {
        return binaryDataReader;
    }

    /**
     * Returns the block identified by the given memory address. This can be used
     * to get the data block referred by pointers. At least most of the time...
     * @param oldMemAddress basically the value of a pointer found in some structure
     * @return the blender file block associated witht he given memory address or
     * null if no such block exists.
     */
    public BlenderFileBlock getBlockByOldMemAddress(Number oldMemAddress) {
        return blenderFileBlocksForMemAddress.get(oldMemAddress);
    }

    /**
     * Returns the block at the given file position.
     * @param position the start offset in the blend file of the block to get
     * @return the block at the given position of null if no such block exists.
     */
    public BlenderFileBlock getBlockByFilePosition(Number position) {
        return blenderFileBlocksForFilePosition.get(position);
    }
}
