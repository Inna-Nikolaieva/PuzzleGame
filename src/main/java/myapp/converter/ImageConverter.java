package myapp.converter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageConverter {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

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

    public static List<Mat> convertStringsToMats(List<String> base64Images) {
        List<Mat> mats = new ArrayList<>();

        for (String base64Image : base64Images) {
            byte[] decodedImage = Base64.getDecoder().decode(base64Image);
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedImage));
            } catch (IOException e) {

                e.printStackTrace();
                continue;
            }

            Mat mat = convertBufferedImageToMat(bufferedImage);
            mats.add(mat);
        }

        return mats;
    }

    public static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        int type = bufferedImage.getType();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);

        return mat;
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

    public static BufferedImage convertMatToBufferedImage(Mat mat) {
        if (mat.rows() <= 0 || mat.cols() <= 0) {
            throw new IllegalArgumentException("Invalid Mat dimensions. Rows and columns must be greater than 0.");
        }

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