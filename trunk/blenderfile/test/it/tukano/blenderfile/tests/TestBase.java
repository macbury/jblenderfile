package it.tukano.blenderfile.tests;

import it.tukano.blenderfile.BlenderFile;

public abstract class TestBase {

    protected static BlenderFile blenderFile;

    protected static void loadTestScene(String blendFileName) {
        blenderFile = BlenderFile.newInstance(TestObjectTransform.class.getResource("/testscenes/" + blendFileName));
        if(blenderFile == null) throw new RuntimeException("Cannot load test file");
    }

    /**
     * Instance initializer
     */
    public TestBase() {
    }
}
