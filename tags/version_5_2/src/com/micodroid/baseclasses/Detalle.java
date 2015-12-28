package com.micodroid.baseclasses;

public class Detalle {
	private long id;
	private int cantidad;
	private String descripcion;
	private float valor_unitario;
	
	public Detalle (long id, int cantidad, String descripcion, float valor_unitario) {
		this.setCantidad(cantidad);
		this.setDescripcion(descripcion);
		this.setValor_unitario(valor_unitario);
		this.id = id;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase().trim();
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setValor_unitario(float valor_unitario) {
		this.valor_unitario = valor_unitario;
	}

	public float getValor_unitario() {
		return valor_unitario;
	}	

	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
}
