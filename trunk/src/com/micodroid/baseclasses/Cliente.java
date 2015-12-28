package com.micodroid.baseclasses;

public class Cliente {
	private long id;
	private String nombre;
	private String rfc;
	private String direccion_fiscal;
	private String email;
	
	public Cliente(long id, String nombre, String rfc, String direccion_fiscal, String email) {
		this.setId(id);
		this.setNombre(nombre.toUpperCase());
		this.setRfc(rfc.toUpperCase());
		this.setDireccion_fiscal(direccion_fiscal.toUpperCase());
		this.setEmail(email.toUpperCase());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getDireccion_fiscal() {
		return direccion_fiscal;
	}

	public void setDireccion_fiscal(String direccion_fiscal) {
		this.direccion_fiscal = direccion_fiscal;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}

