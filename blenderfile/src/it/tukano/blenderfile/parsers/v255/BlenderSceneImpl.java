package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderSceneLayer;
import it.tukano.blenderfile.elements.UnitSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Blender scene implementation
 * @author pgi
 */
public class BlenderSceneImpl implements it.tukano.blenderfile.elements.BlenderScene {
    private String name;
    private UnitSettings unitSettings;
    private final Map<Number, BlenderSceneLayerImpl> layers = new HashMap<Number, BlenderSceneLayerImpl>();
    private final Map<String, BlenderObjectImpl> uidToObjectMap = new HashMap<String, BlenderObjectImpl>();

    synchronized void setName(String name) {
        this.name = name;
    }

    synchronized void setUnitSettings(UnitSettings unitSettings) {
        this.unitSettings = unitSettings;
    }

    synchronized BlenderSceneLayerImpl getOrCreateLayer(Number index) {
        BlenderSceneLayerImpl layer = layers.get(index);
        if(layer == null) layers.put(index, layer = new BlenderSceneLayerImpl(this, index));
        return layer;
    }

    /**
     * Returns the name of this scene
     * @return the name of this scene
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Returns the name of this scene withouth the SC qualifier
     * @return the unqualified name of this element
     */
    public String getSimpleName() {
        String fullName = getName();
        String simpleName = fullName;
        if(fullName != null && simpleName.startsWith("SC")) {
            simpleName = simpleName.substring(2);
        }
        return simpleName;
    }

    /**
     * Returns the unit settings of this scene
     * @return the unit settings of this scene
     */
    public synchronized UnitSettings getUnitSettings() {
        return unitSettings;
    }

    public synchronized List<BlenderSceneLayer> getLayers() {
        ArrayList<BlenderSceneLayer> copy = new ArrayList<BlenderSceneLayer>(layers.values());
        return copy;
    }

    @Override
    public String toString() {
        return String.format("[%s]", getSimpleName());
    }

    public BlenderObject findObjectWithUnqualifiedName(String string) {
        for (BlenderSceneLayerImpl layer : layers.values()) {
            for (BlenderObject blenderObject : layer.getBlenderObjects()) {
                if(blenderObject.getUnqualifiedName().equals(string)) {
                    return blenderObject;
                }
            }
        }
        return null;
    }

    synchronized Map<String, BlenderObjectImpl> getUidToObjectMap() {
        return uidToObjectMap;
    }

    synchronized void registerBlenderObject(BlenderObjectImpl o) {
        uidToObjectMap.put(o.getName(), o);
    }

    public List<BlenderObject> findSceneRoots() {
        Collection<BlenderSceneLayerImpl> layerElements;
        synchronized (this) {
            layerElements = layers.values();
        }
        List<BlenderObject> roots = new LinkedList<BlenderObject>();
        for (BlenderSceneLayerImpl layer : layerElements) {
            for (BlenderObject object : layer.getBlenderObjects()) {
                if(object.getParent() == null) roots.add(object);
            }
        }
        return roots;
    }
}
