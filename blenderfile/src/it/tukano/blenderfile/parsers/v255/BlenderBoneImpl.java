package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderBone;
import it.tukano.blenderfile.elements.BlenderMatrix3;
import it.tukano.blenderfile.elements.BlenderMatrix4;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * BlenderBone data wrapper.
 * @author pgi
 */
public class BlenderBoneImpl implements BlenderBone {
    private String name;
    private Number uid;
    private Number parentUid;
    private Number roll, dist, length, xWidth, zWidth, weight;
    private BlenderTuple3 head, tail, armatureHead, armatureTail;
    private BlenderMatrix3 matrix;
    private BlenderMatrix4 armatureMatrix;
    private List<BlenderBoneImpl> children = new LinkedList<BlenderBoneImpl>();
    private BlenderBoneImpl parent;
    private final Number layer;
    private String nextBoneName, prevBoneName;

    BlenderBoneImpl(Number layer) {
        this.layer = layer;
    }

    synchronized void setNextBoneName(String name) {
        this.nextBoneName = name;
    }

    synchronized void setPrevBoneName(String name) {
        this.prevBoneName = name;
    }

    synchronized String getNextBoneName() {
        return nextBoneName;
    }

    synchronized String getPrevBoneName() {
        return prevBoneName;
    }

    Number getLayer() {
        return layer;
    }

    public synchronized void setParent(BlenderBoneImpl parent) {
        this.parent = parent;
    }

    public synchronized void addChild(BlenderBoneImpl bone) {
        bone.setParent(this);
        children.add(bone);
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    /**
     * @return the uid
     */
    public synchronized Number getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public synchronized void setUid(Number uid) {
        this.uid = uid;
    }

    public synchronized void setParentUid(Number number) {
        parentUid = number;
    }

    public synchronized Number getParentUid() {
        return parentUid;
    }

    public synchronized void setRoll(Number roll) {
        this.roll = roll;
    }

    public synchronized void setHead(BlenderTuple3 head) {
        this.head = head;
    }

    public synchronized void setTail(BlenderTuple3 tail) {
        this.tail = tail;
    }

    public synchronized void setMatrix(BlenderMatrix3 blenderMatrix3) {
        this.matrix = blenderMatrix3;
    }

    public synchronized void setArmatureHead(BlenderTuple3 blenderTuple3) {
        this.armatureHead = blenderTuple3;
    }

    public synchronized void setArmatureTail(BlenderTuple3 blenderTuple3) {
        this.armatureTail = blenderTuple3;
    }

    public synchronized void setArmatureMatrix(BlenderMatrix4 blenderMatrix4) {
        this.armatureMatrix = blenderMatrix4;
    }

    public synchronized void setDist(Number dist) {
        this.dist = dist;
    }

    public synchronized void setWeight(Number weight) {
        this.weight = weight;
    }

    public synchronized void setXWidth(Number xwidth) {
        this.xWidth = xwidth;
    }

    public synchronized void setLength(Number length) {
        this.length = length;
    }

    public synchronized void setZWidth(Number zwidth) {
        this.zWidth = zwidth;
    }

    public synchronized List<? extends BlenderBone> getChildBones() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String toString() {
        return getName() + " - head position: " + getLocalSpaceHeadPosition() + " tail position: " + getLocalSpaceTailPosition() + " rotation: " + getLocalSpaceRotation().toEuler().toDegrees();
    }

    public synchronized BlenderBone getParentBone() {
        return parent;
    }

    public synchronized BlenderTuple3 getLocalSpaceHeadPosition() {
        return head;
    }

    public synchronized BlenderTuple3 getLocalSpaceTailPosition() {
        return tail;
    }

    public synchronized BlenderMatrix3 getLocalSpaceRotation() {
        return matrix;
    }

    public synchronized BlenderTuple3 getArmatureSpaceHeadPosition() {
        return armatureHead;
    }

    public synchronized BlenderTuple3 getArmatureSpaceTailPosition() {
        return armatureTail;
    }

    public synchronized BlenderMatrix4 getArmatureSpaceMatrix() {
        return armatureMatrix;
    }

    public synchronized Number getRoll() {
        return roll;
    }
}
