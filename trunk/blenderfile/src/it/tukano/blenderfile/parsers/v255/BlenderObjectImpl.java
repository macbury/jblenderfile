package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderObjectData;
import it.tukano.blenderfile.elements.BlenderScene;
import it.tukano.blenderfile.elements.BlenderSceneLayer;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * BlenderObject implementation
 * @author pgi
 */
public class BlenderObjectImpl implements BlenderObject {

    private String name;
    private BlenderTuple3 location, rotation, scale;
    private ObjectType type;
    private final List<BlenderObjectData> objectDataList = new LinkedList<BlenderObjectData>();
    private final List<String> armatureObjectNames = new LinkedList<String>();
    private BlenderSceneLayer layer;
    private ArrayList<String> meshDeformGroupNames;

    public synchronized ArrayList<String> getMeshDeformGroupNames() {
        return meshDeformGroupNames;
    }
    
    synchronized void setLayer(BlenderSceneLayer layer) {
        this.layer = layer;
    }

    public synchronized BlenderSceneLayer getLayer() {
        return layer;
    }

    public synchronized List<String> getArmatureObjectNames() {
        return Collections.unmodifiableList(armatureObjectNames);
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized BlenderTuple3 getLocation() {
        return location;
    }

    public synchronized void setLocation(BlenderTuple3 location) {
        this.location = location;
    }

    public synchronized BlenderTuple3 getRotation() {
        return rotation;
    }

    public synchronized void setRotation(BlenderTuple3 rotation) {
        this.rotation = rotation;
    }

    public synchronized BlenderTuple3 getScale() {
        return scale;
    }

    public synchronized void setScale(BlenderTuple3 scale) {
        this.scale = scale;
    }

    public synchronized ObjectType getType() {
        return type;
    }

    public synchronized void setType(ObjectType type) {
        this.type = type;
    }

    public synchronized List<BlenderObjectData> getObjectData() {
        return Collections.unmodifiableList(objectDataList);
    }

    public synchronized void addObjectData(BlenderObjectData objectData) {
        objectDataList.add(objectData);
    }

    @Override
    public String toString() {
        return String.format("Object Type %s [%s] LOC[%s] ROT[%s] SCA[%s]", getType(), getName(), getLocation(), getRotation(), getScale());
    }

    synchronized void addArmatureObjectName(String name) {
        armatureObjectNames.add(name);
    }

    public List<BlenderObject> getModifiers(ObjectType type) {
        List<String> arms = getArmatureObjectNames();
        List<BlenderObject> result = new LinkedList<BlenderObject>();
        BlenderScene scene = getLayer().getScene();
        for (BlenderSceneLayer blenderSceneLayer : scene.getLayers()) {
            for (BlenderObject blenderObject : blenderSceneLayer.getBlenderObjects(type)) {
                if(arms.contains(blenderObject.getName())) {
                    result.add(blenderObject);
                }
            }
        }
        return result;
    }

    synchronized void setDeformGroupNames(ArrayList<String> meshDeformGroupNames) {
        this.meshDeformGroupNames = meshDeformGroupNames;
    }

    public <T extends BlenderObjectData> List<T> getObjectData(Class<T> dataType) {
        List<T> result = new LinkedList<T>();
        for (BlenderObjectData blenderObjectData : this.objectDataList) {
            if(dataType.isAssignableFrom(blenderObjectData.getClass())) {
                result.add(dataType.cast(blenderObjectData));
            }
        }
        return result;
    }
}
