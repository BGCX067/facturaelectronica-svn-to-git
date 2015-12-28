package com.micodroid.baseclasses;

public class Cliente {
	private long id;
	private String nombre;
	private String rfc;
	private String direccion;
	
	public Cliente(long id, String nombre, String rfc, String direccion) {
		this.id = id;
		this.nombre = nombre.toUpperCase();
		this.rfc = rfc.toUpperCase();
		this.direccion = direccion.toUpperCase();
	}

	public void setNombre(String nombre) {
		this.nombre = nombre.toUpperCase();
	}

	public String getNombre() {
		return nombre;
	}

	public long getId() {
		return id;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc.toUpperCase();
	}

	public String getRfc() {
		return rfc;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion.toUpperCase();
	}

	public String getDireccion() {
		return direccion;
	}
	
}

