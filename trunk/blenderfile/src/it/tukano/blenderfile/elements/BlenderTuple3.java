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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BlenderTuple3) {
            BlenderTuple3 that = (BlenderTuple3) obj;
            return that.getX().equals(this.getX()) &&
                    that.getY().equals(this.getY()) &&
                    that.getZ().equals(this.getZ());
        } else {
            return false;
        }
    }

    public boolean equalsWithinThreshold(BlenderTuple3 that, Number threshold, boolean print) {
        if(print) {
            System.out.println(this + " = " + that);
        }
        return
                Math.abs(that.getX().doubleValue() - this.getX().doubleValue()) < threshold.doubleValue() &&
                Math.abs(that.getY().doubleValue() - this.getY().doubleValue()) < threshold.doubleValue() &&
                Math.abs(that.getZ().doubleValue() - this.getZ().doubleValue()) < threshold.doubleValue();
    }

    public BlenderMatrix3 eulerRadiansToRotationMatrix() {
        float[] eul = {getX().floatValue(), getY().floatValue(), getZ().floatValue() };
	double ci, cj, ch, si, sj, sh, cc, cs, sc, ss;

	ci = Math.cos(eul[0]);
	cj = Math.cos(eul[1]);
	ch = Math.cos(eul[2]);
	si = Math.sin(eul[0]);
	sj = Math.sin(eul[1]);
	sh = Math.sin(eul[2]);
	cc = ci*ch;
	cs = ci*sh;
	sc = si*ch;
	ss = si*sh;

        return new BlenderMatrix3(new Number[] {
            (cj*ch),(cj*sh),-sj,
            (sj*sc-cs),(sj*ss+cc),(cj*si),
            (sj*cc+ss),(sj*cs-sc),(cj*ci)
        });
//	mat[0][0] = (float)(cj*ch);
//	mat[1][0] = (float)(sj*sc-cs);
//	mat[2][0] = (float)(sj*cc+ss);
//	mat[0][1] = (float)(cj*sh);
//	mat[1][1] = (float)(sj*ss+cc);
//	mat[2][1] = (float)(sj*cs-sc);
//	mat[0][2] = (float)-sj;
//	mat[1][2] = (float)(cj*si);
//	mat[2][2] = (float)(cj*ci);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.x != null ? this.x.hashCode() : 0);
        hash = 97 * hash + (this.y != null ? this.y.hashCode() : 0);
        hash = 97 * hash + (this.z != null ? this.z.hashCode() : 0);
        return hash;
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

    public BlenderTuple3 toRadians() {
        return new BlenderTuple3(
                Math.toRadians(getX().doubleValue()),
                Math.toRadians(getY().doubleValue()),
                Math.toRadians(getZ().doubleValue()));
    }
}
