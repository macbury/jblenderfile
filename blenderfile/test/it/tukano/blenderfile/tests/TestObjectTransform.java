/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tukano.blenderfile.tests;

import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderScene;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test scale/rotation/translation of objects
 * @author pgi
 */
public class TestObjectTransform extends TestBase {

    public TestObjectTransform() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        loadTestScene("transform_test_249.blend");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTransformValuesMatchBlenderSceneValues() throws IOException {
        BlenderScene scene = blenderFile.getScenes().get(0);

        BlenderObject cube003 = scene.findObjectWithUnqualifiedName("Cube.003");
        Assert.assertTrue(new BlenderTuple3(15f,-25f,-30f).equalsWithinThreshold(cube003.getRotation().toDegrees(), 0.001, true));
        Assert.assertTrue(new BlenderTuple3(-4f,0f,0f).equalsWithinThreshold(cube003.getLocation(), 0.00001, true));
        Assert.assertTrue(new BlenderTuple3(1.0f,1.0f,1.0f).equalsWithinThreshold(cube003.getScale(), 0.00001, true));

        BlenderObject cube = scene.findObjectWithUnqualifiedName("Cube");
        Assert.assertTrue(new BlenderTuple3(0.0f,0.0f,0.0f).equalsWithinThreshold(cube.getRotation().toDegrees(),0.001, true));
        Assert.assertTrue(new BlenderTuple3(0.0f,0.0f,0.0f).equalsWithinThreshold(cube.getLocation(), 0.00001, true));
        Assert.assertTrue(new BlenderTuple3(1.0f,1.0f,1.0f).equalsWithinThreshold(cube.getScale(), 0.000001, true));

        BlenderObject cube001 = scene.findObjectWithUnqualifiedName("Cube.001");
        checkTransformValues(
                0.0, 3.0, 0.0,
                0.0, 0.0, -10,
                1, 1, 1, cube001);

        checkTransformValues(
                0, 6, 0,
                0, 0, -30,
                1.312, 1.312, 1.312,
                scene.findObjectWithUnqualifiedName("Cube.002"));
    }

    private void checkTransformValues(double locX, double locY, double locZ, double rotX, double rotY, double rotZ, double scaleX, double scaleY, double scaleZ, BlenderObject blenderObject) {
        Assert.assertTrue(blenderObject.getUnqualifiedName(), new BlenderTuple3(rotX, rotY, rotZ).equalsWithinThreshold(blenderObject.getRotation().toDegrees(),0.001, true));
        Assert.assertTrue(blenderObject.getUnqualifiedName(), new BlenderTuple3(locX, locY, locZ).equalsWithinThreshold(blenderObject.getLocation(), 0.001, true));
        Assert.assertTrue(blenderObject.getUnqualifiedName(), new BlenderTuple3(scaleX, scaleY, scaleZ).equalsWithinThreshold(blenderObject.getScale(), 0.001, true));

    }

}