package it.tukano.blenderfile.elements;

/**
 * Vertex deform data
 * @author pgi
 */
public interface BlenderDeformWeight {

    /**
     * Returns the name of the applicable bone
     * @return the name of the bone
     */
    String getBoneName();

    /**
     * Returns the deform weight
     * @return the deform weight
     */
    Number getWeight();
}