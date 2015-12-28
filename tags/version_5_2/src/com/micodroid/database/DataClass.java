package com.micodroid.database;

import java.util.ArrayList;
import java.util.List;
import com.micodroid.baseclasses.Cliente;
import com.micodroid.baseclasses.Contribuyente;
import com.micodroid.baseclasses.Detalle;
import com.micodroid.baseclasses.Factura;

import android.content.Context;

public class DataClass {

	
	private static DataClass instance;
	private Context context;
	private DBAdapter dbHelper;
	private Contribuyente contribuyente = null;
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private List<Factura> facturas = new ArrayList<Factura>();
	public boolean actualizar_facturas = false;

	/**
	 * Constructor
	 * @param context
	 */
	
	public DataClass(Context context) {
		this.context = context;
		dbHelper = new DBAdapter(this.context);
		setClientes(dbHelper.fetchClientes());
		setContribuyente(dbHelper.fetchContribuyente());
		setFacturas(dbHelper.fecthFacturas());
	}
	
	/* Regresa la información de los detalles
	 * @return Detalles de la Factura Especificada
	 */
	public List<Detalle> fetchDetalles(long factura) {
		return dbHelper.fetchDetalles(factura);
	}
	
	public void ActualizaFacturas() {
		setFacturas(dbHelper.fecthFacturas());
	}
	
	
	/***
	 * Actualiza Factura
	 * @param id
	 * @param fecha
	 * @param lugar
	 * @param folio
	 * @param cliente
	 * @param iva
	 * @return
	 */
	public int updateFactura(long id, String fecha, String lugar, String folio, long cliente, float iva) {
		return dbHelper.updateFactura(id, fecha, lugar, folio, cliente, iva);
	}
	
	
	/**
	 * Se inicializa instancia
	 * @param context
	 */
	
	public static void initInstance(Context context)

	{
		if (instance == null) {
			instance = new DataClass(context);	
		}
	}
	
	/**
	 * Obtiene la instancia
	 * @return
	 */
	
	public static DataClass getInstance() {
		return instance;
	}	
	
	/**
	 * Asignamos listado de clientes
	 * @param clientes
	 */

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	/**
	 * Obtenemos listado de clientes
	 * @return
	 */
	
	public List<Cliente> getClientes() {
		return clientes;
	}
	
	/**
	 * Eliminamos Clientes
	 * @param id
	 * @return número de clientes eliminados, -1 en caso de error
	 */
	
	public int deleteCliente(long id)  {
		return dbHelper.deleteCliente(id);
	}
	

	/***
	 * Función para insertar un nuevo cliente
	 * @param nombre
	 * @param rfc
	 * @param direccion
	 * @return -1 en caso de error o el ID del nuevo registro
	 */
	
	public long insertCliente(String nombre, String rfc, String direccion) {
		return dbHelper.insertCliente(nombre, rfc, direccion);
	}
	
	/***
	 * Actualizamos los datos del Cliente
	 * @param id
	 * @param nombre
	 * @param rfc
	 * @param direccion
	 * @return número de registros afectados
	 */
	public int updateCliente(long id, String nombre, String rfc, String direccion) {
		return dbHelper.updateCliente(id, nombre, rfc, direccion);
	}
	
	/**
	 * Asignamos el objeto Contribuyente
	 * @param contribuyente
	 */
	
	public void setContribuyente(Contribuyente contribuyente) {
		this.contribuyente = contribuyente;
	}

	/**
	 * Obtenemos el objeto Contribuyente
	 * @return Contribuyente
	 */
	
	public Contribuyente getContribuyente() {
		return contribuyente;
	}
	
	/***
	 * Actualizamos Fecha de Folios
	 * @param fecha del folio en milisegundos
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateFechaFolios(String fecha_folios ){
		return dbHelper.updateFechaFolios(fecha_folios);
	}
	
	/***
	 * Actualizamos los datos del Contribuyente
	 * @param nombre
	 * @param rfc
	 * @param curp
	 * @param direccion
	 * @param iva
	 * @param sicofi
	 * @return número de elementos actualizados
	 */
	public int updateContribuyente(String nombre, String rfc, String curp, String direccion, String iva, String sicofi) {
		return dbHelper.updateContribuyente(nombre, rfc, curp, direccion, iva, sicofi);
	}
	
	/***
	 * Actualizamos Imagen QR
	 * @param imagen QR
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateImagenQR(byte[] imagen ){
		return dbHelper.updateImagenQR(imagen);
	}
			
	/**
	 * Asignamos Listado de Facturas
	 * @param facturas
	 */
	
	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

	/**
	 * Obtenemos Listado de Facturas
	 * @return
	 */
	public List<Factura> getFacturas() {
		return facturas;
	}
	
	/***
	 * Elimina una factura y su detalle
	 * @param id
	 * @return número de registros eliminados,  -1 referencia de inegridad
	 */
	
	public int deleteFactura(long id) {
		return dbHelper.deleteFactura(id);
	}
	
	/***
	 * Desactiva Factura
	 * @param id
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateDesactivaFactura(long id) {
		return dbHelper.updateDesactivaFactura(id);
	}
	
	/***
	 * Activa Factura
	 * @param id
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateActivaFactura(long id) {
		return dbHelper.updateActivaFactura(id);
	}
	
	
	/**
	 * Insertamos datos de la Factura
	 * @param fecha
	 * @param lugar
	 * @param folio
	 * @param cliente
	 * @param iva
	 * @return
	 */

	public long insertFactura(String fecha, String lugar, String folio, long cliente, float iva) {
		return dbHelper.insertFactura(fecha, lugar, folio, cliente, iva);
	}
	
	/***
	 * Función para insertar dellates a la Factura
	 * @param ID FACTURA
	 * @param OBJETO DETALLE
	 * @return -1 en caso de error o el ID del nuevo registro
	 */
	
	public long insertDetalles(long factura, Detalle detalle) {
		return dbHelper.insertDetalles(factura, detalle);
	}
	
	/**
	 * ELIMINA DETALLES DE LA FACTURA
	 * @param factura
	 * @return número de elementos eliminados
	 */
	public int deleteDetalles(long factura) {
		return dbHelper.deleteDetalles(factura);
	}
	
	
	
	
}



 
