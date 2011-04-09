package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.BlenderFile;
import it.tukano.blenderfile.parserstructures.BlenderFileBlock;
import it.tukano.blenderfile.parserstructures.BlenderFileHeader;
import it.tukano.blenderfile.parserstructures.SDNAStructure;
import it.tukano.blenderfile.elements.BlenderMaterial;
import it.tukano.blenderfile.elements.BlenderTexture;
import it.tukano.blenderfile.elements.BlenderTuple3;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blender material
 * @author pgi
 */
public class BlenderMaterialImpl implements BlenderMaterial {

    private final String name;
    private final Type type;
    private final BlenderTuple3 rgb;
    private final BlenderTuple3 specularRgb;
    private final BlenderTuple3 mirrorRgb;
    private final BlenderTuple3 ambientRgb;
    private final Number emit, ang, spectra, rayMirror, alpha, ref, spec, zoffs, add, translucency,
            modeMask, diffShaderMask, specShaderMask, rms, dark, hard;
    private final BlenderTextureImpl[] textures;
    private final Map<Integer, BlenderTexture> textureUnits;
    private final List<Mode> activeModes;

    public BlenderMaterialImpl(BlenderFile file, BlenderFileBlock data) throws IOException {
        BlenderFileHeader header = data.getBlenderFileHeader();
        SDNAStructure structure = data.listStructures("Material").get(0);
        SDNAStructure id = (SDNAStructure) structure.getFieldValue("id", file);
        name = (String) id.getFieldValue("name", file);
        type = new BlenderMaterial.Type((Number) structure.getFieldValue("material_type", file));
        rgb = new BlenderTuple3(structure.getFieldValue("r", file), structure.getFieldValue("g", file), structure.getFieldValue("b", file));
        specularRgb = new BlenderTuple3(structure.getFieldValue("specr", file), structure.getFieldValue("specg", file), structure.getFieldValue("specb", file));
        mirrorRgb = new BlenderTuple3(structure.getFieldValue("mirr", file), structure.getFieldValue("mirg", file), structure.getFieldValue("mirb", file));
        ambientRgb = new BlenderTuple3(structure.getFieldValue("ambr", file), structure.getFieldValue("ambg", file), structure.getFieldValue("ambb", file));
        emit = (Number) structure.getFieldValue("emit", file);
        ang = (Number) structure.getFieldValue("ang", file);
        spectra = (Number) structure.getFieldValue("spectra", file);
        rayMirror = (Number) structure.getFieldValue("ray_mirror", file);
        alpha = (Number) structure.getFieldValue("alpha", file);
        ref = (Number) structure.getFieldValue("ref", file);
        spec = (Number) structure.getFieldValue("spec", file);
        zoffs = (Number) structure.getFieldValue("zoffs", file);
        add = (Number) structure.getFieldValue("add", file);
        rms = (Number) structure.getFieldValue("rms", file);
        translucency = (Number) structure.getFieldValue("translucency", file);
        modeMask = (Number) structure.getFieldValue("mode", file);
        diffShaderMask = (Number) structure.getFieldValue("diff_shader", file);
        specShaderMask = (Number) structure.getFieldValue("spec_shader", file);
        dark = (Number) structure.getFieldValue("darkness", file);
        hard = (Number) structure.getFieldValue("har", file);
        Number[] mtexPointers = (Number[]) structure.getFieldValue("mtex", file);
        BlenderTextureImpl[] mtexArray = new BlenderTextureImpl[mtexPointers.length];
        for (int i = 0; i < mtexPointers.length; i++) {
            Number number = mtexPointers[i];
            if(number != null) {
                BlenderTextureImpl tex = new BlenderTextureImpl(file, file.getBlockByOldMemAddress(number));
                mtexArray[i] = tex;
            }
        }
        this.textures = mtexArray;
        Map<Integer, BlenderTexture> activeTextureUnits = new HashMap<Integer, BlenderTexture>();
        for (int i = 0; i < mtexArray.length; i++) {
            BlenderTextureImpl blenderTextureImpl = mtexArray[i];
            if(blenderTextureImpl != null) activeTextureUnits.put(i, blenderTextureImpl);
        }
        this.textureUnits = Collections.unmodifiableMap(activeTextureUnits);
        this.activeModes = Collections.unmodifiableList(BlenderMaterial.Mode.getActiveModes(modeMask));
    }

    /**
     * Returns the transparency of this material (0, fully transparent -> 1, fully opaque)
     * @return the transparency of this material
     */
    public Number getAlpha() {
        return alpha;
    }

    public Number getSpecFactor() {
        return spec;
    }

    public Number getRmsFactor() {
        return rms;
    }

    public Number getRefFactor() {
        return ref;
    }

    public Number getDarknessFactor() {
        return dark;
    }

    public Number getHardnessFactor() {
        return hard;
    }

    public Type getType() {
        return type;
    }

    public Number getTextureSlotsCount() {
        return textures.length;
    }

    public BlenderTexture getTexture(Number index) {
        return textures[index.intValue()];
    }

    public String getName() {
        return name;
    }

    public BlenderTuple3 getRgb() {
        return rgb;
    }

    public BlenderTuple3 getSpecularRgb() {
        return specularRgb;
    }

    public BlenderTuple3 getMirrorRgb() {
        return mirrorRgb;
    }

    public BlenderTuple3 getAmbientRgb() {
        return ambientRgb;
    }

    public boolean isModeOn(Mode mode) {
        return mode.isOn(modeMask);
    }

    public boolean hasDiffuseShader(DiffuseShader shader) {
        return this.diffShaderMask.intValue() == shader.hashCode();
    }

    public boolean hasSpecularShader(SpecularShader shader) {
        return this.specShaderMask.intValue() == shader.hashCode();
    }

    public Map<Integer, BlenderTexture> getActiveTextureUnits() {
        Map<Integer, BlenderTexture> units = new HashMap<Integer, BlenderTexture>();
        for (Map.Entry<Integer, BlenderTexture> entry : textureUnits.entrySet()) {
            if(entry.getKey() != null && entry.getValue() != null && entry.getValue().getBlenderImage() != null) {
                units.put(entry.getKey(), entry.getValue());
            }
        }
        return units;
    }

    public List<Mode> getActiveModes() {
        return activeModes;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Material Name:").append(name).append("\n");
        buffer.append("Active Modes:").append(getActiveModes()).append("\n");
        buffer.append("Active Texture Units: ").append(getActiveTextureUnits().size()).append("\n");
        return buffer.toString();
    }
}
