package com.ardor3d.extension.model.blender;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import it.tukano.blenderfile.elements.BlenderTuple3;

public class MathTypeConversions {

    public Vector3 Vector3(BlenderTuple3 tuple) {
        return new Vector3(tuple.getX().doubleValue(), tuple.getY().doubleValue(), tuple.getZ().doubleValue());
    }

    /* z-y-x */
    public Quaternion Quaternion(BlenderTuple3 t) {
        return new Quaternion().fromEulerAngles(t.getZ().doubleValue(), t.getY().doubleValue(), t.getX().doubleValue());
    }

    /**
     * Instance initializer
     */
    public MathTypeConversions() {
    }

    public ColorRGBA ColorRGBA(BlenderTuple3 rgb) {
        return new ColorRGBA(rgb.getX().floatValue(), rgb.getY().floatValue(), rgb.getZ().floatValue(), 1);
    }
}
