package it.tukano.blenderfile.elements;

/**
 * A light
 * @author pgi
 */
public interface BlenderLamp extends BlenderObjectData {

    /**
     * The type of lights
     */
    enum LampType {
        LOCAL(0),
        SUN(1),
        SPOT(2),
        HEMI(3),
        AREA(4),
        YF_PHOTON(5);

        private final int code;

        /**
         * Initializes this light type with the given code
         * @param flag the code of the light
         */
        LampType(int flag) {
            this.code = flag;
        }

        /**
         * Returns the code of this light type
         * @return the code of this light type
         */
        public int getCode() {
            return code;
        }
    }

    /**
     * Returns the type of this light
     * @return the type of this light
     */
    LampType getType();

    /**
     * Returns the name of this light
     * @return the name of this light
     */
    String getName();

    /**
     * Returns the rgb color of this light (3 float in the range [0.0 ... 1.0])
     * @return the rgb color of this light
     */
    BlenderTuple3 getRgb();

    /**
     * Returns the shadow color of this light (3 float in the range [0.0 ... 1.0])
     * @return the shadow color of this light
     */
    BlenderTuple3 getShadowRgb();
}
