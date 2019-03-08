package util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class IOController {
    Robot robot;
    Scanner kb;

    //Read ints
    public int readInt(){
        int i = kb.nextInt();
        kb.nextLine();
        return i;
    }
    //Read Strings
    public String readString(){
        return kb.nextLine();
    }
    
    //Load images
    public ArrayList<BufferedImage> loadImages(){
        File dir = chooseDir(findDirs("images"));
        File[] files = searchDirectory("jpg", "images/"+dir.getName());
        ArrayList<BufferedImage> imgs = new ArrayList<>();
        if(files == null) {
            return null;
        }
        try{
            for(File f: files)
                imgs.add(ImageIO.read(f));
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
        return imgs;
    }
    
    public ArrayList<String> loadBarcodes(){
        File barcodes = chooseFile(searchDirectory("txt"));
        ArrayList<String> bc = new ArrayList<>();
        try{
            FileReader fr = new FileReader(barcodes);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null){
                bc.add(line);
                line  = br.readLine();
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return bc;
    }

    //Choose file from Filearray
    public File chooseFile(File[] files){
        int answer = -1;
        while(answer < 1 || answer > files.length){
            System.out.print("Choose which file from "+1+"-"+files.length+">");
            answer = readInt();
        }
        System.out.println("File ["+files[answer-1].getName()+"] chosen.");
        return files[answer-1];
    }
    
    //Choose directory
    public File chooseDir(File[] dirs){
        for(int i = 0; i<dirs.length; i++){
            System.out.println((i+1)+". "+dirs[i]);
        }
        return dirs[readInt()-1];
    }

    //Finds directories
    public File[] findDirs(String startDir){
        File dir = new File(System.getProperty("user.dir")+"/"+startDir);
        return dir.listFiles();
    }
    
    //Searches a directory for files
    public File[] searchDirectory(String fileType){
        return searchDirectory(fileType, fileType);
    }
    public File[] searchDirectory(String fileType, String path){
        File dir = new File(System.getProperty("user.dir")+"/"+path);
        System.out.println(dir.getAbsolutePath());
        File[] matchingFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(fileType);
            }
        });
        if(matchingFiles == null || matchingFiles.length == 0){
            System.out.println("No files found");
            return null;
        } else if(matchingFiles.length > 1) {
            System.out.println("Files found: [");
            for (int i = 0; i < matchingFiles.length; i++) {
                System.out.println(i + 1 + ". " + matchingFiles[i].getName());
            }
            System.out.println("]");
        }else if(matchingFiles.length == 1){
            System.out.println("One file found: ["+matchingFiles[0].getName()+"]");
        }
        return matchingFiles;
    }

    //Print barcodelist
    public void printBarcodes(ArrayList<String> barcodes){
        if(barcodes!=null)
            for(int i = 0; i < barcodes.size(); i++){
                System.out.println("Page "+(i+1)+": "+barcodes.get(i));
            }
        else System.out.println("Nothing in the Barcode list...");
    }

    //Save images made from a pdf to disk
    public void saveImages(ArrayList<BufferedImage> images, String folderName){
        if (images != null)
            try {
                new File("images/"+folderName).mkdirs();
                for (int i = 0; i < images.size(); i++) {
                    ImageIOUtil.writeImage(images.get(i), "images/"+folderName+"/img_" + i + ".jpg", 400);
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        else System.out.println("No images to save...");
    }

    //Saves barcodes to file
    public void saveBarCodes(ArrayList<String> barcodes){
        try{
            File output = new File("txt/Barcodes.txt");
            FileOutputStream fos = new FileOutputStream(output);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for(String s: barcodes){
                bw.write(s);
                bw.newLine();
            }
            bw.close();
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Barcodes");
    }

    //Saves the pdf to disk
    public void savePDF(PDDocument pdf){
        savePDF(pdf,"cropped");
    }
    public void savePDF(PDDocument pdf, String name){
        try{
            Date d = new Date();
            pdf.save("pdfout/"+d.getTime()+"-"+name+".pdf");
            System.out.println("PDF Saved...");
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void typeString(){

    }

    public IOController(){
        kb = new Scanner(System.in);

    }
}
