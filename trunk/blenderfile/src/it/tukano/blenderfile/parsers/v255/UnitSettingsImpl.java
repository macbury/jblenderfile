package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.UnitSettings;

/**
 * Unit settings implementation
 * @author pgi
 */
public class UnitSettingsImpl implements UnitSettings {

    private final System system;
    private final Number scaleLength;

    public UnitSettingsImpl(System system, Number scaleLength) {
        this.system = system;
        this.scaleLength = scaleLength;
    }

    public System getSystem() {
        return system;
    }

    public Number getScaleLength() {
        return scaleLength;
    }

}
