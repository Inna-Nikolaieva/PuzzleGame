package myapp.controllers;

import myapp.services.ImageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/api/divideImage")
    public void divideImage(@RequestParam("image") MultipartFile image,
                            @RequestParam("numberOfPieces") int numberOfPieces) {

        imageService.dividingTheImageIntoPuzzles(image,numberOfPieces);
    }

    @PostMapping("/verifyPuzzles")
    public boolean verifyPuzzles(@RequestBody List<String> base64Images) {

        return imageService.verify(base64Images);
    }


    @PostMapping("/autoCompletePuzzles")
    public void autoCompletePuzzles(@RequestBody List<String> base64Images) {
        try {
            imageService.placePiecesOrderedByOpenCV(base64Images);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}

