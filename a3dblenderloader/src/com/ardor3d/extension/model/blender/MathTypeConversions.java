package com.ardor3d.extension.model.blender;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector3;
import it.tukano.blenderfile.elements.BlenderMatrix3;
import it.tukano.blenderfile.elements.BlenderMatrix4;
import it.tukano.blenderfile.elements.BlenderTuple3;

/**
 * Contains utility methods used to convert math types of the blenderfile project
 * int ardor3d equivalent
 * @author pgi
 */
public class MathTypeConversions {

    /**
     * Helper method used to clamp a value
     * @param value the value to clamp
     * @param min the minium (inclusive) of the range
     * @param max the maximum (inclusive) of the range
     * @return the value clamped to the min-max range
     */
    public static float clamp(float value, float min, float max) {
        value *= 128f;
        return value > max ? max : value < min ? min : value;
    }

    /**
     * Converts a tuple3 into a vector3
     * @param tuple the tuple to convert
     * @return the ardor3d vector equivalent
     */
    public static Vector3 Vector3(BlenderTuple3 tuple) {
        return new Vector3(tuple.getX().doubleValue(), tuple.getY().doubleValue(), tuple.getZ().doubleValue());
    }

    /**
     * Converts a blender file matrix into an ardor3d matrix
     * @param m the matrix to convert
     * @return the ardor3d matrix equivalent
     */
    public static Matrix4 Matrix4(BlenderMatrix4 m) {
        return new Matrix4(
                m.getM00().doubleValue(), m.getM01().doubleValue(), m.getM02().doubleValue(), m.getM03().doubleValue(),
                m.getM10().doubleValue(), m.getM11().doubleValue(), m.getM12().doubleValue(), m.getM13().doubleValue(),
                m.getM20().doubleValue(), m.getM21().doubleValue(), m.getM22().doubleValue(), m.getM23().doubleValue(),
                m.getM30().doubleValue(), m.getM31().doubleValue(), m.getM32().doubleValue(), m.getM33().doubleValue());
    }

    /**
     * Converts a blender matrix3 into an ardor3d matrix
     * @param matrix the matrix to convert
     * @return the ardor3d matrix
     */
    public static Matrix3 Matrix3(BlenderMatrix3 matrix) {
        return new Matrix3(
                matrix.getM00().doubleValue(), matrix.getM01().doubleValue(), matrix.getM02().doubleValue(),
                matrix.getM10().doubleValue(), matrix.getM11().doubleValue(), matrix.getM12().doubleValue(),
                matrix.getM20().doubleValue(), matrix.getM21().doubleValue(), matrix.getM22().doubleValue());
    }

    /**
     * Converts a tuple3 representing a xyz euler set into a matrix3
     * @param eulerRadiansXYZ the angle set to convert
     * @return the ardor3d matrix
     */
    public static Matrix3 Matrix3(BlenderTuple3 eulerRadiansXYZ) {
        return new Matrix3().fromAngles(eulerRadiansXYZ.getX().floatValue(), eulerRadiansXYZ.getY().floatValue(), eulerRadiansXYZ.getZ().floatValue());
    }

    /**
     * Converts a tuple3 representing an rgb color into an ardor3d color
     * @param rgb the rgb tuple to convert
     * @return the ardord3 color
     */
    public static ColorRGBA ColorRGBA(BlenderTuple3 rgb) {
        return new ColorRGBA(rgb.getX().floatValue(), rgb.getY().floatValue(), rgb.getZ().floatValue(), 1);
    }
}
