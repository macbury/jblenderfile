package it.tukano.blenderfile.elements;

import java.util.List;
import java.util.Map;

/**
 * A mesh object. The geometric data can be accessed via the triangulate method.
 * @author pgi
 */
public interface BlenderMesh extends BlenderObjectData {

    /**
     * The xyz location of the mesh (3 float)
     * @return the location of the mesh
     */
    BlenderTuple3 getLocation();

    /**
     * The xyz rotation of the mesh, in radians (maybe)
     * @return the rotation of the mesh
     */
    BlenderTuple3 getRotation();

    /**
     * The xyz scale of the mesh (3 float)
     * @return the scale of the mesh
     */
    BlenderTuple3 getScale();

    /**
     * Returns a list of the triangles that compose this mesh
     * @return the list of triangles that compose this mesh
     */
    List<BlenderMeshTriangle> getTriangles();

    /**
     * Returns the triangles of this mesh grouped by material
     * @return the triangles of this mesh grouped by material
     */
    Map<BlenderMaterial, List<BlenderMeshTriangle>> getTrianglesByMaterial();

    /**
     * The name of the mesh
     * @return the name of the mesh
     */
    String getName();

    /**
     * The names of the tex coord sets of this mesh
     * @return the names of the tex coord sets of this mesh
     */
    List<String> getTexCoordSetNames();

    /**
     * How many materials are defined for this mesh
     * @return the material count of this mesh
     */
    Number getMaterialCount();

    /**
     * Returns a material from the material list of this mesh
     * @param index the index of the material to get
     * @return the material at the given index in the list of materials of this
     * mesh
     */
    BlenderMaterial getMaterial(Number index);

    /**
     * Returns the object that contains this mesh.
     * @return the object that contains this mesh.
     */
    BlenderObject getContainer();

    /**
     * Returns the data that bind one or more vertices of this mesh to the bone
     * of an armature.<br/>
     * Usage hint:<br/>
     * <pre>BlenderMesh mesh = ... some mesh
     * for(BlenderDeformVert x : mesh.getVertexBoneAssignments()) {
     *     Number theVertexIndex = x.getDeformedVertexIndex();
     *     for(BlenderDeformWeight w : x.getWeights()) {
     *         Number boneIndex = w.getBoneIndex();
     *         Number influenceWeight = w.getWeight();
     *     }
     * }</pre><br/>
     * The boneIndex value is relative to the skeleton of some armature that
     * modifies the object of the mesh:<br/>
     * <pre>BlenderObject container = mesh.getContainer();
     * List<BlenderObject> armatures = container.getModifiers(ObjectType.ARMATURE);
     * BlenderObject armatureContainer = armatures.get(0);//check for emptyness
     * BlenderArmature armature = (BlenderArmature) armatureContainer.getObjectData().get(0);
     * List<BlenderDeformVert> vertDeformDataList = mesh.getVertexBoneAssignments();
     * for(BlenderDeformVert vertDeformData : vertDeformDataList) {
     *     Number vertexIndex = vertDeformData.getDeformedVertexIndex();
     *     List<? extends BlenderDeformWeight> weights = vertDeformData.getWeights();
     *     for(BlenderDeformWeight weightData : weights) {
     *         Number weight = weightData.getWeight();
     *         BlenderBone bone = armature.getBoneForName(weightData.getBoneName());
     *         if(bone != null) {
     *             //the vertex with index vertexIndex is bound to bone with the weight factor
     *         }
     *     }
     * }</pre>
     * @return the list of vertex to bone assignments. Can be empty, never null.
     */
    List<BlenderDeformVert> getVertexBoneAssignments();
}
