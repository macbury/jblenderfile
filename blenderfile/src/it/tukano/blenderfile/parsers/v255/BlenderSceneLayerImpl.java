package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderObject.ObjectType;
import it.tukano.blenderfile.elements.BlenderSceneLayer;
import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderScene;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Blender scene layer
 * @author pgi
 */
public class BlenderSceneLayerImpl implements BlenderSceneLayer {
    private final List<BlenderObject> elements = new LinkedList<BlenderObject>();
    private final Number index;
    private final BlenderScene scene;

    public BlenderSceneLayerImpl(BlenderScene scene, Number index) {
        this.scene = scene;
        this.index = index;
    }

    public BlenderScene getScene() {
        return scene;
    }

    <T extends BlenderObjectImpl> T add(T e) {
        synchronized(this) {
            elements.add(e);
        }
        e.setLayer(this);
        return e;
    }

    public Number getIndex() {
        return index;
    }

    public synchronized List<BlenderObject> getBlenderObjects() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public String toString() {
        final int s = getBlenderObjects().size();
        return String.format("[index %s with %s object%s]", getIndex(), getBlenderObjects().size(), s > 1 ? "s" : "");
    }

    public List<BlenderObject> getBlenderObjects(ObjectType dataType) {
        List<BlenderObject> result = new ArrayList<BlenderObject>();
        for (BlenderObject blenderObject : elements) {
            if(blenderObject.getType().equals(dataType)) result.add(blenderObject);
        }
        return result;
    }
}
