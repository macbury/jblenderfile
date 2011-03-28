package test;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import java.io.IOException;
import java.net.MalformedURLException;

public class TestTransforms {

    /**
     * Instance initializer
     */
    public TestTransforms() {
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
//        double[] eulers = {MathUtils.DEG_TO_RAD * 0, MathUtils.DEG_TO_RAD * 0, MathUtils.DEG_TO_RAD * 30};
//        System.out.printf("Eulers: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);
//        Matrix3 m = new Matrix3().fromAngles(eulers[0], eulers[1], eulers[2]);
//        m.toAngles(eulers);
//        System.out.printf("Matrix Rot: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);
//        Transform t = new Transform();
//        t.setRotation(m);
//        m = new Matrix3(t.getMatrix());
//        m.toAngles(eulers);
//        System.out.printf("Transform Rot: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);
//        Node n = new Node("test");
//        n.setTransform(t);
//        m = new Matrix3(n.getRotation());
//        m.toAngles(eulers);
//        System.out.printf("Node Rot: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);


        double[] eulers = {MathUtils.DEG_TO_RAD * 0, MathUtils.DEG_TO_RAD * 0, MathUtils.DEG_TO_RAD * 30};
        System.out.printf("Eulers: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);
        Quaternion q = new Quaternion().fromEulerAngles(eulers[1], eulers[2], eulers[0]);
        Spatial s = new Node("test");
        s.setRotation(q);
        ReadOnlyMatrix3 matrix = s.getRotation();
        matrix.toAngles(eulers);
        System.out.printf("Rot: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);

//        double[] eulers = {MathUtils.DEG_TO_RAD * 0, MathUtils.DEG_TO_RAD * 0, MathUtils.DEG_TO_RAD * 30};
//        System.out.printf("Eulers: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);
//        Matrix3 q = new Matrix3().fromAngles(eulers[0], eulers[1], eulers[2]);
//        Spatial s = new Node("test");
//        s.setRotation(q);
//        ReadOnlyMatrix3 matrix = s.getRotation();
//        matrix.toAngles(eulers);
//        System.out.printf("Rot: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);

        
        
//        BlenderImporter importer = new BlenderImporter();
//        BlenderStorage storage = importer.load(new URLResourceSource(new File("C:\\Users\\pgi\\Documents\\models\\transform_test.blend").toURI().toURL()));
//        ArrayList<Spatial> spatials = new ArrayList<Spatial>(storage.getScenes());
//        for (int i= 0; i < spatials.size(); i++) {
//            Spatial s = spatials.get(i);
//            ReadOnlyVector3 tra = s.getTranslation();
//            ReadOnlyMatrix3 rot = s.getRotation();
//            ReadOnlyVector3 sca = s.getScale();
//            System.out.println(s.getName());
//            System.out.println("Trans:" + tra);
//            System.out.println("Scale:" +sca);
//            double[] eulers = rot.toAngles(new double[3]);
//            System.out.printf("Rot: %.4f %.4f %.4f%n", MathUtils.RAD_TO_DEG * eulers[0], MathUtils.RAD_TO_DEG * eulers[1], MathUtils.RAD_TO_DEG * eulers[2]);
//            if(s instanceof Node) {
//                spatials.addAll(((Node)s).getChildren());
//            }
//        }
    }
}
