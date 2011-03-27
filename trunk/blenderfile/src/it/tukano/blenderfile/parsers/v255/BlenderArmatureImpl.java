package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderArmature;
import it.tukano.blenderfile.elements.BlenderBone;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the blender armature.
 * @author pgi
 */
public class BlenderArmatureImpl implements BlenderArmature {

    private String name;
    private List<BlenderBoneImpl> roots;

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized void setRoots(List<BlenderBoneImpl> roots) {
        this.roots = roots;
    }

    public synchronized List<? extends BlenderBone> getRootBones() {
        return Collections.unmodifiableList(roots);
    }

    @Override
    public synchronized String toString() {
        return getName() + " Root Bones: " + roots.size();
    }

    public BlenderBone getBoneForName(String boneName) {
        ArrayList<BlenderBone> stack = new ArrayList<BlenderBone>(getRootBones());
        BlenderBone result = null;
        for(int i = 0; i < stack.size() && result == null; i++) {
            BlenderBone bone = stack.get(i);
            if(bone.getName().equals(boneName)) {
                result = bone;
            } else {
                stack.addAll(bone.getChildBones());
            }
        }
        return result;
    }
}
