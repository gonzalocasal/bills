package com.bills.util;

import net.sourceforge.tess4j.Tesseract;
import org.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bills.util.Constants.PAGE_SEGMENTATION_MODE;

public class TextExtractor {

    public List<String> getImageText(InputStream imageFile) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setPageSegMode(PAGE_SEGMENTATION_MODE);
            BufferedImage image = ImageIO.read(imageFile);
            return Arrays.stream(tesseract.doOCR(image).split(System.lineSeparator())).filter(l -> !l.isEmpty()).collect(Collectors.toList());
        } catch (Exception e) {
            Logger.error("Error trying to read image text");
            return new ArrayList<>();
        }
    }

}
