package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.PDFToImage;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import util.IOController;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PDFController {
    private PDDocument pdf = null;

    //Loads the pdf from file.
    public PDDocument loadPDF(File f){
        PDDocument pddc = null;
        try {
            pddc = PDDocument.load(f);
            System.out.println("PDF Loaded...");
            return pddc;
        } catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    //Loads the pdf from path-string.
    public PDDocument loadPDF(String path){
        return loadPDF(new File(path));
    }



    //Crops away the unneccesary area of every page
    private void cropPage(PDPage page){
        PDRectangle currentRect = page.getCropBox();
        PDRectangle rightwayup = new PDRectangle(currentRect.getWidth()/2, currentRect.getHeight()/2+150, currentRect.getWidth()/2, currentRect.getHeight()/2-150);
        PDRectangle upsidedown = new PDRectangle(0, 0, currentRect.getWidth()/2, currentRect.getHeight()/2-150);
        page.setCropBox(rightwayup);
        System.out.print("-");
    }

    //Loops through the pdf pages and executes cropping on every one.
    public void cropPDF(PDDocument pdf){
        System.out.print("Cropping PDF, Page [");
        for(int i = 0; i < pdf.getNumberOfPages(); i++){
            cropPage(pdf.getPage(i));
        }
        System.out.println("]");
    }
    

    //Attempts to extract images from a supplied pdf.
    public ArrayList<BufferedImage> extractImagesFromPdf(PDDocument pdf) {
        ArrayList<BufferedImage> imgList = new ArrayList<>();
        PDFRenderer renderer = new PDFRenderer(pdf);
        BufferedImage bim;
        System.out.print("Getting Image from page [");
        for (int i = 0; i < pdf.getNumberOfPages(); i++) {
            System.out.print("-");
            try {
                bim = renderer.renderImageWithDPI(i, 400, ImageType.GRAY);
                imgList.add(bim);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("]");
        return imgList;
    }

    //Returns specified pages as a new pdf.
    public PDDocument savePages(PDDocument pdf, String[] pages){
        PDDocument doc = new PDDocument();
        for(String p: pages){
            doc.addPage(pdf.getPage(Integer.parseInt(p)-1));
        }
        return doc;
    }

    //Turns a pdf- File into a pddocument.
    public PDDocument fileToPDoc(File f){

        PDDocument doc = null;
        try{
            doc = PDDocument.load(f);
        }catch (IOException e){
            System.out.println("File to doc failed: "+e.getMessage());
        }
        return doc;
    }

    //Constructor
    public PDFController(){
    }

}