package myapp.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import myapp.converter.ImageConverter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final String outputDirPath = "D:/outputPuzzleGame"; // Set the desired output directory

    private String createDirectory(String path) {
        Path directoryPath = Paths.get(path);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
                return "Cannot create directory " + path;
            }
        }
        return path;
    }

    public String dividingTheImageIntoPuzzles(MultipartFile image, int numberOfPieces) {
        try {
            // Convert the MultipartFile to a BufferedImage
            BufferedImage originalImage = ImageIO.read(image.getInputStream());

            // Create the output directory if it does not exist
            createDirectory(outputDirPath);

            // Create the puzzles using ImageConverter
            ImageConverter.createPuzzles(originalImage, numberOfPieces, outputDirPath);

            return "Image processed successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing the image.";
        }
    }

    public boolean verify(List<String> puzzles) {
        File[] files = new File(outputDirPath).listFiles();
        File[] sortedArray = Arrays.asList(files)
                .stream().sorted(Comparator.comparing(file -> {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(file.getName());
                    return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
                })).toArray(File[]::new);
        for (int i = 0; i < sortedArray.length; i++) {
            File file = sortedArray[i];
            String stringPuzzle = puzzles.get(i);
            boolean equal = isEqual(stringPuzzle, file);
            if (!equal) {
                return false;
            }
        }
        return true;
    }

    private boolean isEqual(String stringPuzzle, File file) {
        BufferedImage bufferedPuzzle = ImageConverter.convertString(stringPuzzle);
        BufferedImage bufferedFile = ImageConverter.convertFileToBufferedImage(file);
        byte[] filePixels = getPixels(bufferedFile);
        byte[] puzzlePixels = getPixels(bufferedPuzzle);
        return Arrays.equals(filePixels, puzzlePixels);
    }

    private byte[] getPixels(BufferedImage bufferedFile) {
        return ((DataBufferByte) bufferedFile.getRaster().getDataBuffer()).getData();
    }

    public List<BufferedImage> getPiecesOrderedByOpenCV(List<String> puzzles) throws MalformedURLException {
        List<File> fileList = List.of(new File(outputDirPath).listFiles());
        File[] puzzleFiles = fileList.toArray(new File[0]);
        List<Mat> assembledPieces = new ArrayList<>();
        try {
            List<Mat> imagePieces = loadAndPreprocessImages(puzzleFiles);
            assembledPieces = assembleImagePieces(imagePieces);
        } catch (Exception e) {
            System.out.println("Error occurred during image assembly: " + e.getMessage());
        }
        List<BufferedImage> bufferedImages = assembledPieces.stream()
                .map(mat -> ImageConverter.convertMatToBufferedImage(mat))
                .collect(Collectors.toList());
        System.out.println("The list of buffered images ordered by OpenCV have been created");
        return bufferedImages;
    }

    public void saveAssembledImagesToDirectory(List<BufferedImage> bufferedImages) {
        String path = "./assembled/";
        createDirectory(path);
        BufferedImage[] array = bufferedImages.toArray(new BufferedImage[0]);
        ImageConverter.convertBufferedImagesToFiles(array, path);
        System.out.println("the list of image files have been created");
    }

    private static List<Mat> loadAndPreprocessImages(File[] puzzleFiles) {
        List<Mat> imagePieces = new ArrayList<>();
        if (puzzleFiles != null) {
            for (File puzzleFile : puzzleFiles) {
                if (puzzleFile.isFile() && puzzleFile.getName().endsWith(".jpg")) {
                    Mat image = Imgcodecs.imread(puzzleFile.getAbsolutePath());
                    imagePieces.add(image);
                }
            }
        }
        return imagePieces;
    }

    private static List<Mat> assembleImagePieces(List<Mat> imagePieces) {
        List<Mat> assembledPieces = new ArrayList<>();
        Mat firstPiece = imagePieces.get(0);
        assembledPieces.add(firstPiece);
        imagePieces.remove(0);
        while (!imagePieces.isEmpty()) {
            Mat previousPiece = assembledPieces.get(assembledPieces.size() - 1);
            Mat bestMatch = null;
            double bestMatchScore = Double.MAX_VALUE;
            Mat previousBorderPixels = extractBorderPixels(previousPiece, BorderType.RIGHT);
            for (Mat currentPiece : imagePieces) {
                Mat currentBorderPixels = extractBorderPixels(currentPiece, BorderType.LEFT);

                double score = calculateMatchingScore(previousBorderPixels, currentBorderPixels);

                if (score < bestMatchScore) {
                    bestMatchScore = score;
                    bestMatch = currentPiece;
                }
            }
            if (bestMatch != null) {
                assembledPieces.add(bestMatch);
                imagePieces.remove(bestMatch);
            } else {
                break;
            }
        }

        return assembledPieces;
    }

    private static Mat extractBorderPixels(Mat image, BorderType borderType) {
        int rows = image.rows();
        int cols = image.cols();
        int borderSize = 1;
        Mat borderPixels = new Mat();
        if (borderType == BorderType.LEFT) {
            borderPixels = new Mat(image, new Rect(0, 0, borderSize, rows));
        } else if (borderType == BorderType.RIGHT) {
            borderPixels = new Mat(image, new Rect(cols - borderSize, 0, borderSize, rows));
        }
        return borderPixels;
    }

    private static double calculateMatchingScore(Mat borderPixels1, Mat borderPixels2) {
        int totalPixels = borderPixels1.rows() * borderPixels1.cols();

        double sumSquaredDiff = 0;
        for (int row = 0; row < borderPixels1.rows(); row++) {
            for (int col = 0; col < borderPixels1.cols(); col++) {
                double diff = borderPixels1.get(row, col)[0] - borderPixels2.get(row, col)[0];
                sumSquaredDiff += Math.pow(diff, 2);
            }
        }
        return sumSquaredDiff / totalPixels;
    }

    enum BorderType {
        LEFT,
        RIGHT
    }

    private static Mat mergeImagePieces(List<Mat> assembledPieces) {
        Mat assembledImage = new Mat();
        Core.hconcat(assembledPieces, assembledImage);
        return assembledImage;
    }

    public void placePiecesOrderedByOpenCV(List<String> puzzles) throws MalformedURLException {
        List<BufferedImage> piecesOrderedByOpenCV = getPiecesOrderedByOpenCV(puzzles);
        saveAssembledImagesToDirectory(piecesOrderedByOpenCV);
    }
}

