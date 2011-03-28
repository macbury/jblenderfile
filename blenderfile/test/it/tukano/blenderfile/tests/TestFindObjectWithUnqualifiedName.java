/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tukano.blenderfile.tests;

import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderScene;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the capability to get an object by name
 * @author pgi
 */
public class TestFindObjectWithUnqualifiedName extends TestBase {

    public TestFindObjectWithUnqualifiedName() {
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
    public void testFindObjects() throws IOException {
        BlenderScene scene = blenderFile.getScenes().get(0);
        BlenderObject cube = scene.findObjectWithUnqualifiedName("Cube");
        BlenderObject cube003 = scene.findObjectWithUnqualifiedName("Cube.003");
        BlenderObject lamp = scene.findObjectWithUnqualifiedName("Lamp");
        BlenderObject noCube = scene.findObjectWithUnqualifiedName("wrong name");
        
        Assert.assertNotNull(cube);
        Assert.assertNotNull(cube003);
        Assert.assertNotNull(lamp);
        Assert.assertNull(noCube);
    }

    @Test
    public void testFindObjectIsCaseSensitive() throws IOException {
        BlenderScene scene = blenderFile.getScenes().get(0);
        BlenderObject noCube = scene.findObjectWithUnqualifiedName("cube");
        Assert.assertNull(noCube);
    }
}