package controller;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BarcodeRecognizor {
    public static String readBarCode(BufferedImage img){
        LuminanceSource ls = new BufferedImageLuminanceSource(img);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(ls));

        try{
            Result r = new MultiFormatReader().decode(bitmap);
            return r.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }

    //Scan images to find any barcodes
    public static ArrayList<String> findBarcodes(ArrayList<BufferedImage> images){
        ArrayList<String> barcodeList = new ArrayList<>();
        System.out.print("Scanning Image [");
        for(BufferedImage bi: images){
            System.out.print("-");
            String s = BarcodeRecognizor.readBarCode(bi);
            if(s!=null)
                barcodeList.add(s);
            else
                barcodeList.add("[unrecognizable]");
        }
        System.out.println("]");
        return barcodeList;
    }
}
