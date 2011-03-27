package com.ardor3d.extension.model.blender;

import com.ardor3d.scenegraph.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlenderStorage {

    private final List<Node> SCENE_NODES;

    /**
     * Instance initializer
     */
    protected BlenderStorage(Collection<? extends Node> sceneNodes) {
        SCENE_NODES = new ArrayList<Node>(sceneNodes);
    }

    public List<Node> getScenes() {
        return Collections.unmodifiableList(SCENE_NODES);
    }
}
