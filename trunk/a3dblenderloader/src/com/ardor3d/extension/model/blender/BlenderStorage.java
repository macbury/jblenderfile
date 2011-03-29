package com.ardor3d.extension.model.blender;

import com.ardor3d.scenegraph.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The result of the blender file import procedure.
 * @author pgi
 */
public class BlenderStorage {

    /* One one for each scene in the blender file */
    private final List<Node> SCENE_NODES;

    /**
     * Instance initializer
     */
    protected BlenderStorage(Collection<? extends Node> sceneNodes) {
        SCENE_NODES = new ArrayList<Node>(sceneNodes);
    }

    /**
     * Returns the list of nodes that map the scene found in the blender file
     * @return the list of scene nodes found in the blender file
     */
    public List<Node> getScenes() {
        return Collections.unmodifiableList(SCENE_NODES);
    }
}
