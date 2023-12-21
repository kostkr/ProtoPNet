package com.example.website.controller;

import com.example.website.model.ImageReader;
import com.example.website.model.MyImage;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class RateController {

    @GetMapping("/getImages")
    public List<MyImage> getImages() {
        return ImageReader.readImages();
    }

    @PostMapping("/rateImage")
    public void rateImage(@RequestBody String rate) {
        System.out.println("rated " + rate );
    }

}


