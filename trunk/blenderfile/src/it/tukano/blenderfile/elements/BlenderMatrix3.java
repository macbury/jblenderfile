package it.tukano.blenderfile.elements;

/**
 * A 3x3 read only matrix.
 * {@code m00, m01, m02}<br/>
 * {@code m10, m11, m22}<br/>
 * {@code m20, m21, m22}<br/>
 * @author pgi
 * {@link http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToEuler/index.htm}
 */
public class BlenderMatrix3 {

    private final Number m00, m01, m02, m10, m11, m12, m20, m21, m22;

    /**
     * Initializes this matrix with the given 9 component array
     * @param mm the 9 values for this matrix
     */
    public BlenderMatrix3(Number[] mm) {
        m00 = mm[0];
        m01 = mm[1];
        m02 = mm[2];
        m10 = mm[3];
        m11 = mm[4];
        m12 = mm[5];
        m20 = mm[6];
        m21 = mm[7];
        m22 = mm[8];
    }

    public BlenderTuple3 toEuler() {
        double h, p, b;
        double sp = -getM12().doubleValue();
        if(sp <= -1) {
            p = -Math.PI / 2;
        } else if(sp >= 1) {
            p = Math.PI / 2;
        } else {
            p = Math.asin(sp);
        }
        if(sp > 0.9999) {
            b = 0;
            h = Math.atan2(-getM20().doubleValue(), getM00().doubleValue());
        } else {
            h = Math.atan2(getM12().doubleValue(), getM22().doubleValue());
        }
        b = Math.atan2(getM10().doubleValue(), getM11().doubleValue());
        return new BlenderTuple3(p, h, b);
    }

    public Number getM00() {
        return m00;
    }

    public Number getM01() {
        return m01;
    }

    public Number getM02() {
        return m02;
    }

    public Number getM10() {
        return m10;
    }

    public Number getM11() {
        return m11;
    }

    public Number getM12() {
        return m12;
    }

    public Number getM20() {
        return m20;
    }

    public Number getM21() {
        return m21;
    }

    public Number getM22() {
        return m22;
    }
}
