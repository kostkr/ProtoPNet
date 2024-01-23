package com.example.website.controller;

import com.example.website.model.ImageReader;
import com.example.website.model.MyImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class RateController {
    @Autowired
    ImageReader imageReader;

    @GetMapping("/getImages")
    public List<MyImage> getImages() {
        return imageReader.readImages();
    }

    @PostMapping("/rateImage")
    public void rateImage(@RequestBody String data) {
        imageReader.showUserAnswer(data);
    }

}


