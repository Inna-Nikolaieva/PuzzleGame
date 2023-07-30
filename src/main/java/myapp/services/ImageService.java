package myapp.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import myapp.converter.ImageConverter;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final String outputDirPath = "D:/outputPuzzleGame";
    private final String assembledDirPath = "D:/assembledPuzzleGame";

    enum BorderType {
        LEFT,
        RIGHT,
        BOTTOM,
        TOP
    }

    private void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }

    private String createDirectory(String path) {
        Path directoryPath = Paths.get(path);

        try {
            if (Files.exists(directoryPath)) {
                File directory = directoryPath.toFile();
                purgeDirectory(directory);
            } else {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Cannot create directory " + path;
        }

        return path;
    }


    public String dividingTheImageIntoPuzzles(MultipartFile image, int numberOfPieces) {
        try {
            BufferedImage originalImage = ImageIO.read(image.getInputStream());

            createDirectory(outputDirPath);

            ImageConverter.createPuzzles(originalImage, numberOfPieces, outputDirPath);

            return "Image processed successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing the image.";
        }
    }

    private static List<Mat> loadAndPreprocessImages(File[] puzzleFiles) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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

    private byte[] getPixels(BufferedImage bufferedFile) {
        return ((DataBufferByte) bufferedFile.getRaster().getDataBuffer()).getData();
    }

    private boolean isEqual(String stringPuzzle, File file) {
        BufferedImage bufferedPuzzle = ImageConverter.convertString(stringPuzzle);
        BufferedImage bufferedFile = ImageConverter.convertFileToBufferedImage(file);
        byte[] filePixels = getPixels(bufferedFile);
        byte[] puzzlePixels = getPixels(bufferedPuzzle);
        return Arrays.equals(filePixels, puzzlePixels);
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

    public void saveAssembledImageToDirectory(String path, BufferedImage assembledImage) {
        createDirectory(path);
        String filePath = path + "/assembled_puzzle.jpg";

        File outputFile = new File(filePath);

        try {
            ImageIO.write(assembledImage, "jpg", outputFile);
            System.out.println("Assembled image saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving assembled image.");
        }
    }

    public List<BufferedImage> getPiecesOrderedByOpenCV(List<String> puzzles) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
        System.out.println("The list of buffered images ordered by OpenCV has been created");
        return bufferedImages;
    }

    public BufferedImage assemblePuzzle(List<BufferedImage> piecesOrderedByOpenCV, int numberOfPieces) {
        int totalPieces = piecesOrderedByOpenCV.size();
        int pieceSize = piecesOrderedByOpenCV.get(0).getWidth();

        int rows = (int) Math.sqrt(numberOfPieces);
        int columns = rows;

        int assembledSize = rows * pieceSize;


        Mat assembledMat = new Mat(assembledSize, assembledSize, CvType.CV_8UC3);

        int currentRow = 0;
        int currentColumn = 0;

        for (int i = 0; i < totalPieces; i++) {
            BufferedImage piece = piecesOrderedByOpenCV.get(i);

            Mat pieceMat = ImageConverter.convertBufferedImageToMat(piece);

            Mat resizedPieceMat = new Mat();
            Size targetSize = new Size(pieceSize, pieceSize);
            Imgproc.resize(pieceMat, resizedPieceMat, targetSize);

            if (resizedPieceMat.channels() == 1) {
                Imgproc.cvtColor(resizedPieceMat, resizedPieceMat, Imgproc.COLOR_GRAY2BGR);
            }

            int posX = currentColumn * pieceSize;
            int posY = currentRow * pieceSize;

            Mat targetROI = assembledMat.submat(posY, posY + pieceSize, posX, posX + pieceSize);
            resizedPieceMat.copyTo(targetROI);

            currentColumn++;
            if (currentColumn >= columns) {
                currentColumn = 0;
                currentRow++;
            }
        }

        BufferedImage assembledImage = ImageConverter.convertMatToBufferedImage(assembledMat);

        return assembledImage;
    }


    public void placePiecesOrderedByOpenCV(List<String> puzzles) throws MalformedURLException {
        List<BufferedImage> piecesOrderedByOpenCV = getPiecesOrderedByOpenCV(puzzles);
        int numberOfPieces = piecesOrderedByOpenCV.size();

        BufferedImage assembledImage = assemblePuzzle(piecesOrderedByOpenCV, numberOfPieces);
        saveAssembledImageToDirectory(assembledDirPath, assembledImage);
    }
}