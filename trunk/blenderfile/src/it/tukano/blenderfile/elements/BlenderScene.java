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
}
