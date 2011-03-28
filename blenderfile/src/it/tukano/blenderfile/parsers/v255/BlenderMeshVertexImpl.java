package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Blender mesh vertex
 * @author pgi
 */
public class BlenderMeshVertexImpl implements BlenderMeshVertex {

    private final Number index, materialIndex, weight, flag;
    private final BlenderTuple3 normal, position;

    public Number getFlag() {
        return flag;
    }

    public Number getWeight() {
        return weight;
    }



    public BlenderMeshVertexImpl(Number index, Number materialIndex, BlenderTuple3 normal, BlenderTuple3 position, Number weight, Number flag) {
        this.weight = weight;
        this.flag = flag;
        this.index = index;
        this.materialIndex = materialIndex;
        this.normal = normal;
        this.position = position;
    }

    public Number getIndex() {
        return index;
    }

    public Number getMaterialIndex() {
        return materialIndex;
    }

    public BlenderTuple3 getNormal() {
        return normal;
    }

    public BlenderTuple3 getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("P: %s, N: %s", getPosition(), getNormal());
    }

    public static ArrayList<BlenderMeshVertex> readList(BlenderFile blenderFile, BlenderFileBlock dataBlock) throws IOException {
        final ArrayList<BlenderMeshVertex> vertices = new ArrayList<BlenderMeshVertex>();
        if(dataBlock != null) {
            final List<SDNAStructure> mVertStructures = dataBlock.listStructures("MVert");
            for(int i = 0; i < mVertStructures.size(); i++) {
                final SDNAStructure mVertStructure = mVertStructures.get(i);
                final BlenderTuple3 position = new BlenderTuple3(mVertStructure.getFieldValue("co", blenderFile));
                BlenderTuple3 normals = new BlenderTuple3(mVertStructure.getFieldValue("no", blenderFile));
                normals = new BlenderTuple3(
                        normals.getX().shortValue() / (float) Short.MAX_VALUE,
                        normals.getY().shortValue() / (float) Short.MAX_VALUE,
                        normals.getZ().shortValue() / (float) Short.MAX_VALUE);
                final Number materialNumber = (Number) mVertStructure.getFieldValue("mat_nr", blenderFile);
                final Number flag = (Number) mVertStructure.getFieldValue("flag", blenderFile);
                final Number bweight = (Number) mVertStructure.getFieldValue("bweight", blenderFile);
                final Number index = i;
                final BlenderMeshVertexImpl vertex = new BlenderMeshVertexImpl(index, materialNumber, normals, position, bweight, flag);
                vertices.add(vertex);
            }
        } else {
            Logger.getLogger(BlenderMeshVertexImpl.class.getName()).log(Level.WARNING, "Null BlenderFiledDataBlock");
        }
        return vertices;
    }


}
