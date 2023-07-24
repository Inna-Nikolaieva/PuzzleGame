package myapp.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;


public class ImageConverter {

    public static void createPuzzles(BufferedImage originalImage, int numberOfPieces, String outputDirPath) {
        try {
            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();

            int puzzleWidth = imageWidth / (int) Math.sqrt(numberOfPieces);
            int puzzleHeight = imageHeight / (int) Math.sqrt(numberOfPieces);

            for (int i = 0; i < Math.sqrt(numberOfPieces); i++) {
                for (int j = 0; j < Math.sqrt(numberOfPieces); j++) {
                    int x = j * puzzleWidth;
                    int y = i * puzzleHeight;
                    BufferedImage subImage = originalImage.getSubimage(x, y, puzzleWidth, puzzleHeight);
                    File outputFile = new File(outputDirPath, "img" + (i * (int) Math.sqrt(numberOfPieces) + j) + ".jpg");
                    ImageIO.write(subImage, "jpg", outputFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage convertString(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        try {
            return ImageIO.read(new ByteArrayInputStream(decodedBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage convertFileToBufferedImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> convertBufferedImagesToFiles(BufferedImage[] bufferedImages, String imagesDirectory) {
        List<File> puzzleFiles = new ArrayList<>();
        for (int i = 0; i < bufferedImages.length; i++) {
            File outputFile = new File(imagesDirectory + "img" + i + ".jpg");
            try {
                ImageIO.write(bufferedImages[i], "jpg", outputFile);
            } catch (IOException e) {
                throw new RuntimeException("Cannot write puzzle image");
            }
            puzzleFiles.add(outputFile);
        }
        System.out.println("Puzzles have been created.");
        return puzzleFiles;
    }

    public static BufferedImage convertMatToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] data = new byte[bufferSize];
        mat.get(0, 0, data);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);

        return image;
    }

}