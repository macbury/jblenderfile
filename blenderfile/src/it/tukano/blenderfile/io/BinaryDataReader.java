package it.tukano.blenderfile.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Abstract reader of binary data
 * @author pgi
 */
public interface BinaryDataReader {

    /**
     * Locate the reader cursor to a specific position in the source file. The
     * position is an offset relative to the start of the source.
     * @param position the new position of the read cursor
     * @return a binary reader to read data from the requested position.
     */
    BinaryDataReader jumpTo(Number position) throws IOException;

    /**
     * Returns the current position of the read cursor
     * @return the current position of the read cursor (ie the last jump offset)
     * @throws IOException if a read error occurs
     */
    Number getCurrentPosition() throws IOException;

    /**
     * Transfer data from the reader to the buffer.
     * @param dest where to put the data
     * @return the dest buffer
     * @throws IOException if reading fails for some reason
     */
    ByteBuffer fill(ByteBuffer dest) throws IOException;

    /**
     * Read a single byte from the current position
     * @return the read byte
     * @throws IOException if a read error occurs
     */
    Number readByte() throws IOException;

    /**
     * Sets the aligned position of the cursor
     * @param size the alignment size
     */
    void align(int size);

    /**
     * Returns this reader as a raw input stream
     * @return an InputStream view of the whole data of this stream.
     */
    InputStream asInputStream();
}