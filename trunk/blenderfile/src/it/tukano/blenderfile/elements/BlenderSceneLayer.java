package it.tukano.blenderfile.elements;

import it.tukano.blenderfile.elements.BlenderObject.ObjectType;
import java.util.List;

/**
 * A layer of a blender scene
 * @author pgi
 */
public interface BlenderSceneLayer {

    /**
     * The index of this layer in the scene
     * @return the index of this layer
     */
    Number getIndex();

    /**
     * The objects contained in this layer
     * @return the content of this layer
     */
    List<BlenderObject> getBlenderObjects();

    /**
     * Returns a list of the objects in this layer that contains a data of
     * the specified type
     * @param dataType the type of the data contained in the objects we want to get
     * @return a list, maybe empty never null, with the requested objects
     */
    List<BlenderObject> getBlenderObjects(ObjectType dataType);

    /**
     * Returns the scene of this layer
     * @return the scene of this layer.
     */
    BlenderScene getScene();
}