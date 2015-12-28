package com.micodroid.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.os.Environment;
import android.util.Log;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.micodroid.baseclasses.Cliente;
import com.micodroid.baseclasses.Detalle;
import com.micodroid.baseclasses.Factura;
import com.micodroid.database.DataClass;



public class CreatePDF {

	   private static final String DATASUBDIRECTORY = "MicodroidFacturas";
	   private DateFormatSymbols dfs = new DateFormatSymbols();
	   private String[] meses = dfs.getMonths();
	   private Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));
	   public static Factura factura = null;
	   
	
	public CreatePDF() {
		
	}
	
	public String PruebaPDFDescargar (int factura_pos) {
	
		
		String path = "";
		 try { 
			 
		      File dir = new File(Environment.getExternalStorageDirectory(), DATASUBDIRECTORY);
		      factura = DataClass.getInstance().getFacturas().get(factura_pos);
		      
		      Cliente cliente =  null;
		      int i;
		      for(i=0; i< DataClass.getInstance().getClientes().size() && DataClass.getInstance().getClientes().get(i).getId() != factura.getCliente(); i++);
		      if((int) i < DataClass.getInstance().getClientes().size() ) {
		    	  cliente = DataClass.getInstance().getClientes().get(i);
		      }
		      else {
		    	  cliente = new Cliente(0, "<< SIN NOMBRE DEL CLIENTE >>", "<< SIN RFC >>", "<< SIN DIRECCIÓN >>");
		    }
		    	  
		    
		    
		      
		      if (!dir.exists()) {
		         dir.mkdirs();
		      }
		   
		      // ---------------------------------------------------------------------------------- 
		    	
		      	cal.setTimeInMillis(Long.parseLong(factura.getFecha()));
				
				// Lista de objetos
				List<Detalle> listaex = new ArrayList<Detalle>();
				listaex = DataClass.getInstance().fetchDetalles(factura.getId());
				
		      
		    		
	            OutputStream file = new FileOutputStream(new File(dir, "FACTURA-" + factura.getFolio() + ".PDF")); 
	         
	            
	            PdfPTable tabla = new PdfPTable(1);
				PdfPTable tablaaux = new PdfPTable(new float[]{1.67f,5.55F,2.78F});
				Document document = new Document(PageSize.LETTER,1,1,1,1);
	            	           
				PdfWriter writer  = PdfWriter.getInstance(document, file); 
				if(factura.getEstado() == 0) writer.setPageEvent(new Watermark());	
	            
	            Font ftsencilla = new Font(Font.HELVETICA, 6, Font.NORMAL);
				ftsencilla.setColor(123,123,123);
				
				Font fttitulo = new Font(Font.HELVETICA, 24, Font.NORMAL);
				fttitulo.setColor(113,116,125);

				Font ftBlanca = new Font(Font.HELVETICA, 10, Font.NORMAL);
				ftBlanca.setColor(255,255,255);
				
				Font ftgris = new Font(Font.HELVETICA, 11, Font.NORMAL);
				ftgris.setColor(113,116,120);
				
				Font ftnegra = new Font(Font.HELVETICA, 11, Font.NORMAL);
				ftnegra.setColor(0,0,0);
				
				Font ftnegrab = new Font(Font.HELVETICA, 11, Font.BOLD);
				ftnegra.setColor(0,0,0);

				PdfPCell celda;
				Paragraph parrafo;
				Phrase frase = new Phrase();
				
				tabla.setWidthPercentage(100);
				tablaaux.setWidthPercentage(100);				
				celda = new PdfPCell();
	            
				tabla.getDefaultCell().setBorder(0);
				tablaaux.getDefaultCell().setBorder(0);

				tablaaux.addCell("");
				
				if(DataClass.getInstance().getContribuyente().getNombre().length() <= 0)
					celda = llenaCelda("<< SIN NOMBRE >>", Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, fttitulo);
				else
					celda = llenaCelda(DataClass.getInstance().getContribuyente().getNombre(), Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, fttitulo);
				celda.setPadding(20);
				tablaaux.addCell(celda);
				
				
				
				
				celda = new PdfPCell();
				celda.setBorder(0);
				ftsencilla.setColor(40,162,219);
				ftsencilla.setSize(15);
				parrafo = new Paragraph("FACTURA", ftsencilla);
				parrafo.setAlignment(Element.ALIGN_CENTER);
				celda.addElement(parrafo);
				ftsencilla.setSize(10);
				parrafo = new Paragraph();
				parrafo.add(new Chunk("FOLIO ",ftsencilla));
				ftBlanca.setColor(0,0,0);
				parrafo.add(new Chunk(factura.getFolio(),ftBlanca));
				parrafo.setAlignment(Element.ALIGN_CENTER);
				parrafo.setLeading(10);
				celda.addElement(parrafo);
				celda.setHorizontalAlignment(Element.ALIGN_CENTER);
				celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
				celda.setFixedHeight(110);
				celda.setBackgroundColor(new harmony.java.awt.Color(203,238,255));
				tablaaux.addCell(celda);
				

				tabla.addCell(tablaaux);
				
				tablaaux = new PdfPTable(new float[]{1.87f,4.03F,1.50F,2.60F});
				
				

				
				
				
				
				celda = llenaCelda("CURP",Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, ftgris);
				tablaaux.addCell(celda);
				
				if(DataClass.getInstance().getContribuyente().getCurp().length() <= 0 )
					celda = llenaCelda("<< SIN CURP >>",Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				else
					celda = llenaCelda(DataClass.getInstance().getContribuyente().getCurp(),Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				tablaaux.addCell(celda);
				
				celda = new PdfPCell();
				celda.setColspan(2);
				celda.setBorder(0);
				tablaaux.addCell(celda);
				
				celda = llenaCelda("RFC",Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE,ftgris);
				tablaaux.addCell(celda);
				
				if(DataClass.getInstance().getContribuyente().getRfc().length() <= 0)
					celda = llenaCelda("<< SIN RFC >>",Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				else
					celda = llenaCelda(DataClass.getInstance().getContribuyente().getRfc(),Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				tablaaux.addCell(celda);
				
				celda = llenaCelda("FECHA",Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE,ftgris);
				tablaaux.addCell(celda);
				
				celda = llenaCelda(cal.get(Calendar.DAY_OF_MONTH) + " " + meses[cal.get(Calendar.MONTH)] +  " " + cal.get(Calendar.YEAR),Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				tablaaux.addCell(celda);

				celda = llenaCelda("DIRECCIÓN",Element.ALIGN_RIGHT, Element.ALIGN_TOP,ftgris);
				tablaaux.addCell(celda);
				
				if(DataClass.getInstance().getContribuyente().getDireccion().length() <= 0)
					celda = llenaCelda("<< SIN DIRECCIÓN >>",Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				else
					celda = llenaCelda(DataClass.getInstance().getContribuyente().getDireccion(),Element.ALIGN_LEFT, Element.ALIGN_MIDDLE,ftnegra);
				tablaaux.addCell(celda);
				
				
				celda = llenaCelda("LUGAR",Element.ALIGN_RIGHT, Element.ALIGN_TOP,ftgris);
				tablaaux.addCell(celda);
				
				celda = llenaCelda(factura.getLugar(),Element.ALIGN_LEFT, Element.ALIGN_TOP,ftnegra);
				tablaaux.addCell(celda);
				
				tabla.addCell(tablaaux);
				
				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setFixedHeight(40);
				tabla.addCell(celda);
				
			
				tablaaux = new PdfPTable(new float[]{1.87f,5.53f,2.60f});
				
				// parte azul
				celda = llenaCelda("NOMBRE", Element.ALIGN_RIGHT, Element.ALIGN_BOTTOM, ftgris);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				celda.setFixedHeight(24);
				tablaaux.addCell(celda);

				celda = llenaCelda(cliente.getNombre(),Element.ALIGN_LEFT, Element.ALIGN_BOTTOM,ftnegra);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);		

				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);
				
				//RFC
				celda = llenaCelda("RFC CLIENTE", Element.ALIGN_RIGHT, Element.ALIGN_TOP, ftgris);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);

				celda = llenaCelda(cliente.getRfc(),Element.ALIGN_LEFT, Element.ALIGN_TOP,ftnegra);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);		

				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);
				
				//DIRECCION
				celda = llenaCelda("DIRECCIÓN", Element.ALIGN_RIGHT, Element.ALIGN_TOP, ftgris);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				celda.setFixedHeight(40);
				tablaaux.addCell(celda);

				celda = llenaCelda(cliente.getDireccion(),Element.ALIGN_LEFT, Element.ALIGN_TOP,ftnegra);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);		

				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
				tablaaux.addCell(celda);		
				
				tabla.addCell(tablaaux);

				
				
				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setFixedHeight(40);
				tabla.addCell(celda);
				
				
				
				// tabla produtos
				celda = tablaventa(listaex);
				celda.setBorder(0);
				tabla.addCell(celda);
				tabla.setSplitLate(false);
				
				
				
				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setFixedHeight(40);
				tabla.addCell(celda);
				
				celda = new PdfPCell();
				frase.add(new Chunk("NUMERO DE APROBACIÓN SICOFI ",ftnegra));
				
				if(DataClass.getInstance().getContribuyente().getSicofi().length() <= 0)
						frase.add(new Chunk("<< SIN SICOFI >>",ftnegrab));
				else
					frase.add(new Chunk(DataClass.getInstance().getContribuyente().getSicofi(),ftnegrab));
		
				
				celda.addElement(frase);
				celda.addElement(new Chunk("Pago en una sola exhibición. RÉGIMEN FISCAL: " + DataClass.getInstance().getContribuyente().getRegimen(),ftnegrab));
				celda.setPaddingLeft(30);
				celda.setBorder(0);
				tabla.addCell(celda);
				
				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setFixedHeight(10);
				tabla.addCell(celda);
				
				tablaaux = new PdfPTable(new float[]{0.69f,1.29f,8.06f});
				
				celda = new PdfPCell();
				celda.setBorder(0);
				tablaaux.addCell(celda);
	            
				/*AQUI VA EL CODIGO QT, PUEDE SER UNA IMAGEN GENERADA*/
			
				
				if(DataClass.getInstance().getContribuyente().getQr() == null) {
					
					celda = new PdfPCell();
					celda.setBorder(0);
					tablaaux.addCell(celda);
				}
				else
				{
					  Image img = Image.getInstance(DataClass.getInstance().getContribuyente().getQr()); 
					  tablaaux.addCell(img);
				}
			  
				
				

				celda = new PdfPCell();
				celda.setBorder(0);
				celda.setPaddingLeft(20);
				celda.setPaddingRight(20);
				
				Phrase frase2 = new Phrase();	
				frase2.setLeading(12f);
				frase2.add(new Chunk("Efectos Fiscales al pago",ftnegrab));
				frase2.add(Chunk.NEWLINE);
				
				
				frase2.add(new Chunk("La reproducción apócrifa de este comprobante constituye un delito en los términos de las disposiciones fiscales.",ftnegra));
				frase2.add(Chunk.NEWLINE);
				frase2.add(new Chunk("Este comprobante tendrá una vigencia de dos años contados apartir de la fecha de aprobación de la asignación de folios, la cual es ",ftnegra));
				
				
				cal.setTimeInMillis(Long.parseLong(DataClass.getInstance().getContribuyente().getFecha_folios()));
				frase2.add(new Chunk(cal.get(Calendar.DAY_OF_MONTH) + " " + meses[cal.get(Calendar.MONTH)] +  " " + cal.get(Calendar.YEAR),ftnegrab));
				frase2.add(Chunk.NEWLINE);
				frase2.add(new Chunk("Factura emitida desde ",ftnegra));
				frase2.add(new Chunk("MIS FACTURAS (C) APP ANDROID",ftsencilla));
				celda.addElement(frase2);
				
				tablaaux.addCell(celda);
				tabla.addCell(tablaaux);
				
				
				
				document.open();
				
				
				document.setMargins(1, 1, 75, 1);
				document.add(tabla);

				document.close();
				
				
	         
	            file.close(); 
	            path = dir.getPath() + "/FACTURA-" + factura.getFolio() + ".PDF";
	        	
		 
		 
		 } catch (Exception e) { 
	        	 Log.i("com.micodroid.app", e.getMessage());
	        } 
		 return path;
	}       
	      
	
	private static PdfPCell llenaCelda(String texto, int alinea,int valinea, Font ft1 ){
		PdfPCell celda;
		Chunk chk;
		chk = new Chunk(texto,ft1);
		celda = new PdfPCell(new Paragraph(chk));
		celda.setVerticalAlignment(valinea);
		celda.setHorizontalAlignment(alinea);
		celda.setBorder(0);
		return celda;
	}
	
	
	
	
	private static PdfPCell tablaventa(List<Detalle> lista){
		
		Detalle det;
		float total = 0f;
		
		PdfPTable taux = new PdfPTable(new float[]{2.07f,3.82f,1.7f,2.3f});
		taux.setWidthPercentage(100);
		PdfPCell celda;
		Font ft1 = new Font(Font.HELVETICA, 11, Font.NORMAL);
		ft1.setColor(113,116,125);
		
		Font ft2 = new Font(Font.HELVETICA, 11, Font.NORMAL);
		ft2.setColor(0,0,0);
		Font ft2b = new Font(Font.HELVETICA, 11, Font.BOLD);
		ft2b.setColor(0,0,0);
		int renglones =10;
		DecimalFormat decf = new DecimalFormat("###,###,###.##");
		if (lista.size()>10){
			renglones = 0;
		}else{
			renglones = 10 - lista.size();
		}
		
		celda = llenaCelda("CANTIDAD",Element.ALIGN_CENTER,Element.ALIGN_MIDDLE,ft1);
		taux.addCell(celda);
		celda = llenaCelda("DESCRIPCIÓN",Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft1);
		taux.addCell(celda);
		celda = llenaCelda("VALOR UNITARIO",Element.ALIGN_CENTER,Element.ALIGN_MIDDLE,ft1);
		taux.addCell(celda);
		celda = llenaCelda("IMPORTE",Element.ALIGN_CENTER,Element.ALIGN_MIDDLE,ft1);
		taux.addCell(celda);
		
		taux.setHeaderRows(1);
		
		
			for(int i=0;i<lista.size();i++ ){
				
				det = lista.get(i);
				
				
				celda = llenaCelda(String.valueOf(det.getCantidad()),Element.ALIGN_CENTER,Element.ALIGN_MIDDLE,ft2);
				celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
				celda.setBorder(Rectangle.RECTANGLE);
				celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
				taux.addCell(celda);
				
				celda = llenaCelda(det.getDescripcion(),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2);
				celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
				celda.setPaddingLeft(10);
				celda.setBorder(Rectangle.RECTANGLE);
				celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
				taux.addCell(celda);
				
				celda = llenaCelda("$ "+decf.format(det.getValor_unitario()),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2);
				celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
				celda.setPaddingLeft(10);
				celda.setBorder(Rectangle.RECTANGLE);
				celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
				taux.addCell(celda);
				
				total = total +(det.getValor_unitario()*det.getCantidad());
				celda = llenaCelda("$ "+decf.format(det.getValor_unitario() * det.getCantidad()),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2);
				celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
				celda.setPaddingLeft(10);
				celda.setBorder(Rectangle.RECTANGLE);
				celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
				taux.addCell(celda);	
			}
		
		
		for (int i =0;i<renglones; i++){
			for(int n=0;n<4;n++){
				celda = new PdfPCell();
				celda.setFixedHeight(18);
				celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
				celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
				taux.addCell(celda);			
			}
		}
		
		
		
		celda = llenaCelda("SUBTOTAL",Element.ALIGN_RIGHT,Element.ALIGN_MIDDLE,ft1);
		celda.setColspan(3);
		celda.setPaddingRight(10);
		taux.addCell(celda);
		
		
		if(total>0 ) celda = llenaCelda("$ "+decf.format(total),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2b);
		else celda = llenaCelda("$ "+ total ,Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2b);
		celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
		celda.setBorder(Rectangle.RECTANGLE);
		celda.setPaddingLeft(10);
		celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
		taux.addCell(celda);

		
		
		celda = llenaCelda("IMPORTE CON LETRA",Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft1);
		celda.setPaddingLeft(30);
		celda.setColspan(2);
		taux.addCell(celda);
		
		
		celda = llenaCelda("IVA (" + factura.getIva()  + "%)",Element.ALIGN_RIGHT,Element.ALIGN_MIDDLE,ft1);
		celda.setPaddingRight(10);
		taux.addCell(celda);
		
		float total_general = (total * factura.getIva())  / 100;
		total_general += total;
		
		celda = llenaCelda("$ "+decf.format( (total * factura.getIva())  / 100),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2b);
		celda.setBackgroundColor(new harmony.java.awt.Color(229,231,232));
		celda.setBorder(Rectangle.RECTANGLE);
		celda.setPaddingLeft(10);
		celda.setBorderColor((new harmony.java.awt.Color(255,255,255)));
		taux.addCell(celda);
		
		
		
		Num_to_Text conversor = new Num_to_Text();
		celda = llenaCelda(conversor.Convertir(String.valueOf(total_general), true),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2b);
		celda.setPaddingLeft(30);
		celda.setColspan(2);
		taux.addCell(celda);
		
		
		celda = llenaCelda("TOTAL",Element.ALIGN_RIGHT,Element.ALIGN_MIDDLE,ft1);
		celda.setPaddingRight(10);
		taux.addCell(celda);
		
		
		celda = llenaCelda("$ "+decf.format(total*((factura.getIva()/100) + 1)),Element.ALIGN_LEFT,Element.ALIGN_MIDDLE,ft2b);
		celda.setBackgroundColor(new harmony.java.awt.Color(221,242,252));
		celda.setBorder(Rectangle.RECTANGLE);
		celda.setPaddingLeft(10);
		celda.setBorderColor((new harmony.java.awt.Color(252,255,255)));
		taux.addCell(celda);
		
		
		celda = new PdfPCell();
		celda.addElement(taux);
		return celda;
	}
	
	class Watermark extends PdfPageEventHelper { 
		
		  Font watermark = new Font(Font.HELVETICA, 64, Font.BOLD);
		  
		  public void onEndPage(PdfWriter writer, Document document) { 
			  watermark.setColor(252,115,115);
			  ColumnText.showTextAligned(writer.getDirectContent(), 
		      Element.ALIGN_CENTER, new Phrase("CANCELADA", watermark), 
		      317.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45); 
		  } 
		} 
	
}





