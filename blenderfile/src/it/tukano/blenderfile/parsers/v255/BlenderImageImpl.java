/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tukano.blenderfile.parsers.v255;

import it.tukano.blenderfile.elements.BlenderImage;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * BlenderImage implementation
 * @author pgi
 */
class BlenderImageImpl implements BlenderImage {
    private BufferedImage image;
    private String imageName;
    private ByteBuffer data;

    public BlenderImageImpl() {
    }

    synchronized BlenderImageImpl setJavaImage(BufferedImage javaImage) {
        image = javaImage;
        return this;
    }

    synchronized BlenderImageImpl setImageLocation(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public synchronized BufferedImage getJavaImage() {
        return image;
    }

    public synchronized String getImagePath() {
        return imageName;
    }

    synchronized BlenderImageImpl setImageData(ByteBuffer buffer) {
        this.data = buffer;
        return this;
    }

    public synchronized ByteBuffer getImageData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("BlenderImage path=%s hasJavaImage=%s hasDataBuffer=%s", getImagePath(), getJavaImage() != null, getImageData() == null ? "no buffer" :  getImageData().capacity());
    }
}
