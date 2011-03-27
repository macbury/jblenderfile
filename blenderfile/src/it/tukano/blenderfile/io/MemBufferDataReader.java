package it.tukano.blenderfile.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A binary data reader that buffers the data source in memory. This will be
 * replaced by a random access file data reader.
 * @author pgi
 */
public class MemBufferDataReader implements BinaryDataReader {

    /* The data read from the input stream passed to the constructor */
    private final ByteBuffer dataBytes;

    /* The actual position of the read cursor */
    private Number cursor = 0;

    /**
     * Initialize this reader to get data from the given buffer. This is used
     * by file blocks when asked to create sub-readers
     * @param buffer the buffer that contains the data read by this reader
     */
    public MemBufferDataReader(ByteBuffer buffer) {
        dataBytes = buffer;
    }

    /**
     * Initializes this reader to get data from the given stream.
     * @param in the source of bytes for this reader. The stream is not closed
     * by this constructor.
     * @throws IOException if a io error occurs while transfering data from the
     * input stream to memory
     */
    public MemBufferDataReader(InputStream in) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(in);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for(int b = bin.read(); b != -1; b = bin.read()) {
            bout.write(b);
        }
        dataBytes = ByteBuffer.wrap(bout.toByteArray());
    }

    /**
     * Change the offset of the reader to the given position
     * @param position the new position of the reader
     * @return this reader
     */
    public BinaryDataReader jumpTo(Number position) {
        cursor = position;
        return this;
    }

    private ByteBuffer view(int size) {
        dataBytes.clear();
        dataBytes.position(cursor.intValue());
        dataBytes.limit(dataBytes.position() + size);
        return dataBytes.slice();
    }

    /**
     * Transfers bytes from the reader current position to the dest buffer. The
     * amount of bytes transferer is min(x, dest.remaining) where x is the
     * amount of bytes remaining in this reader
     * @param dest the destination of the transferred bytes.
     * @return the dest buffer
     */
    public ByteBuffer fill(ByteBuffer dest) {
        int pos = cursor.intValue();
        dataBytes.clear().position(cursor.intValue());
        while(dest.hasRemaining() && dataBytes.hasRemaining()) {
            dest.put(dataBytes.get());
            pos++;
        }
        cursor = pos;
        return dest;
    }

    /**
     * Returns the current position of the reader
     * @return the current position of the reader
     */
    public Number getCurrentPosition() {
        return cursor;
    }

    /**
     * Reads a byte from the current position
     * @return the requested byte
     */
    public Number readByte() {
        dataBytes.position(cursor.intValue());
        byte b = dataBytes.get();
        cursor = cursor.intValue() + 1;
        return b;
    }

    /**
     * Align the reader position
     * @param size the alignment size
     */
    public void align(int size) {
        int mis = size - (cursor.intValue() % size);
        if(mis != size) {
            cursor = cursor.intValue() + mis;
        }
    }

    /**
     * Returns an input stream on the data of this reader.
     * @return an input stream that can be used to read the same data used by this
     * reader. The stream position starts at the first byte of the underlying array
     * of bytes available to this reader (ie it doesn't take into account the
     * current position of the reader itself).
     */
    public InputStream asInputStream() {
        return new ByteArrayInputStream(dataBytes.array());
    }


}
