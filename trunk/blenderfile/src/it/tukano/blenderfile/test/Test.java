package it.tukano.blenderfile.test;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.BlenderFileParameters;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMaterial.MapTo;
import it.tukano.blenderfile.elements.BlenderMaterial.TexCo;
import it.tukano.blenderfile.elements.BlenderMesh;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderScene;
import it.tukano.blenderfile.elements.BlenderSceneLayer;
import it.tukano.blenderfile.elements.BlenderObject;
import it.tukano.blenderfile.elements.BlenderTexture;
import it.tukano.blenderfile.elements.BlenderTuple2;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IOException {
        File file = new File("c:\\users\\pgi\\documents\\models\\lightmaptest_2.blend");
        FileInputStream fin = null;
        BlenderFile blenderFile = null;
        try {
            fin = new FileInputStream(file);
            BlenderFileParameters param = new BlenderFileParameters(fin);
            blenderFile = new BlenderFile(param);
        } finally {
            if(fin != null) fin.close();
        }
//        String html = blenderFile.getBlenderFileSdna().createHtmlDescription();
//        PrintStream out = new PrintStream("d:\\blenderfilesdna.html");
//        out.print(html);
//        out.flush();
//        out.close();
        List<BlenderScene> scenes = blenderFile.getScenes();
        for (BlenderScene blenderScene : scenes) {
            for (BlenderSceneLayer blenderSceneLayer : blenderScene.getLayers()) {
                for (BlenderObject blenderObject : blenderSceneLayer.getBlenderObjects()) {
                    if(blenderObject.getType() == BlenderObject.ObjectType.MESH) {
                        writeMeshInfo(blenderObject.getObjectData(BlenderMesh.class).get(0));
                    }
                }
            }
        }
    }

    private static void writeMeshInfo(BlenderMesh m) {
        int mcount = m.getMaterialCount().intValue();
        for (int i= 0; i < mcount; i++) {
            BlenderMaterial material = m.getMaterial(i);
            writeMaterialInfo(material);
        }
    }

    private static void writeTextureInfo(BlenderMesh m) {
        String uv = m.getTexCoordSetNames().get(0);
        for (BlenderMeshTriangle t : m.getTriangles()) {
            BlenderTuple2 t1 = t.getT1(uv);
            BlenderTuple2 t2 = t.getT2(uv);
            BlenderTuple2 t3 = t.getT3(uv);
            System.out.println(t.getV1().getPosition() + "," + t.getV2().getPosition() + "," + t.getV3().getPosition());
            System.out.println(t1 + "," + t2 + "," + t3);
        }
        int matCount = m.getMaterialCount().intValue();
        for (int i = 0; i < matCount; i++) {
            BlenderMaterial material = m.getMaterial(i);
            int texCount = material.getTextureSlotsCount().intValue();
            for (int j= 0; j < texCount; j++) {
                BlenderTexture texture = material.getTexture(j);
                if(texture != null) {
                    System.out.println(texture.getTextureImageName());
                    System.out.println(texture.getBlenderImage());
                    System.out.println(texture.getUVName());
                }
            }
        }
    }

    private static void writeMaterialInfo(BlenderMaterial material) {
        int texcount = material.getTextureSlotsCount().intValue();
        for (int i= 0; i < texcount; i++) {
            BlenderTexture texture = material.getTexture(i);
            if(texture != null) writeTextureInfo(texture);
        }
    }

    private static void writeTextureInfo(BlenderTexture texture) {
        List<MapTo> mapTo = texture.getMapTo();
        TexCo texCo = texture.getTexCo();
        String uVName = texture.getUVName();
        System.out.println(texture.getBlendType());
        System.out.println(mapTo);
        System.out.println(texCo);
        System.out.println(uVName);
    }
}