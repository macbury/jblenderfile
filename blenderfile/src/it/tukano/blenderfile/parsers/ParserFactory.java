package it.tukano.blenderfile.parsers;

import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.ConstantValues.BlockCode;
import it.tukano.blenderfile.parsers.v255.BlenderFileSceneParser;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for block parser instances.
 * @author pgi
 */
public class ParserFactory {

    private static final ParserFactory instance = new ParserFactory();

    /**
     * Returns a shared instance of this factory
     * @param caller the sender of the getInstance request
     * @return a parser factory suitable to handle the requests coming from the
     * given caller.
     */
    public static ParserFactory getInstance(Object caller) {
        return instance;
    }

    private final Map<BlockCode, BlenderFileBlockParser> parsers;

    /**
     * Initializes this parser factory
     */
    protected ParserFactory() {
        Map<BlockCode, BlenderFileBlockParser> map = new HashMap<BlockCode, BlenderFileBlockParser>();
        map.put(BlockCode.SC, new BlenderFileSceneParser());
        parsers = map;
    }

    /**
     * Returns the parser suitable to handle the given block.
     * @param blenderFileBlock the block to parse
     * @return the parser that can handle the transformation of the given block.
     */
    public BlenderFileBlockParser getBlenderFileBlockParser(BlenderFileBlock blenderFileBlock) {
        return parsers.get(blenderFileBlock.getCode());
    }
}
