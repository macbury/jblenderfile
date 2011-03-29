package it.tukano.blenderfile.elements;

import it.tukano.blenderfile.Log;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A blender material.
 * Check: http://www.blender.org/documentation/249PythonDoc/Material-module.html#Shaders
 * @author pgi
 */
public interface BlenderMaterial {

    /**
     * Texture blend values
     */
    class BlendType {

        public static final BlendType BLEND = new BlendType(0, "BLEND");
        public static final BlendType MUL = new BlendType(1, "MUL");
        public static final BlendType ADD = new BlendType(2, "ADD");
        public static final BlendType SUB = new BlendType(3, "SUB");
        public static final BlendType DIV = new BlendType(4, "DIV");
        public static final BlendType DARK = new BlendType(5, "DARK");
        public static final BlendType DIFF = new BlendType(6, "DIFF");
        public static final BlendType LIGHT = new BlendType(7, "LIGHT");
        public static final BlendType SCREEN = new BlendType(8, "SCREEN");
        public static final BlendType OVERLAY = new BlendType(9, "OVERLAY");
        public static final BlendType BLEND_HUE = new BlendType(10, "BLEND_HUE");
        public static final BlendType BLEND_SAT = new BlendType(11, "BLEND_SAT");
        public static final BlendType BLEND_VAL = new BlendType(12, "BLEND_VAL");
        public static final BlendType BLEND_COLOR = new BlendType(13, "BLEND_COLOR");
        public static final BlendType NUM_BLENDTYPES = new BlendType(14, "NUM_BLENDTYPES");
        public static final BlendType SOFT_LIGHT = new BlendType(15, "SOFT_LIGHT");
        public static final BlendType LIN_LIGHT = new BlendType(16, "LIN_LIGHT");
        public static final List<BlendType> values = Collections.<BlendType>unmodifiableList(Arrays.<BlendType>asList(
                BLEND, MUL, ADD, SUB, DIV, DARK, DIFF, LIGHT, SCREEN, OVERLAY, BLEND_HUE,
                BLEND_SAT, BLEND_VAL, BLEND_COLOR, NUM_BLENDTYPES, SOFT_LIGHT, LIN_LIGHT));

        public static BlendType valueOf(Number value) {
            for (BlendType blendType : values) {
                if (value.intValue() == blendType.value.intValue()) {
                    return blendType;
                }
            }
            return new BlendType(value, "Unsupported:" + value);
        }
        private final Number value;
        private final String name;

        public BlendType(Number value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public String toString() {
            return "BlendType." + name;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BlendType && ((BlendType) o).value.equals(value);
        }
    }

    /**
     * Texture mapping values
     */
    class MapTo {

        private final Number flag;
        private final String name;

        /**
         * Initializes this map with the given flag value
         * @param flag
         */
        public MapTo(Number flag, String name) {
            this.flag = flag;
            this.name = name;
        }

        /**
         * Checks if this flag is active on the given mask
         * @param mask the mask to check this flag against
         * @return true if the flag is on on the given mask
         */
        public boolean isOn(Number mask) {
            return ((mask.longValue() & flag.longValue()) == flag.longValue());
        }

        @Override
        public final int hashCode() {
            return flag.intValue();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MapTo other = (MapTo) obj;
            if (this.flag != other.flag && (this.flag == null || !this.flag.equals(other.flag))) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "MapTo." + name + " Code: " + flag;
        }
        /**
         * The texture is mapped to the color of the surface
         */
        public static final MapTo COL = new MapTo(1, "COL");
        /**
         * The texture defines the normals of the surface
         */
        public static final MapTo NORM = new MapTo(2, "NORM");
        /**
         * The texture defines the specular color of the surface
         */
        public static final MapTo COLSPEC = new MapTo(4, "COLSPEC");
        /**
         * The texture defines the mirror color of the surface
         */
        public static final MapTo COLMIR = new MapTo(8, "COLMIR");
        /**
         * todo find this
         */
        public static final MapTo VARS = new MapTo(0xFFF0, "VARS");
        /**
         * The texture defines the diffuse reflectivity value of the surface
         */
        public static final MapTo REF = new MapTo(16, "REF");
        /**
         * The texture defines the specularity value of the surface
         */
        public static final MapTo SPEC = new MapTo(32, "SPEC");
        /**
         * The texture defines the emission color of the surface (maybe)
         */
        public static final MapTo EMIT = new MapTo(64, "EMIT");
        /**
         * The texture defines the transparency of the surface
         */
        public static final MapTo ALPHA = new MapTo(128, "ALPHA");
        /**
         * The texture defines the hardness of the surface (color?)
         */
        public static final MapTo HARDNESS = new MapTo(256, "HARDNESS");
        /**
         * The texture defines the mirror reflectivity of the surface
         */
        public static final MapTo RAYMIRR = new MapTo(512, "RAYMIRR");
        /**
         * The texture defines the translucency of the surface
         */
        public static final MapTo TRANSLU = new MapTo(1024, "TRANSLU");
        /**
         * The texture defines the ambient color of the surface
         */
        public static final MapTo AMB = new MapTo(2048, "AMB");
        /**
         * The texture defines the displacement of the surface
         */
        public static final MapTo DISPLACE = new MapTo(4096, "DISPLACE");
        /**
         * The texture transforms the coordinates of the following textures
         */
        public static final MapTo WARP = new MapTo(8192, "WARP");
        /**
         * todo
         */
        public static final MapTo LAYER = new MapTo(16384, "LAYER");
        public static final List<MapTo> values = Collections.unmodifiableList(Arrays.asList(
                ALPHA, AMB, COL, COLMIR, COLSPEC, DISPLACE, EMIT, HARDNESS, LAYER, NORM,
                RAYMIRR, REF, SPEC, TRANSLU, VARS, WARP));

        public static MapTo valueOf(Number code) {
            for (MapTo mapTo : values) {
                if (mapTo.flag.intValue() == code.intValue()) {
                    return mapTo;
                }
            }
            if (code.intValue() == 2049) {
                return MapTo.AMB;//? got this....
            }
            System.out.println("no mapto constant for code: " + code);
            return null;
        }
    }

    /**
     * Texture coordinate generation/mapping mode
     */
    class TexCo {

        private final Number flag;

        /**
         * Initializes this texco intance with the given flag
         * @param flag the flag of this texco value
         */
        public TexCo(Number flag) {
            this.flag = flag;
        }

        /**
         * True is this textco flag is active for the given mask
         * @param mask the mask to check this flag against
         * @return true if this texco mode is active in the given mask
         */
        public boolean isOn(Number mask) {
            return ((mask.longValue() & flag.longValue()) == flag.longValue());
        }
        /**
         * Original coordinates of the mesh
         */
        public static final TexCo ORCO = new TexCo(1);
        /**
         * Reflection vector as texture coordinates
         */
        public static final TexCo REFL = new TexCo(2);
        /**
         * Normal vector as texture coordinates
         */
        public static final TexCo NORM = new TexCo(4);
        /**
         * Global coordinates for texture coordinates
         */
        public static final TexCo GLOB = new TexCo(8);
        /**
         * UV coordinates for texture coordinates
         */
        public static final TexCo UV = new TexCo(16);
        /**
         * Linked object coordinates for texture coordinates
         */
        public static final TexCo OBJECT = new TexCo(32);
        /**
         * ?
         */
        public static final TexCo LAVECTOR = new TexCo(64);
        /**
         * View coordinates for texture coordinates
         */
        public static final TexCo VIEW = new TexCo(128);
        /**
         * Mesh sticky coordinates for texture coordinates
         */
        public static final TexCo STICKY = new TexCo(256);
        /**
         * ?
         */
        public static final TexCo OSA = new TexCo(512);
        /**
         * Screen coordinates for texture coordinates
         */
        public static final TexCo WINDOW = new TexCo(1024);
        /**
         * ?
         */
        public static final TexCo NEED_UV = new TexCo(2048);
        /**
         * Mesh tangents coordinates for texture coordinates
         */
        public static final TexCo TANGENT = new TexCo(4096);
        /**
         * ?
         */
        public static final TexCo STRAND = new TexCo(8192);
        /**
         * ?
         */
        public static final TexCo PARTICLE = new TexCo(8192);
        /**
         * Mesh stress coordinates for texture coordinates
         */
        public static final TexCo STRESS = new TexCo(16384);
        /**
         * ?
         */
        public static final TexCo SPEED = new TexCo(32768);
    }

    /**
     * The rendering type of the material
     */
    class Type {

        private final Number code;

        /**
         * Initializes this type with the given code
         * @param code the code of this type
         */
        public Type(Number code) {
            this.code = code;
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public boolean equals(Object that) {
            return that instanceof Type && ((Type) that).code.equals(this.code);
        }
        /**
         * Render as surface
         */
        public static final Type SURFACE = new Type(0);
        /**
         * Render as halo
         */
        public static final Type HALO = new Type(1);
        /**
         * Render as volume
         */
        public static final Type VOLUME = new Type(2);
        /**
         * Render as wire
         */
        public static final Type WIRE = new Type(3);
    }

    /**
     * Identifiers for the material mode
     */
    class Mode {

        private final Number flag;

        /**
         * Initializes this mode with the given flag
         * @param flag the flag of this mode
         */
        public Mode(Number flag) {
            this.flag = flag;
        }

        @Override
        public String toString() {
            Field[] fields = Mode.class.getFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(field.getType() == Mode.class) {
                    try {
                        Mode m = (Mode) field.get(null);
                        if(m.flag.intValue() == this.flag.intValue()) return "Mode." + field.getName();
                    } catch (IllegalArgumentException ex) {
                        Log.ex(ex);
                    } catch (IllegalAccessException ex) {
                        Log.ex(ex);
                    }
                }
            }
            return "Mode.Undefined";
        }

        /**
         * Checks if the given mode is active given a integer mask
         * @param mask the mask of mode values
         * @return true if this mode is actie in the given mask
         */
        public boolean isOn(Number mask) {
            return (mask.longValue() & flag.longValue()) == flag.longValue();
        }
        /**
         * Material visible for shadow lamps
         */
        public static final Mode TRACEBLE = new Mode(1);
        /**
         * Material enabled for shadows
         */
        public static final Mode SHADE = new Mode(2);
        /**
         * Material insensitive to light or shadow
         */
        public static final Mode SHADELESS = new Mode(4);
        /**
         * Wire mode material
         */
        public static final Mode WIRE = new Mode(8);
        /**
         * ?
         */
        public static final Mode VERTEXCOL = new Mode(16);
        /**
         * ?
         */
        public static final Mode HALO_SOFT = new Mode(16);
        /**
         * Render as halo
         */
        public static final Mode HALO = new Mode(32);
        /**
         * Z-Buffer transparent faces
         */
        public static final Mode ZTRANSP = new Mode(64);
        /**
         * Replaces basic colors with vertex colors
         */
        public static final Mode VERTECOLP = new Mode(128);
        /**
         * Renders with inverted z-buffer
         */
        public static final Mode ZINV = new Mode(256);
        /**
         * Render rings over the basic halo (?)
         */
        public static final Mode HALO_RINGS = new Mode(256);
        /**
         * Do not render material
         */
        public static final Mode ENV = new Mode(512);
        /**
         * Render as halo lines
         */
        public static final Mode HALO_LINES = new Mode(512);
        /**
         * Render only shadows
         */
        public static final Mode ONLYSHADOW = new Mode(1024);
        /**
         * Use extreme alpha
         */
        public static final Mode HALO_XALPHA = new Mode(1024);
        /**
         * Render halo as star
         */
        public static final Mode STAR = new Mode(0x800);
        /**
         * Gives color and texture for faces
         */
        public static final Mode FACETEXTURE = new Mode(0x800);
        /**
         * Texture for halo
         */
        public static final Mode HALOTEX = new Mode(0x1000);
        /**
         * Vertex normal specifies the halo dimension
         */
        public static final Mode HALOPUNO = new Mode(0x2000);
        /**
         * ?
         */
        public static final Mode ONLYCAST = new Mode(0x2000);
        /**
         * Material insensitive to mist
         */
        public static final Mode NOMIST = new Mode(0x4000);
        /**
         * Halo receives light
         */
        public static final Mode HALO_SHADE = new Mode(0x4000);
        /**
         * Render halo as lens flare
         */
        public static final Mode HALO_FLARE = new Mode(0x8000);
        /**
         * ?
         */
        public static final Mode TRANSP = new Mode(0x10000);
        /**
         * Enables raytracing for transparency
         */
        public static final Mode RAYTRANSP = new Mode(0x20000);
        /**
         * Enables raytracing for mirror
         */
        public static final Mode RAYMIRROR = new Mode(0x40000);
        /**
         * ?
         */
        public static final Mode SHADOW_TRA = new Mode(0x80000);
        /**
         * Colorband ramp status for diffuse color
         */
        public static final Mode RAMP_COL = new Mode(0x100000);
        /**
         * Colorband ramp status for specular color
         */
        public static final Mode RAMP_SPEC = new Mode(0x200000);
        /**
         * Phong interpolated normals for shadow error prevention
         */
        public static final Mode RAY_BIAS = new Mode(0x400000);
        /**
         * Rendering of all osa samples
         */
        public static final Mode FULL_OSA = new Mode(0x800000);
        /**
         * Use direction of strands as normal for tangent-shading
         */
        public static final Mode TANGENT_STR = new Mode(0x1000000);
        /**
         * ?
         */
        public static final Mode SHADBUF = new Mode(0x2000000);
        /**
         * Use tangent vector direction for shading
         */
        public static final Mode TANGENT_V = new Mode(0x4000000);
        /**
         * Tangent space normal mapping
         */
        public static final Mode NORMAP_TANG = new Mode(0x8000000);
        /**
         * Lights from this group if if the lights are on a hidden layer (?)
         */
        public static final Mode GROUP_NOLAY = new Mode(0x10000000);
        /**
         * If FACETEXTURE use alpha too
         */
        public static final Mode FACETEXTURE_ALPHA = new Mode(0x20000000);
        /**
         * ?
         */
        public static final Mode STR_B_UNITS = new Mode(0x40000000);
        
        /**
         * ?
         */
        public static final Mode STR_SURFDIFF = new Mode(0x80000000);

        public static final List<Mode> values = Collections.<Mode>unmodifiableList(Arrays.<Mode>asList(
            SHADE,SHADELESS,WIRE,VERTEXCOL,HALO_SOFT,
            HALO,ZTRANSP,VERTECOLP,ZINV,HALO_RINGS,
            ENV,HALO_LINES,ONLYSHADOW,HALO_XALPHA,STAR,
            FACETEXTURE,HALOTEX,HALOPUNO,ONLYCAST,NOMIST,
            HALO_SHADE,HALO_FLARE,TRANSP,RAYTRANSP,RAYMIRROR,
            SHADOW_TRA,RAMP_COL,RAMP_SPEC,RAY_BIAS,FULL_OSA,
            TANGENT_STR,SHADBUF,TANGENT_V,NORMAP_TANG,GROUP_NOLAY,
            FACETEXTURE_ALPHA,STR_B_UNITS,STR_SURFDIFF));

        public static List<Mode> getActiveModes(Number mask) {
            LinkedList<Mode> activeModes = new LinkedList<Mode>();
            for (Mode mode : values) {
                if(mode.isOn(mask)) activeModes.add(mode);
            }
            return activeModes;
        }
    }

    /**
     * The type of the diffuse shader used by the material
     */
    class DiffuseShader {

        private final Number code;

        /**
         * Initializes this diffuse shader type with the given int code
         * @param code the numeric code that identifies this diffuse shader type
         */
        public DiffuseShader(Number code) {
            this.code = code;
        }

        @Override
        public int hashCode() {
            return code.intValue();
        }

        @Override
        public boolean equals(Object that) {
            return that instanceof DiffuseShader && ((DiffuseShader) that).code.equals(this.code);
        }
        /**
         * Lambert diffuse shader
         */
        public static final DiffuseShader LAMBERT = new DiffuseShader(0);
        /**
         * Oren-Nayer diffuse shader
         */
        public static final DiffuseShader ORENNAYAR = new DiffuseShader(1);
        /**
         * Toon diffuse shader
         */
        public static final DiffuseShader TOON = new DiffuseShader(2);
        /**
         * Minnaert diffuse shader
         */
        public static final DiffuseShader MINNAERT = new DiffuseShader(3);
        /**
         * Fresnel diffuse shader
         */
        public static final DiffuseShader FRESNEL = new DiffuseShader(4);
    }

    /**
     * Identifies the specular shader used by this material
     */
    class SpecularShader {

        private final Number code;

        /**
         * Initializes this specular shader with the given code
         * @param code the code that identifies this shader type
         */
        public SpecularShader(Number code) {
            this.code = code;
        }

        @Override
        public int hashCode() {
            return code.intValue();
        }

        @Override
        public boolean equals(Object that) {
            return that instanceof SpecularShader && ((SpecularShader) that).code.equals(this.code);
        }
        /**
         * Cook-Torr specular shader
         */
        public static final SpecularShader COOKTORR = new SpecularShader(0);
        /**
         * Phong specular shader
         */
        public static final SpecularShader PHONG = new SpecularShader(1);
        /**
         * Blinn specular shader
         */
        public static final SpecularShader BLINN = new SpecularShader(2);
        /**
         * Toon specular shader
         */
        public static final SpecularShader TOON = new SpecularShader(3);
        /**
         * Ward-iso specular shader
         */
        public static final SpecularShader WARDISO = new SpecularShader(4);
    }

    /**
     * Checks if this material has the given diffuse shader type
     * @param shader the diffuse shader type to check
     * @return true if the given diffuse shader type is active on this material
     */
    public boolean hasDiffuseShader(DiffuseShader shader);

    /**
     * Checks if this material has the given specular shader type
     * @param shader the specular shader type to check
     * @return true if the given specular shader type is active on this material
     */
    public boolean hasSpecularShader(SpecularShader shader);

    /**
     * Cehcks if the given mode is active for this material
     * @param mode the mode code to check
     * @return true if the given mode is active on this material
     */
    public boolean isModeOn(Mode mode);

    /**
     * Returns the name of this material
     * @return the name of this material
     */
    public String getName();

    /**
     * How many texture slots are enabled on this material
     * @return the number of texture slots used by this material
     */
    public Number getTextureSlotsCount();

    /**
     * Returns the texture at the given slot.
     * @param slotIndex the index of the texture slot to get
     * @return the blender texture at the given slot or null if the slot is empty
     */
    public BlenderTexture getTexture(Number slotIndex);

    /**
     * The render type of the material
     * @return the render type of the material
     */
    public Type getType();

    /**
     * Returns the color of this material (diffuse?)
     * @return the color of this material
     */
    public BlenderTuple3 getRgb();

    /**
     * Returns the specular color of this material
     * @return the specular color of this material
     */
    public BlenderTuple3 getSpecularRgb();

    /**
     * Returns the mirror color of this material
     * @return the mirror color of this material
     */
    public BlenderTuple3 getMirrorRgb();

    /**
     * Returns the ambient color of this material
     * @return the ambient color of this material
     */
    public BlenderTuple3 getAmbientRgb();

    /**
     * Returns the specular factor of this material
     * @return the specular factor of this material
     */
    public Number getSpecFactor();

    /**
     * The material standard deviation of surface slope. Yap, i have no idea of
     * what that means.
     * @return the material standard deviation of surface slope.
     */
    public Number getRmsFactor();

    /**
     * Returns the refraction index of the material
     * @return the refraction index of the material
     */
    public Number getRefFactor();

    /**
     * Returns the darkness of this material
     * @return the darkness of this material
     */
    public Number getDarknessFactor();

    /**
     * Returns the hardness of this material
     * @return the hardness of this material
     */
    public Number getHardnessFactor();

    /**
     * Returns the transparency of this material (0.0 -> 1.0)
     * @return the transparency of this material. 0 is fully transparent, 1 is fully opaque
     */
    public Number getAlpha();

    /**
     * Returns the set used of texture unit index -> texture pairs
     * @return the filled texture units of this material
     */
    public Map<Integer, BlenderTexture> getActiveTextureUnits();

    /**
     * Return a list of all the material modes active in this material
     * @return the list of active modes (transparency, wire, shadowless and so on
     */
    public List<Mode> getActiveModes();
}
