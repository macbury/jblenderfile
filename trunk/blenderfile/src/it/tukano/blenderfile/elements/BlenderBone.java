package it.tukano.blenderfile.elements;

import java.util.List;

/**
 * A bone
 * @author pgi
 */
public interface BlenderBone {

    /**
     * Returns the name of this bone
     * @return the name of this bone
     */
    String getName();

    /**
     * Returns the parent of this bone. Can be null if this bone is a root
     * @return the parent of this bone. Can be null.
     */
    BlenderBone getParentBone();

    /**
     * Returns the list of children of this bone
     * @return the children of this bone
     */
    List<? extends BlenderBone> getChildBones();

    /**
     * Returns the position of the head in local coordinate space
     * @return the position of the head
     */
    BlenderTuple3 getLocalSpaceHeadPosition();

    /**
     * Returns the position of the tail in local coordinate space
     * @return the position of the tail in local coordinate space
     */
    BlenderTuple3 getLocalSpaceTailPosition();

    /**
     * Returns the orientation of the bone in local coordinate space
     * @return the orientation of the bone in local coordinate space
     */
    BlenderMatrix3 getLocalSpaceRotation();

    /**
     * Return the location of the bone head in armature space
     * @return the location of the bone head in armature space
     */
    BlenderTuple3 getArmatureSpaceHeadPosition();

    /**
     * Return the location of the bone tail in armature space
     * @return the location of the bone tail in armature space
     */
    BlenderTuple3 getArmatureSpaceTailPosition();

    /**
     * Return the transform matrix of this bone in armature space
     * @return the transform matrix of this bone in armature space
     */
    BlenderMatrix4 getArmatureSpaceMatrix();
}
