package it.tukano.blenderfile.elements;

import java.util.List;

/**
 * A blender object contains a mesh, lamp and so on. The object can defined the
 * location of the contained element (as in the case of lamps).
 * @author pgi
 */
public interface BlenderObject {
    /**
     * The type of a blender object.
     */
    enum ObjectType {
        EMPTY(0),
        MESH(1),
        CURVE(2),
        SURF(3),
        FONT(4),
        MBALL(5),
        LAMP(10),
        CAMERA(11),
        LATTICE(22),
        ARMATURE(25);

        private final int code;

        ObjectType(int code) {
            this.code = code;
        }

        public static ObjectType fromCode(int code) {
            for (int i = 0; i < values().length; i++) {
                ObjectType objectType = values()[i];
                if(objectType.code == code) return objectType;
            }
            return null;
        }
    }

    /**
     * Returns the parent of this object, if any
     * @return the parent of this object or null if this object has no parent
     */
    BlenderObject getParent();

    /**
     * Returns the children of this object
     * @return the list of children of this object. Can be empty, never null
     */
    List<? extends BlenderObject> getChildren();

    /**
     * Returns the layer of this object
     * @return the layer of this object
     */
    BlenderSceneLayer getLayer();

    /**
     * Returns the name of this object
     * @return the name of this object
     */
    String getName();

    /**
     * Returns the name of this object withouth the qualifier (ie OB, SC, LA, AR ...)
     * @return the unquaified name of this object
     */
    String getUnqualifiedName();

    /**
     * Returns the location of this object, relative to parent if any
     * @return the location of this object
     */
    BlenderTuple3 getLocation();

    /**
     * Returns the rotation of this object, relative to parent if any
     * @return the rotation of this object
     */
    BlenderTuple3 getRotation();

    /**
     * Returns the scale of this object, relative to parent if any
     * @return the scale of this object
     */
    BlenderTuple3 getScale();

    /**
     * Returns the type of this object
     * @return the type of this object
     */
    ObjectType getType();

    /**
     * Returns the data contained in this object
     * @param <T> the type of the requested data
     * @param dataType the class of the requested data
     * @return the data of the requested type contained in this object. Can be
     * empty, never null. Returns all the data elements whose type is compatible
     * with the given class type.
     */
    <T extends BlenderObjectData> List<T> getObjectData(Class<T> dataType);

    /**
     * Returns a list of the modifiers of the specified type applied to this
     * object
     * @param type the type of the requested modifier.
     * @return a list of the requested modifiers. Can be empty, never null.
     */
    List<BlenderObject> getModifiers(ObjectType type);

    /**
     * Returns the transform matrix of this object, relative to parent if any
     * @return the transform matrix of this object
     */
    BlenderMatrix4 getObjectMatrix();
}