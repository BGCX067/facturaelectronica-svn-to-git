package com.micodroid.tools;

import java.util.Comparator;

import com.micodroid.baseclasses.Cliente;

public class IgnoreClienteCaseComparator implements Comparator<Cliente> {
	  public int compare(Cliente cliente1, Cliente cliente2) {
		    return cliente1.getNombre().compareToIgnoreCase(cliente2.getNombre());
		  }

}
