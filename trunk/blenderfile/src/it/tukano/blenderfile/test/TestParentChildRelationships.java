package it.tukano.blenderfile.test;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderScene;
import it.tukano.blenderfile.elements.BlenderSceneLayer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class TestParentChildRelationships {

    public static void main(String[] args) throws MalformedURLException, IOException {
        BlenderFile file = BlenderFile.newInstance(new File("C:\\Users\\pgi\\Documents\\models\\parents.blend").toURI().toURL());
        List<BlenderScene> scenes = file.getScenes();
        for (BlenderScene blenderScene : scenes) {
            List<BlenderSceneLayer> layers = blenderScene.getLayers();
            for (BlenderSceneLayer layer : layers) {
                List<BlenderObject> blenderObjects = layer.getBlenderObjects();
                System.out.println("object count: " + blenderObjects.size());
                for (BlenderObject blenderObject : blenderObjects) {
                    if(blenderObject.getParent() == null) {
                        printBranch(blenderObject, 0);
                    }
                }
            }
        }
    }

    private static void printBranch(BlenderObject parent, int level) {
        StringBuilder tabs = new StringBuilder();
        for (int i= 0; i < level; i++) {
            tabs.append("\t");
        }
        System.out.println(tabs + parent.getName());
        for (BlenderObject child : parent.getChildren()) {
            printBranch(child, level + 1);
        }

    }
}
