package it.tukano.blenderfile.parsers;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import java.io.IOException;

/**
 * The type of the block parsers
 * @author pgi
 */
public interface BlenderFileBlockParser {

    /**
     * A block parser that does nothing and returns null.
     */
    BlenderFileBlockParser nullParser = new BlenderFileBlockParser() {

        public Object parse(BlenderFile file, BlenderFileBlock fileBlock) {
            return null;
        }
    };

    /**
     * Transforms a block of the blender file.
     * @param file the blender file the block belongs to
     * @param fileBlock the blender file block to parse
     * @return the result of the parsing process
     * @throws IOException if a read error occurs
     */
    Object parse(BlenderFile file, BlenderFileBlock fileBlock) throws IOException;
}