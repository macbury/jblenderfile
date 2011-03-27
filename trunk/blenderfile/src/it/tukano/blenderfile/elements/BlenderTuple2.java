package it.tukano.blenderfile.elements;

/**
 * A pair of numbers
 * @author pgi
 */
public class BlenderTuple2 {

    private final Number x, y;

    public BlenderTuple2(Number x, Number y) {
        this.x = x;
        this.y = y;
    }

    public Number getX() {
        return x;
    }

    public Number getY() {
        return y;
    }

    public Number getU() {
        return x;
    }

    public Number getV() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%.4f,%.4f)", getX().floatValue(), getY().floatValue());
    }
}
