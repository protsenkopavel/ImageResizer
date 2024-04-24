package com.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final long START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path to the source directory: ");
        String sourcePath = scanner.nextLine();

        File sourceDirectory = new File(sourcePath);
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            LOGGER.log(Level.SEVERE, "Invalid source directory: " + sourcePath);
            System.exit(1);
        }

        scanner.close();

        String destinationPath = sourcePath + "/resized_images";
        File destinationDirectory = new File(destinationPath);
        if (!destinationDirectory.exists()) {
            if (!destinationDirectory.mkdir()) {
                LOGGER.log(Level.SEVERE, "Failed to create destination directory: " + destinationPath);
                System.exit(1);
            }
        }

        resizeImages(sourcePath, destinationPath, Runtime.getRuntime().availableProcessors());
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

                executor.submit(new ImageResizer(batch, 600, destinationPath, START_TIME));
            }
        } else {
            LOGGER.log(Level.WARNING, "No images found in the source directory");
        }

        executor.shutdown();
    }
}
