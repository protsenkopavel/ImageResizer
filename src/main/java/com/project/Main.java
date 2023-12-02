package com.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final long START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        String source = "src/main/resources/source";
        String destination = "src/main/resources/destination";

        resizeImages(source, destination, Runtime.getRuntime().availableProcessors()); //pathToSourceFolder, destinationPath, countOfThreads
    }

    private static void resizeImages(String sourcePath, String destinationPath, int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        File imagesDirectory = new File(sourcePath);
        File[] images = imagesDirectory.listFiles();

        if (images != null) {
            int batchSize = images.length / threads;

            for (int i = 0; i < threads; i++) {
                int start = i * batchSize;
                int end = (i == threads - 1) ? images.length : (i + 1) * batchSize;

                File[] batch = new File[end - start];
                System.arraycopy(images, start, batch, 0, end - start);

                executor.submit(new ImageResizer(batch, 1400, destinationPath, START_TIME));
            }
        } else {
            LOGGER.log(Level.WARNING, "No images found in the source directory");
        }

        executor.shutdown();
    }
}
