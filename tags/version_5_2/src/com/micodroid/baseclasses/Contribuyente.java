package com.micodroid.baseclasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Contribuyente {
	private String nombre;
	private String rfc;
	private String curp;
	private String direccion;
	private float iva;
	private String sicofi;
	private String fecha_folios;
	private byte[] qr; 
	private Bitmap bitmap = null; 
	private String regimen = "Régimen Intermedio de la Federación";
	
	public Contribuyente(String nombre, String rfc, String curp, String direccion, float iva, String sicofi, String fecha_folios, byte[] qr ) {
		this.nombre = nombre.toUpperCase();
		this.rfc = rfc;
		this.curp = curp;
		this.direccion = direccion;
		this.iva = iva;
		this.sicofi = sicofi;
		this.fecha_folios = fecha_folios;
		setQr(qr);
	}

	public Bitmap getBitmap(){
		return bitmap;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre.toUpperCase();
	}

	public String getNombre() {
		return nombre;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc.toUpperCase();
	}

	public String getRfc() {
		return rfc;
	}

	public void setCurp(String curp) {
		this.curp = curp.toUpperCase();
	}

	public String getCurp() {
		return curp;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion.toUpperCase();
	}

	public String getDireccion() {
		return direccion;
	}

	public void setIva(float iva) {
		this.iva = iva;
	}

	public float getIva() {
		return iva;
	}

	public void setSicofi(String sicofi) {
		this.sicofi = sicofi.toUpperCase();
	}

	public String getSicofi() {
		return sicofi;
	}

	public void setFecha_folios(String fecha_folios) {
		this.fecha_folios = fecha_folios;
	}

	public String getFecha_folios() {
		return fecha_folios;
	}

	public void setQr(byte[] qr) {
		this.qr = qr;
		if (qr  != null) {
			bitmap = BitmapFactory.decodeByteArray(qr, 0, qr.length);
		}
	}

	public byte[] getQr() {
		return qr;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getRegimen() {
		return regimen;
	}
	
}

