package com.project;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageResizer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ImageResizer.class.getName());

    private final File[] files;
    private final int newWidth;
    private final String destination;
    private final long start;

    public ImageResizer(File[] files, int newWidth, String destination, long start) {
        this.files = files;
        this.newWidth = newWidth;
        this.destination = destination;
        this.start = start;
    }

    @Override
    public void run() {
        for (File file : files) {
            resizeImage(file);
        }
        LOGGER.log(Level.INFO, "Thread stopped, images have been resized successfully, time: " + (System.currentTimeMillis() - start) + " ms");
    }

    private void resizeImage(File file) {
        try {
            BufferedImage originalImage = ImageIO.read(file);
            if (originalImage == null) {
                return;
            }

            int newHeight = (int) Math.round(originalImage.getHeight() / (originalImage.getWidth() / (double) newWidth));

            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, newWidth, newHeight);
            File outputFile = new File(destination + "/" + file.getName());
            ImageIO.write(resizedImage, "jpg", outputFile);
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Error while resizing image: " + file.getName(), e);
        }
    }
}
