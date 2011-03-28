package it.tukano.blenderfile.elements;

public class BlenderMatrix4 {

    private final Number
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33;

    public BlenderMatrix4(Number[] mm) {
        m00 = mm[0];
        m01 = mm[1];
        m02 = mm[2];
        m03 = mm[3];
        m10 = mm[4];
        m11 = mm[5];
        m12 = mm[6];
        m13 = mm[7];
        m20 = mm[8];
        m21 = mm[9];
        m22 = mm[10];
        m23 = mm[11];
        m30 = mm[12];
        m31 = mm[13];
        m32 = mm[14];
        m33 = mm[15];
    }

    public BlenderMatrix3 getRotation() {
        return new BlenderMatrix3(new Number[] {
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22});
    }

    public BlenderTuple3 getTranslation() {
        return new BlenderTuple3(m30, m31, m32);
    }

    @Override
    public String toString() {
        return String.format("%.4f %.4f %.4f %.4f\n%.4f %.4f %.4f %.4f\n %.4f %.4f %.4f %.4f\n%.4f %.4f %.4f %.4f",
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33);
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

    /**
     * @return the m03
     */
    public Number getM03() {
        return m03;
    }

    /**
     * @return the m13
     */
    public Number getM13() {
        return m13;
    }

    /**
     * @return the m23
     */
    public Number getM23() {
        return m23;
    }

    /**
     * @return the m30
     */
    public Number getM30() {
        return m30;
    }

    /**
     * @return the m31
     */
    public Number getM31() {
        return m31;
    }

    /**
     * @return the m32
     */
    public Number getM32() {
        return m32;
    }

    /**
     * @return the m33
     */
    public Number getM33() {
        return m33;
    }
}
