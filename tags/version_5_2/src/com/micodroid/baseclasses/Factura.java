package com.micodroid.baseclasses;

import java.util.List;
import com.micodroid.database.DataClass;

public class Factura {
	private long id;
	private String fecha;
	private String lugar;
	private String folio;
	private String nombre_cliente;

	private int estado = 1;
	private float iva;
	private float monto_total;
	private long cliente;
	

	
	
	
	public Factura(long id, String fecha, String lugar, String folio, String nombre_cliente, long cliente , float iva, int estado, float monto_total) {
		this.id = id;
		this.fecha = fecha;
		this.setLugar(lugar);
		this.folio = folio;
		this.setNombre_cliente(nombre_cliente);
		this.cliente = cliente;
		this.iva = iva;
		this.estado = estado;
		this.monto_total = monto_total;	
	}
	
	
	
	
	public String getNombreCliente() {
		return this.nombre_cliente;
	}
	
	public void setMontoTotal(float monto_total) {
		this.monto_total = monto_total;
	}
	
	public float getMontoTotal() {
		return monto_total;
	}
	
	public int getEstado() {
		return estado;
	}
	
	public void setEstado(int estado) {
		this.estado = estado;
	}
	
	public long getId() {
		return id;
	}
	

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getFecha() {
		return fecha;
	}

	
	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getFolio() {
		return folio;
	}

	public void setCliente(long cliente) {
		this.cliente = cliente;
	}

	public long getCliente() {
		return cliente;
	}
	
	public void setIva(float iva) {
		this.iva = iva;
	}
	
	public float getIva() {
		return iva;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getLugar() {
		return lugar;
	}

	
	public List<Detalle> getDetalles() {		
		return DataClass.getInstance().fetchDetalles(this.id);
	}




	public void setNombre_cliente(String nombre_cliente) {
		this.nombre_cliente = nombre_cliente;
	}




	
}

