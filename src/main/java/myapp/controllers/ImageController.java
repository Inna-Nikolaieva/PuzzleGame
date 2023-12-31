package myapp.controllers;

import myapp.services.ImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/divideImage")
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