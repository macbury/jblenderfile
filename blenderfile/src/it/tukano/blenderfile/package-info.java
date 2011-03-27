/**
 * The user-access point of the library.
 * <h1>How the parser works</h1>
 * The parser first loads the blender file and parses the SDNA of the file. There
 * is a wrapper for a structure, SDNAStructure, that is used to read values from the file.
 * A SDNAStructure can then return the value of its fields. The fields are accessed by
 * simple name (ie if something is called data[40] or *data, the name is data).<br>
 * <h1>Usage</h1>
 * The first thing to do is to parse the blender file and get its content.
 * <pre> FileInputStream in = new FileInputStream(some .blend file);
 * BlenderFileParameters param = new BlenderFileParameters(in);
 * BlenderFile blenderFile = new BlenderFile(param);
 * List&lt;BlenderScene&gt; scenes = blenderFile.getScenes();</pre>
 * Now we scan the list of scenes (probably one) to get the "real" elements.
 * <h1>Extending the parser</h1>
 * Everything starts in the BlenderFile class. The constructor of the instance
 * creates the basic structures required to parse the file. The actual parsing
 * takes place in the parseScenes method, called after the first invocation of
 * the getScenes method. The parseScenes method transforms the SC blender blocks
 * detected by the constructor. The SC blocks are scanned by an instance of the
 * BlenderFileBlockParser type suitable to handle SC blocks (actually implemented
 * by the BlenderFileSceneParser class in the v255 package).
 */
package it.tukano.blenderfile;
