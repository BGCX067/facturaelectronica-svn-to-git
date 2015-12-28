package com.micodroid.tools;

import java.util.Comparator;
import com.micodroid.baseclasses.Factura;

public class IgnoreFacturaCaseComparator implements Comparator<Factura> {
	  public int compare(Factura factura1, Factura factura2) {
		    return factura2.getFolio().compareToIgnoreCase(factura1.getFolio());
		  }

}
