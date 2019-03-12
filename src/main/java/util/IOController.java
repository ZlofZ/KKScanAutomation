package util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.*;


public class IOController {
    private Scanner kb;
    private Robot robot;
    private final String PATH = "\\\\trafiles02\\privat$\\toblar\\Documents\\pdfhandler\\";
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
            System.out.print("Loading");
            for(File f: files){
                imgs.add(ImageIO.read(f));
                System.out.print(".");
            }
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
        File dir = new File(PATH+startDir);
        return dir.listFiles();
    }
    
    //Searches a directory for files
    public File[] searchDirectory(String fileType){
        return searchDirectory(fileType, fileType);
    }
    public File[] searchDirectory(String fileType, String path){
        File dir = new File(PATH+"/"+path);
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

    
    public boolean saveImage(BufferedImage img, String name) throws IOException{
        return ImageIOUtil.writeImage(img, name, 400);
    }
    //Save images made from a pdf to disk
    public void saveImages(ArrayList<BufferedImage> images, String folderName){
        if (images != null)
            try {
                new File(PATH+"images/"+folderName).mkdirs();
                for (int i = 0; i < images.size(); i++) {
                    saveImage(images.get(i), "images/"+folderName+"/img_" + i + ".jpg");
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        else System.out.println("No images to save...");
    }

    //Saves barcodes to file
    public void saveBarCodes(ArrayList<String> barcodes){
        try{
            File output1 = new File("txt/Barcodes.txt");
            File output2 = new File("txt/bcnpage.txt");
            FileOutputStream fos1 = new FileOutputStream(output1);
			FileOutputStream fos2 = new FileOutputStream(output2);
	
			BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fos1));
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));

            for(int i = 0; i < barcodes.size(); i++){
                bw1.write(barcodes.get(i));
                bw1.newLine();
                bw2.write("Page "+(i+1)+": "+barcodes.get(i));
                bw2.newLine();
            }
			bw1.close();
			bw2.close();
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
    
    public BufferedImage captureScreen(Rectangle area){
        try{
            robot = new Robot();
            BufferedImage bi = robot.createScreenCapture(area);
            return bi;
        }catch(AWTException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean checkIfDelivered(BufferedImage img) throws IndexOutOfBoundsException{
        int sumRed = 0, sumGreen = 0, sumBlue = 0, whitePix = 0;
        int[] pixels = new int[img.getHeight()*img.getWidth()];
        PixelGrabber pg = new PixelGrabber(img,0,0,100,50,pixels,0, img.getWidth());
        try{
            pg.grabPixels();
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
        for(int pixel : pixels){
            int  red = (pixel & 0x00ff0000) >> 16;
            int  green = (pixel & 0x0000ff00) >> 8;
            int  blue = pixel & 0x000000ff;
            if(red == 255 && green == 255 && blue == 255){whitePix++;}
            else{
                sumRed += red;
                sumGreen += green;
                sumBlue += blue;
            }
        }
        whitePix = pixels.length - whitePix;
        sumRed /= whitePix;
        sumGreen /= whitePix;
        sumBlue /= whitePix;
        System.out.println("Red ["+sumRed/whitePix+"], Green ["+sumGreen/whitePix+"], Blue ["+sumBlue/whitePix+"]\nDelivered: "+ (sumGreen/whitePix == 2));
        return (sumGreen/whitePix == 2);
    }
    public boolean checkBarcode(String barcode){
        //type barcode into window
        typeString(barcode);
        //wait .5 sec
        robot.delay(1500);
        //type enter into window
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        //wait .5 sec
        robot.delay(3000);
        //scan window for orange or green.
        BufferedImage img = captureScreen(new Rectangle(1920+380,405,30,20));
        return checkIfDelivered(img);
    }
    
    public ArrayList<String> checkBarcodes(ArrayList<String> barcodes){
        ArrayList<String> notDelivered = new ArrayList<>();
        try{
            if(robot == null)
                robot = new Robot();
            robot.delay(2000);
            for(String code: barcodes){
                if(!checkBarcode(code)){
                    notDelivered.add(code);
                }
            }
        }catch(AWTException e){
            System.out.println(e.getMessage());
        }
        return notDelivered;
    }
    
    public void typeString(String s){
        System.out.print("writing: ");
        try{
            if(robot == null) robot = new Robot();
        }catch(AWTException e){System.out.println(e.getMessage());}
        char[] chars = s.toCharArray();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        for(char c : chars){
            System.out.print(c);
            robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
            robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
            robot.delay(50);
        }
		System.out.println();
    }

    public IOController(){
        kb = new Scanner(System.in);

    }
}
