package com.ardor3d.extension.model.blender;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import it.tukano.blenderfile.elements.BlenderMatrix3;
import it.tukano.blenderfile.elements.BlenderMatrix4;
import it.tukano.blenderfile.elements.BlenderTuple3;

public class MathTypeConversions {

    public static Vector3 Vector3(BlenderTuple3 tuple) {
        return new Vector3(tuple.getX().doubleValue(), tuple.getY().doubleValue(), tuple.getZ().doubleValue());
    }

    public static Matrix4 Matrix4(BlenderMatrix4 m) {
        return new Matrix4(
                m.getM00().doubleValue(), m.getM01().doubleValue(), m.getM02().doubleValue(), m.getM03().doubleValue(),
                m.getM10().doubleValue(), m.getM11().doubleValue(), m.getM12().doubleValue(), m.getM13().doubleValue(),
                m.getM20().doubleValue(), m.getM21().doubleValue(), m.getM22().doubleValue(), m.getM23().doubleValue(),
                m.getM30().doubleValue(), m.getM31().doubleValue(), m.getM32().doubleValue(), m.getM33().doubleValue());
    }

    public static Transform Transform(BlenderMatrix4 matrix) {
        Transform t = new Transform().fromHomogeneousMatrix(Matrix4(matrix));
        t.setTranslation(Vector3(matrix.getTranslation()));
        return t;
    }

    public static Matrix3 Matrix3(BlenderMatrix3 matrix) {
        return new Matrix3(
                matrix.getM00().doubleValue(), matrix.getM01().doubleValue(), matrix.getM02().doubleValue(),
                matrix.getM10().doubleValue(), matrix.getM11().doubleValue(), matrix.getM12().doubleValue(),
                matrix.getM20().doubleValue(), matrix.getM21().doubleValue(), matrix.getM22().doubleValue());
    }

    public static Matrix3 Matrix3(BlenderTuple3 eulerRadiansXYZ) {
        return new Matrix3().fromAngles(eulerRadiansXYZ.getX().floatValue(), eulerRadiansXYZ.getY().floatValue(), eulerRadiansXYZ.getZ().floatValue());
    }

    public static ColorRGBA ColorRGBA(BlenderTuple3 rgb) {
        return new ColorRGBA(rgb.getX().floatValue(), rgb.getY().floatValue(), rgb.getZ().floatValue(), 1);
    }
}
