package it.tukano.blenderfile.elements;

import java.util.List;

/**
 * A scene from the blender file
 * @author pgi
 */
public interface BlenderScene {

    /**
     * The name of this scene
     * @return the name of this scene
     */
    String getName();

    /**
     * The unit settings of this scene
     * @return the measuring unit settings of this scene
     */
    UnitSettings getUnitSettings();

    /**
     * Return a list of the layers in this scene
     * @return the list of layers in this scene
     */
    List<BlenderSceneLayer> getLayers();

    /**
     * Returns the first object with the given unqualified name (that is withouth the OB prefix)
     * @param string the unqualified name of the object (Cube in OBCube, Cube.001 in OBCube.001 and so on)
     * @return the requested object or null if no such object exists
     */
    BlenderObject findObjectWithUnqualifiedName(String string);
}
