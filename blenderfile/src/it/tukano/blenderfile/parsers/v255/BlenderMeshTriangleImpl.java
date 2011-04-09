package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderMeshTriangle;
import it.tukano.blenderfile.elements.BlenderMeshVertex;
import it.tukano.blenderfile.elements.BlenderTuple2;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.List;
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

    public void pushVerticesCoordinates(FloatBuffer coordBuffer) {
        push(coordBuffer, v1.getPosition(), v2.getPosition(), v3.getPosition());
    }

    public void pushNormalsCoordinates(FloatBuffer normalBuffer) {
        push(normalBuffer, v1.getNormal(), v2.getNormal(), v3.getNormal());
    }

    public void pushTexCoordSetsCoordinates(List<String> texCoordSetNames, FloatBuffer[] uvBuffer) {
        for (int i = 0; i < uvBuffer.length; i++) {
            final FloatBuffer buffer = uvBuffer[i];
            final String layerName = texCoordSetNames.get(i);
            pushTexCoordSetCoordinates(layerName, buffer);
        }
    }

    private void push(FloatBuffer buffer, BlenderTuple3... tuples) {
        for (int i = 0; i < tuples.length; i++) {
            BlenderTuple3 tuple = tuples[i];
            buffer.put(tuple.getX().floatValue()).put(tuple.getY().floatValue()).put(tuple.getZ().floatValue());
        }
    }

    private void pushTexCoordSetCoordinates(String layerName, FloatBuffer buffer) {
        push(buffer, getT1(layerName), getT2(layerName), getT3(layerName));
    }

    private void push(FloatBuffer buffer, BlenderTuple2... tuples) {
        for (int i = 0; i < tuples.length; i++) {
            BlenderTuple2 tuple = tuples[i];
            buffer.put(tuple.getU().floatValue()).put(tuple.getV().floatValue());
        }
    }
}
