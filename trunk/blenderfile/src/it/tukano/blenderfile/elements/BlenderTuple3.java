package it.tukano.blenderfile.elements;

/**
 * Well, 3 numbers.
 * @author pgi
 */
public class BlenderTuple3 {

    private final Number x, y, z;

    public BlenderTuple3(Object xyzNumberArray) {
        this((Number[]) xyzNumberArray);
    }

    public BlenderTuple3(Number[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public BlenderTuple3(Number x, Number y, Number z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlenderTuple3(Object x, Object y, Object z) {
        this((Number) x, (Number) y, (Number) z);
    }

    public BlenderTuple3 approximate(int decimalDigits) {
        double scale = decimalDigits == 0 ? 1 : decimalDigits * 10;
        long x = Math.round(getX().doubleValue() * scale);
        long y = Math.round(getY().doubleValue() * scale);
        long z = Math.round(getZ().doubleValue() * scale);
        return new BlenderTuple3(x / scale, y / scale, z / scale);
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

    public BlenderTuple3 toDegrees() {
        return new BlenderTuple3(
                Math.toDegrees(getX().doubleValue()),
                Math.toDegrees(getY().doubleValue()),
                Math.toDegrees(getZ().doubleValue()));
    }

    public BlenderMatrix3 toMatrix() {
        double b = getZ().doubleValue();
        double p = getX().doubleValue();
        double h = getY().doubleValue();
        double cosb = Math.cos(b);
        double cosp = Math.cos(p);
        double cosh = Math.cos(h);
        double sinb = Math.sin(b);
        double sinp = Math.sin(p);
        double sinh = Math.sin(h);
        double m00 = cosh*cosb + sinh*sinp*sinb;
        double m01 = sinb*cosp;
        double m02 = -sinh*cosb + cosh*sinp*sinb;
        double m10 = -cosh*sinb + sinh*sinp*cosb;
        double m11 = cosb*cosp;
        double m12 = sinb*sinh+cosh*sinp*cosb;
        double m20 = sinh*cosp;
        double m21 = -sinp;
        double m22 = cosh*cosp;
        return new BlenderMatrix3(new Number[] {
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22,
        });
    }

    public BlenderTuple3 scale(Number factor) {
        //xxx check number type
        return new BlenderTuple3(
                getX().doubleValue() * factor.doubleValue(),
                getY().doubleValue() * factor.doubleValue(),
                getZ().doubleValue() * factor.doubleValue());
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f, %.6f)", getX().doubleValue(), getY().doubleValue(), getZ().doubleValue());
    }

    public Number getNorm() {
        return Math.sqrt(Math.pow(getX().doubleValue(), 2) + Math.pow(getY().doubleValue(), 2) + Math.pow(getZ().doubleValue(), 2));
    }

    public BlenderTuple3 normalize() {
        double n = getNorm().doubleValue();
        double x = getX().doubleValue() / n;
        double y = getY().doubleValue() / n;
        double z = getZ().doubleValue() / n;
        return new BlenderTuple3(x, y, z);
    }

    public BlenderTuple3 reverseComponents() {
        return new BlenderTuple3(getZ(), getY(), getX());
    }
}
