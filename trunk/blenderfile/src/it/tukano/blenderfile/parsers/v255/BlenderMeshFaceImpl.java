package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.Log;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderMeshFace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Blender mesh face implementation
 * @author pgi
 */
public class BlenderMeshFaceImpl implements BlenderMeshFace {

    public static List<BlenderMeshFace> readList(BlenderFile file, BlenderFileBlock block) throws IOException {
        final ArrayList<BlenderMeshFace> faceList = new ArrayList<BlenderMeshFace>();
        if(block != null) {
            final int count = block.getStructuresCount().intValue();
            final List<SDNAStructure> mFaceStructures = block.listStructures("MFace");
            for(int i = 0; i < count; i++) {
                final SDNAStructure mFaceStructure = mFaceStructures.get(i);
                final Number materialIndex = (Number) mFaceStructure.getFieldValue("mat_nr", file);
                final Number edCode = (Number) mFaceStructure.getFieldValue("edcode", file);
                final Number v1 = (Number) mFaceStructure.getFieldValue("v1", file);
                final Number v2 = (Number) mFaceStructure.getFieldValue("v2", file);
                final Number v3 = (Number) mFaceStructure.getFieldValue("v3", file);
                final Number v4 = (Number) mFaceStructure.getFieldValue("v4", file);
                BlenderMeshFaceImpl face = new BlenderMeshFaceImpl(i, materialIndex, v1, v2, v3, v4);
                faceList.add(face);
            }
        } else {
            Log.info("Null BlenderFileBlock");
        }
        return faceList;
    }

    private final Number materialIndex, faceIndex, v1, v2, v3, v4, vCount;
    private Map<String, MTFace> texCoordSet;

    public BlenderMeshFaceImpl(Number faceIndex, Number materialIndex, Number v1, Number v2, Number v3, Number v4) {
        this.materialIndex = materialIndex;
        this.faceIndex = faceIndex;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        vCount = v3.intValue() == 0 ? 2 : v4.intValue() == 0 ? 3 : 4;
    }

    public synchronized void addTexCoord(String texCoordSetName, MTFace texCoordData) {
        if(texCoordSet == null) texCoordSet = new HashMap<String, MTFace>();
        texCoordSet.put(texCoordSetName, texCoordData);
    }

    public Number getVertexCount() {
        return vCount;
    }

    public Number getFaceIndex() {
        return faceIndex;
    }

    public Number getMaterialIndex() {
        return materialIndex;
    }

    public Number getV1Index() {
        return v1;
    }

    public Number getV2Index() {
        return v2;
    }

    public Number getV3Index() {
        return v3;
    }

    public Number getV4Index() {
        return v4;
    }
}
