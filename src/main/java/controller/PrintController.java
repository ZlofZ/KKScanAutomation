package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttributeSet;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PrintController {
    private PrintService ps;
    private PrintServiceAttributeSet psas;



    public void printPDF(PDDocument pdf){
        for(Attribute a: psas.toArray()){
            System.out.println("Attribute: "+a.getName()+"\nCategory: "+a.getCategory().getName());
        }
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPageable(new PDFPageable(pdf));
        try{
            pj.setPrintService(ps);
            //pj.print();
        }catch (PrinterException pe){
            System.out.println(pe);
        }

    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                System.out.println("Printer ["+printerName+"] found.");
                return printService;
            }
        }
        return null;
    }

    public PrintController(){
        ps = findPrintService("RICOH MP C3504ex");
        psas = ps.getAttributes();
    }
}
