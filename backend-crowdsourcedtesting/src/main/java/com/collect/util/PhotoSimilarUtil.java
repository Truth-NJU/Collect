package com.collect.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PhotoSimilarUtil {

    private static final int size = 3;

    public static double comparePhotos(List<String> la, List<String> lb){
        if (empty(la) && empty(lb)){
            return 1;
        }
        if (empty(la) || empty(lb)){
            return 0;
        }

        try {
            List<int[]> list1 = new ArrayList<>();
            List<int[]> list2 = new ArrayList<>();
            for (String s : la) {
                list1.add(getPixel(s));
            }
            for (String s : lb) {
                list2.add(getPixel(s));
            }
            PriorityQueue<Double> doubles = new PriorityQueue<>();
            for (int[] i1 : list1) {
                for (int[] i2 : list2) {
                    int hammingDistance = getHammingDistance(i1, i2);
                    double similar = calSimilarity(hammingDistance);
                    doubles.offer(similar);
                    if (doubles.size() > size) {
                        doubles.poll();
                    }
                }
            }
            double sum = 0;
            for (Double aDouble : doubles) {
                sum += aDouble;
            }
            return sum / doubles.size();
        }catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean empty(List<String> list){
        return list==null || list.isEmpty();
    }

    private static int[] getPixel(String path) throws IOException {
        File imageFile = new File("file"+File.separator+"report_img"+File.separator+path);
        Image image = ImageIO.read(imageFile);
        image = toGrayscale(image);
        image = scale(image);
        int[] pixels = getPixels(image);
        int averageColor = getAverageOfPixelArray(pixels);
        pixels = getPixelDeviateWeightsArray(pixels, averageColor);
        return pixels;
    }

    private static BufferedImage convertToBufferedFrom(Image srcImage) {
        BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(null),
                srcImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(srcImage, null, null);
        g.dispose();
        return bufferedImage;
    }

    private static BufferedImage toGrayscale(Image image) {
        BufferedImage sourceBuffered = convertToBufferedFrom(image);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(sourceBuffered, null);
    }

    private static Image scale(Image image) {
        image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return image;
    }

    private static int[] getPixels(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        return convertToBufferedFrom(image).getRGB(0, 0, width, height, null, 0, width);
    }

    private static int getAverageOfPixelArray(int[] pixels) {
        Color color;
        long sumRed = 0;
        for (int pixel : pixels) {
            color = new Color(pixel, true);
            sumRed += color.getRed();
        }
        return (int) (sumRed / pixels.length);
    }

    private static int[] getPixelDeviateWeightsArray(int[] pixels, final int averageColor) {
        Color color;
        int[] dest = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            color = new Color(pixels[i], true);
            dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
        }
        return dest;
    }

    private static int getHammingDistance(int[] a, int[] b) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] == b[i] ? 0 : 1;
        }
        return sum;
    }

    private static double calSimilarity(int hammingDistance) {
        int length = 32 * 32;
        double similarity = (length - hammingDistance) / (double) length;
        similarity = java.lang.Math.pow(similarity, 2);
        return similarity;
    }

}

