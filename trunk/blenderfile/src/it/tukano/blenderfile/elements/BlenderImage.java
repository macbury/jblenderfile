/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tukano.blenderfile.elements;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * An image extracted from the blender file.
 * @author pgi
 */
public interface BlenderImage {

    /**
     * Returns the blender image as a java image. Can return null if the image
     * couldn't be read because of a format or location issue.
     * @return the image read from the blender file or null if no could be read
     */
    BufferedImage getJavaImage();

    /**
     * Returns the path of the image. The path can be absolute or relative.
     * @return the path of the image
     */
    String getImagePath();

    /**
     * Returns the raw data of the image. This can be used to parse images in
     * unsupported formats (like tga, dds, tiff and so on). Can be null if the
     * image location has not been found. If this is null then the only way to
     * get back the image is getImagePath.
     * @return the raw data of the image or null if no image is found.
     */
    ByteBuffer getImageData();
}
