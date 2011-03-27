package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderTuple2;
import java.util.Collection;
import java.util.Map;

/**
 * Blender mesh triangle
 * @author pgi
 */
public class BlenderMeshTriangleImpl implements BlenderMeshTriangle {
    private final Map<String, BlenderTuple2[]> texCoordSets;
    private final BlenderMeshVertex
            v1,
            v2,
            v3;
    private final BlenderMaterial material;

    public BlenderMeshTriangleImpl(BlenderMeshVertex v1, BlenderMeshVertex v2, BlenderMeshVertex v3, Map<String, BlenderTuple2[]> texCoordSets, BlenderMaterial material) {
        this.texCoordSets = texCoordSets;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.material = material;
    }

    public BlenderMaterial getMaterial() {
        return material;
    }

    public BlenderMeshVertex getV1() {
        return v1;
    }

    public BlenderMeshVertex getV2() {
        return v2;
    }

    public BlenderMeshVertex getV3() {
        return v3;
    }

    public Collection<String> getTexCoordSetNames() {
        return texCoordSets.keySet();
    }

    public BlenderTuple2 getT1(String setName) {
        return getT(setName, 0);
    }

    public BlenderTuple2 getT2(String setName) {
        return getT(setName, 1);
    }

    public BlenderTuple2 getT3(String setName) {
        return getT(setName, 2);
    }

    private BlenderTuple2 getT(String setName, int i) {
        return
                texCoordSets.containsKey(setName) ? texCoordSets.get(setName)[i] : null;
    }

    @Override
    public String toString() {
        return String.format("Triangle: %s %s %s", getV1(), getV2(), getV3());
    }
}
