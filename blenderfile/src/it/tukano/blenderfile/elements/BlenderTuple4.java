package it.tukano.blenderfile.elements;

/**
 * Guess it, 4 numbers.
 * @author pgi
 */
public class BlenderTuple4 {

    private final Number x, y, z, w;
    
    public BlenderTuple4(Object xyzwNumberArray) {
        this(xyzwNumberArray instanceof Number[] ? (Number[]) xyzwNumberArray : new Number[] {0,0,0,1});
    }

    public BlenderTuple4(Number[] xyzw) {
        this(xyzw[0], xyzw[1], xyzw[2], xyzw[3]);
    }

    public BlenderTuple4(Number x, Number y, Number z, Number w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Number getX() {
        return x;
    }

    public Number getY() {
        return y;
    }

    public Number getZ() {
        return z;
    }

    public Number getW() {
        return w;
    }

    @Override
    public String toString() {
        return "("+getX()+","+getY()+","+getZ()+","+getW()+")";
    }
}
