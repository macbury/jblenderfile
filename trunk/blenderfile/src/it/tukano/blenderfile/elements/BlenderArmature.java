package it.tukano.blenderfile.elements;

import java.util.List;

/**
 * An armature element.
 * @author pgi
 */
public interface BlenderArmature extends BlenderObjectData, BlenderObjectModifier {

    /**
     * Returns the name of this armature
     * @return the name of this armature
     */
    String getName();

    /**
     * Returns the root bones in this armature.
     * @return the root bones in this armature
     */
    List<? extends BlenderBone> getRootBones();

    /**
     * Returns the bone in this armature that has the given name.
     * @param boneName the name of the bone to get
     * @return the requested bone or null if no such bone exists
     */
    BlenderBone getBoneForName(String boneName);
}
