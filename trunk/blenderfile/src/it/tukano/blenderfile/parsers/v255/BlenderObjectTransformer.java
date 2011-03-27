package it.tukano.blenderfile.parsers.v255;

import java.io.IOException;

/**
 * Transforms an Object structure into a typed structure. The result can be a
 * camera, a mesh, an armature..., depending on the actual blender object that
 * is transformed.
 * @author pgi
 */
public interface BlenderObjectTransformer {

    Object transform(ObjectDataWrapper object, BlenderSceneImpl scene) throws IOException;
}