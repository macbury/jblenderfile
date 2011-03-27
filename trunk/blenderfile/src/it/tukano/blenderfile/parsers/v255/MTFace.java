package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderTuple2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MTFace representation
 * @author pgi
 */
public class MTFace {
    private final Number[] uv;

    public MTFace(SDNAStructure structure, BlenderFile file) throws IOException {
        uv = (Number[]) structure.getFieldValue("uv", file);
    }

    public BlenderTuple2 getUV1() {
        return new BlenderTuple2(uv[0], uv[1]);
    }

    public BlenderTuple2 getUV2() {
        return new BlenderTuple2(uv[2], uv[3]);
    }

    public BlenderTuple2 getUV3() {
        return new BlenderTuple2(uv[4], uv[5]);
    }

    public BlenderTuple2 getUV4() {
        return new BlenderTuple2(uv[6], uv[7]);
    }

    private Number get(int c, int r) {
        return uv[4 * r + c];
    }

    public static List<MTFace> listMtFaces(List<SDNAStructure> mtFaceStructureList, BlenderFile file) throws IOException {
        List<MTFace> faces = new ArrayList<MTFace>(mtFaceStructureList.size());
        for (SDNAStructure struct : mtFaceStructureList) {
            faces.add(new MTFace(struct, file));
        }
        return faces;
    }
}
