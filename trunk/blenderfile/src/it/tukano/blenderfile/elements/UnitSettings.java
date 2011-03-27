package it.tukano.blenderfile.elements;

/**
 * Unit settings of a blender scene
 * @author pgi
 */
public interface UnitSettings {

    /**
     * The unit system of a UnitSettings
     */
    enum System {

        /**
         * No system set?
         */
        NONE,

        /**
         * Metric system
         */
        METRIC,

        /**
         * Imperial system
         */
        IMPERIAL;
    }

    /**
     * Returns the system of this unit settings
     * @return the system of this unit settings
     */
    UnitSettings.System getSystem();

    /**
     * Returns the scale factor of this unit settings
     * @return the scale factor of this unit settings
     */
    Number getScaleLength();
}