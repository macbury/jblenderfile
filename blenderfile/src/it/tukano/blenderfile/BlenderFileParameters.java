package it.tukano.blenderfile;

import java.io.InputStream;

/**
 * Holds info required to parse a blender file
 * @author pgi
 */
public class BlenderFileParameters {

    private final InputStream inputStream;
    private final boolean closeStream;

    /**
     * Initializes this instance
     * @param in the input stream that holds the content of the blender file
     * @throws IllegalArgumentException if in is null
     */
    public BlenderFileParameters(InputStream in) throws IllegalArgumentException {
        if(in == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        inputStream = in;
        closeStream = true;
    }

    /**
     * Checks if the parser must close the supplied stream
     * @return true if the parser should close the stream, false otherwise
     */
    public boolean getCloseStream() {
        return closeStream;
    }

    /**
     * Returns the input stream that holds the content of the blender file
     * @return the input stream set during construction
     */
    public InputStream getInputStream() {
        return inputStream;
    }
}
