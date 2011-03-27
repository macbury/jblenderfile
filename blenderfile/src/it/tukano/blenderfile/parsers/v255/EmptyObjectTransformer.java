package it.tukano.blenderfile.parsers.v255;

/**
 * Transforms the data of an empty object
 * @author pgi
 */
public class EmptyObjectTransformer implements BlenderObjectTransformer {

    public Object transform(ObjectDataWrapper object, BlenderSceneImpl scene) {
        BlenderObjectImpl emptyObject = object.toBlenderObject();
        scene.getOrCreateLayer(object.getLay()).add(emptyObject);
        return emptyObject;
    }

}
