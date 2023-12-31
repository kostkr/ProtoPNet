package com.example.website.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ImageReader {
    public static List<MyImage> readImages() {
        ArrayList<MyImage> images = new ArrayList<>();
        try {
            File file1 = new File("E:\\university_subject\\pz\\webSite\\src\\test\\testImage1.jpg");
            MyImage myImage1 = new MyImage(1, convertToBase64(ImageIO.read(file1)));
            images.add(myImage1);

            File file2 = new File("E:\\university_subject\\pz\\webSite\\src\\test\\testImage2.jpg");
            MyImage myImage2 = new MyImage(2, convertToBase64(ImageIO.read(file2)));
            images.add(myImage2);
        } catch (IOException e) {
            System.err.println("Images unread");
        }

        return images;
    }

    private static String convertToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}