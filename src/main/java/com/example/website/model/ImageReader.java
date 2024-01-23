package com.example.website.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class ImageReader {

    private long uniqueImageIndex = 0L;

    @Value("${script.path}")
    private String scriptPath;

    @Value("${model.dir.path}")
    private String modeldirPath;

    @Value("${model.name}")
    private String modelName;

    @Value("${images.base.path}")
    private String baseImagesPath;

    public List<MyImage> readImages() {
        ArrayList<MyImage> images = new ArrayList<>(11);
        try{
            // find image to analyze
            String imgPath = findPathImageToAnalyze();

            // config model script
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", scriptPath,
                    "-modeldir", this.modeldirPath, "-model", this.modelName, "-imgpath", imgPath);

            //start model script
            Process process = processBuilder.start();

            // read images from script
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder outputBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line);
            }

            File file = new File(imgPath);// add original image
            MyImage myImage = new MyImage(getUniqueImageIndex(), convertToBase64(ImageIO.read(file)));
            images.add(myImage);

            long index = 1;// add predicted images
            for (String image : outputBuilder.toString().split(", ")) {
                images.add(new MyImage(index, image));
                ++index;
            }

            int exitCode = process.waitFor();
            //System.out.println("ProtoPNet script exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return images;
    }

    public String findPathImageToAnalyze(){
        File randomFolder = getRandomFolder(baseImagesPath);

        if (randomFolder != null) {
            File randomImage = getRandomImage(randomFolder);
            if (randomImage != null) {
                return randomImage.getPath();
            } else {
                System.err.println("There are no images in the selected folder");
            }
        } else {
            System.err.println("There are no folders at the specified path");
        }

        return "";
    }

    public void showUserAnswer(String data){
        System.out.println(data);
    }

    private File getRandomFolder(String basePath) {
        File baseDir = new File(basePath);
        File[] folders = baseDir.listFiles(File::isDirectory);

        if (folders != null && folders.length > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(folders.length);
            return folders[randomIndex];
        }

        return null;
    }

    private File getRandomImage(File folder) {
        File[] images = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"));

        if (images != null && images.length > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(images.length);
            return images[randomIndex];
        }
        return null;
    }

    private String convertToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private long getUniqueImageIndex(){
        increaseUniqueIndex();
        return uniqueImageIndex == 0 ? 0 : uniqueImageIndex - 1;
    }

    private void increaseUniqueIndex(){
        if(uniqueImageIndex < Long.MAX_VALUE)
            uniqueImageIndex += 1;
        else
            uniqueImageIndex = 0;
    }
}