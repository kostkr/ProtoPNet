package com.example.website.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ImageReader {

    @Value("${script.path}")
    private String scriptPath;

    @Value("${model.dir.path}")
    private String modeldirPath;

    @Value("${model.name}")
    private String modelName;

    @Value("${images.base.path}")
    private String baseImagesPath;

    public void showUserAnswer(String data){
        System.out.println(data);
    }

    public List<Image> readImages() {
        return prepareImages(invokeScript());
    }

    private List<Image> prepareImages(List<String> data) {
        List<Image> images = new ArrayList<>(11);

        try {
            //prepare original image
            //prepare original class name
            Path imagePathObj = Paths.get(data.get(0));
            Path basepathObj = Paths.get(baseImagesPath);

            Path relativePath = basepathObj.relativize(imagePathObj);

            String className = relativePath.getName(0).toString().replace('_', ' ');
            int lastDotIndex = className.lastIndexOf(".");
            if (lastDotIndex != -1 && lastDotIndex < className.length() - 1) {
                className = className.substring(lastDotIndex + 1);
            }

            File fileImage = new File(data.get(0));

            Image originalImage = Image.builder()
                    .name(className)
                    .image(convertToBase64(ImageIO.read(fileImage), "jpg"))
                    .details("")
                    .build();

            images.add(originalImage);

            //prepare prediction images
            File baseDir = new File(baseImagesPath);
            List<String> classNames = Arrays.stream(Objects.requireNonNull(baseDir.list()))// index n-1
                    .map(s -> s.split("\\.")[1])
                    .toList();

            Image predictionImage;
            int classIndex;
            File imageFile;
            String details;
            for(int i = 1; i < data.size(); i+= 5){
                classIndex = Integer.parseInt(data.get(i));
                imageFile = new File(data.get(i+1));
                details = data.get(i+2) + '\n'
                        + data.get(i+3) + '\n'
                        + data.get(i+4);

                predictionImage = Image.builder()
                        .name(classNames.get(classIndex - 1).replace('_', ' '))
                        .image(convertToBase64(ImageIO.read(imageFile), "png"))
                        .details(details)
                        .build();

                images.add(predictionImage);
            }
        } catch (IOException e) {
            System.err.println("error read images from: " + baseImagesPath);
            throw new RuntimeException(e);
        }

        return images;
    }
    private List<String> invokeScript(){
        List<String> data = new ArrayList<>(51);

        try{
            String imgPath = pathImageToAnalyze();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", scriptPath,
                    "-modeldir", this.modeldirPath, "-model", this.modelName, "-imgpath", imgPath);

            Process process = processBuilder.start();

            data.add(imgPath);// add path to original image

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("error invoke script or read output");
        }

        return data;
    }

    public String pathImageToAnalyze(){
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

    private String convertToBase64(BufferedImage image, String formatName) {
        byte[] imageBytes = new byte[0];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            System.err.println("error image convert");
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}