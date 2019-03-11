package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import util.IOController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AppRunner {
	private PDFController pCon;
	private IOController io;
	private ArrayList<BufferedImage> imgList = null;
	private ArrayList<String> barcodeListPrintable;
	private ArrayList<String> barcodeList = null;
	private PDDocument ogPdf = null;
	private PDDocument pdf = null;

	private final String FILE_NAME = "06-03-19";
	private final String PDF_PATH = "C:/Program Files/IntelliJ Idea Community/ProjectFolder/PDFHandler/src/main/resources/"+FILE_NAME+".pdf";



	//checks if IOController is null and initializes it if it is.
	private void checkIO(){
		if(io==null)
			io = new IOController();
	}

	//checks if PDFController is null and initializes it if it is.
	private void checkPdfController(){
		if(pCon ==null){
			pCon = new PDFController();
		}
	}

	//Enter unrecognized barcodes
	private void enterUnrecognized(){
		System.out.println("Enter barcodes that are unrecognizable.");
		for(int i = 0; i < barcodeList.size(); i++){
			if(barcodeList.get(i).equalsIgnoreCase("[unrecognizable]")){
				barcodeList.remove(i);
				System.out.print("Page "+(i+1)+"> ");
				String s = io.readString();
				if(!s.equalsIgnoreCase("0"))
					barcodeList.add(i, s);
				else return;
			}
		}
	}

	//Closes the open pdfs
	private void closePDFs(){
		try{
			if(pdf!=null && ogPdf!=null) {
				pdf.close();
				ogPdf.close();
			}
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	//Saves a pdf of the unrecognized pages.
	private void getNullPdf(){
		PDDocument temp = new PDDocument();
		for(int i = 0; i<pdf.getNumberOfPages(); i++){
			if(barcodeList.get(i).equalsIgnoreCase("[unrecognizable]")){
				temp.addPage(pdf.getPage(i));
			}
		}
		io.savePDF(temp,"unrecognizable");
	}

	//Standard program sequence
	private void standardSequence(){
		checkPdfController();
		checkIO();
		PDDocument pdf = pCon.loadPDF(PDF_PATH);
		pCon.cropPDF(pdf);
		imgList = pCon.extractImagesFromPdf(pdf);
		BarcodeRecognizor.findBarcodes(imgList);
		io.saveBarCodes(barcodeList);
		io.printBarcodes(barcodeList);
		io.savePDF(pdf);
		io.saveImages(imgList, "pdf");
	}
	
	private void mChoice(String choice){
		checkPdfController();
		switch (choice){
			case "Exit": System.exit(0);
				break;
			case "Load PDF":
				File[] files = io.searchDirectory("pdf");
				if(files != null){
					File f = io.chooseFile(files);
					closePDFs();
					ogPdf = pCon.fileToPDoc(f);
					pdf = pCon.fileToPDoc(f);
				}
				return;
			case "Load Barcodes":
				barcodeList = io.loadBarcodes();
				io.typeString(barcodeList);
				return;
			case "Crop PDF":
				pCon.cropPDF(pdf);
				return;
			case "Save PDF":
				io.savePDF(pdf);
				return;
			case "Save Pages":
				System.out.println("Write which pages to save, separated by comma");
				io.savePDF(pCon.savePages(pdf, io.readString().split(",")),"okonfirmerade");
				return;
			case "Extract Images":
				imgList = pCon.extractImagesFromPdf(pdf);
				return;
			case "Save Extracted Images":
				io.saveImages(imgList, new Date().getTime()+"");
				return;
			case "Scan Images for Barcodes":
				barcodeList = BarcodeRecognizor.findBarcodes(imgList);
				return;
			case "Print Barcodes":
				io.printBarcodes(barcodeList);
				return;
			case "Save Barcodes":
				io.saveBarCodes(barcodeList);
				return;
			case "Save PDF of Unrecognizable Pages":
				getNullPdf();
				return;
			case "Enter the Unrecognizable Codes":
				enterUnrecognized();
				return;
			case "Load Images":
				imgList = io.loadImages();
				return;
		}
	}
	
	private void pmen(String[] mOpts){
		for(int i = 0; i < mOpts.length; i++){
			System.out.println((i+1)+". "+mOpts[i]);
		}
		checkIO();
		mChoice(mOpts[io.readInt()-1]);
	}

	private void menu(){
		String[] menuOptions = {
				"Exit",														//0
				"Load PDF",												//1
				"Crop PDF",												//2
				"Save PDF", 											//3
				"Save Pages",											//4
				"Extract Images",										//5
				"Save Extracted Images",							//6
				"Load Barcodes",										//7
				"Scan Images for Barcodes",					//8
				"Print Barcodes",										//9
				"Save Barcodes",										//10
				"Save PDF of Unrecognizable Pages",		//11
				"Enter the Unrecognizable Codes",			//12
				"Load Images"											//13
		} ;
		ArrayList<String>  choices = new ArrayList<>();
		while(true){
			choices.clear();
			choices.add(menuOptions[1]);
			choices.add(menuOptions[7]);
			choices.add(menuOptions[13]);
			if (pdf != null){
				choices.add(menuOptions[2]);
				choices.add(menuOptions[3]);
				choices.add(menuOptions[4]);
				choices.add(menuOptions[5]);
			}
			if  (imgList != null){
				choices.add(menuOptions[6]);
				choices.add(menuOptions[8]);
			}
			if (barcodeList != null){
				choices.add(menuOptions[9]);
				choices.add(menuOptions[10]);
				if(pdf != null)
					choices.add(menuOptions[11]);
				choices.add(menuOptions[12]);
			}
			choices.add(menuOptions[0]);
			String[] temp = new String[choices.size()];
			pmen(choices.toArray(temp));
		}
	}
	
	//Menuhandler
	@Deprecated
	private void menuHandler(boolean[] menuchoices){
		checkIO();
		checkPdfController();
		int choice = -1;
		while(true){
			choice = io.readInt();
			if (menuchoices[choice]==true && choice < menuchoices.length && choice >= 0)
				switch (choice){
					case 0: System.exit(0);
						break;
					case 1:
						File[] files = io.searchDirectory("pdf");
						if(files != null){
							File f = io.chooseFile(files);
							closePDFs();
							ogPdf = pCon.fileToPDoc(f);
							pdf = pCon.fileToPDoc(f);
						}
						return;
					case 2:io.loadBarcodes();
						return;
					case 3: pCon.cropPDF(pdf);
						return;
					case 4: io.savePDF(pdf);
						return;
					case 5: System.out.println("Write which pages to save, separated by comma");
						io.savePDF(pCon.savePages(pdf, io.readString().split(",")),"okonfirmerade");
						return;
					case 6: imgList = pCon.extractImagesFromPdf(pdf);
						return;
					case 7: io.saveImages(imgList, "pdf");
						return;
					case 8: barcodeList = BarcodeRecognizor.findBarcodes(imgList);
						return;
					case 9: io.printBarcodes(barcodeList);
						return;
					case 10: io.saveBarCodes(barcodeList);
						return;
					case 11: getNullPdf();
						return;
					case 12: enterUnrecognized();
						return;
					
				}
		}
	}

	//Print menu
	@Deprecated
	private void printMenu(){
		String t1 = "3. Crop PDF\n4. Save PDF\n5. Save Pages\n6. Extract Images";
		String t2 ="\n7. Save Extracted images\n8. Search images for barcodes";
		String t3 ="\n9. Print barcodes\n10. Save barcodes\n11. Get [unrecognizable]\n12. Enter unrecognizable";
		boolean terminate = false;
		PDDocument cropped = null;
		boolean[] type1 = {true,true,true,false,false,false,false,false,false,false,false,false,false},
				type2 = {true,true,true,true,true,true,true,false,false,false,false,false,false},
				type3 = {true,true,true,true,true,true,true,true,true,false,false,false,false},
				type4 = {true,true,true,true,true,true,true,true,true,true,true,true,true};
		while(!terminate){
			System.out.println("1. Load PDF\n2. Load Barcodes");
			if(pdf==null){
				System.out.println("0. Exit");
				menuHandler(type1);
			}
			if(barcodeList != null) {

			}
			if (imgList == null){
					System.out.println(t1+"\n0. Exit");
					menuHandler(type2);
				} else if(barcodeList==null){
				System.out.println(t1+t2+"\n0. Exit");
				menuHandler(type3);
			}else{
				System.out.println(t1+t2+t3+"\n0. Exit");
				menuHandler(type4);
			}
		}
	}

	public AppRunner(){
		//printMenu();
		
		menu();
		//new ImageProcessor();
	}

}
