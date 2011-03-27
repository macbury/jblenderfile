package it.tukano.blenderfile.exceptions;

import java.io.IOException;

/**
 * A IOException throw when some data is successfully read but not understood or
 * expected by the parser
 * @author pgi
 */
public class BlenderFileParsingException extends IOException {

    /**
     * Initializes this exception with the given message
     * @param message the message reported by this exception
     */
    public BlenderFileParsingException(String message) {
        super(message);
    }
}
