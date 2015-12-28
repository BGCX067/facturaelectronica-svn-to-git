package com.micodroid.database;

import java.util.ArrayList;
import java.util.List;

import com.micodroid.baseclasses.Cliente;
import com.micodroid.baseclasses.Contribuyente;
import com.micodroid.baseclasses.Detalle;
import com.micodroid.baseclasses.Factura;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;


public class DBAdapter {
	 
	//Campos de la BD
	private static final String DATABASE_TABLE_CONTRIBUYENTES = "contribuyente";
	private static final String DATABASE_TABLE_CLIENTES = "clientes";
	private static final String DATABASE_TABLE_FACTURAS = "facturas";
	private static final String DATABASE_TABLE_DETALLES = "detalles";
	private static final String VIEW_FACTURAS = "view_facturas";

	
	public static final String ID = "id" ;
	public static final String ID_FACTURA = "factura" ;
	public static final String CANTIDAD = "cantidad" ;
	public static final String DESCRIPCION = "descripcion" ;
	public static final String PRECIO_UNITARIO = "precio_unitario" ;
	
	public static final String NOMBRE = "nombre" ;
	public static final String RFC = "rfc" ;
	public static final String LUGAR = "lugar" ;
	public static final String CURP_CONTRIBUYENTE = "curp" ;
	public static final String DIRECCION = "direccion" ;
	public static final String IVA = "iva" ;
	public static final String SICOFI_CONTRIBUYENTE = "sicofi" ;
	public static final String IMAGEN_QR = "qr" ;
	public static final String NOMBRE_CLIENTE = "nombre"; 
	public static final String FECHA_FOLIOS = "fecha_folios"; 

	
	public static final String RFC_CLIENTE = "rfc"; 
	public static final String FECHA = "fecha"; 
	public static final String FOLIO = "folio"; 
	public static final String CLIENTE = "cliente"; 
	public static final String ESTADO = "estado"; 
	public static final String MONTO_TOTAL = "monto_total"; 


	public static final int ACTIVA = 1;
	public static final int CANCELADA = 0;
	



	
	
	private Context context;
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
 
	public DBAdapter(Context context) {
		this.context = context;
	}
 
	public void open() throws SQLException {
		dbHelper = new DataBaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}
 
	public void close() {
		dbHelper.close();
	}
	
	
	/**
	 * ELIMINA DETALLES DE LA FACTURA
	 * @param factura
	 * @return número de elementos eliminados
	 */
	public int deleteDetalles(long factura) {
		int resultado = -1;
		open();
		resultado =  database.delete(DATABASE_TABLE_DETALLES, ID_FACTURA + "=" + factura, null);
		close();
		return resultado;
	}
	
	
	/***
	 * Regresa la información de los detalles
	 * @return Detalles de la Factura Especificada
	 */
	public List<Detalle> fetchDetalles(long factura) {
		open();
		List<Detalle> detalles = new ArrayList<Detalle>();
		Cursor cursor2 = database.query(DATABASE_TABLE_DETALLES, new String[] {ID, CANTIDAD, DESCRIPCION, PRECIO_UNITARIO}, ID_FACTURA + "=" + factura, null, null, null, null);
	
		cursor2.moveToFirst();	
		while (cursor2.isAfterLast() == false) {
			detalles.add( new Detalle(
					cursor2.getLong(cursor2.getColumnIndexOrThrow(DBAdapter.ID)),
					cursor2.getInt(cursor2.getColumnIndexOrThrow(DBAdapter.CANTIDAD)),
					cursor2.getString(cursor2.getColumnIndexOrThrow(DBAdapter.DESCRIPCION)), 
					cursor2.getFloat(cursor2.getColumnIndexOrThrow(DBAdapter.PRECIO_UNITARIO))
					));
			
			cursor2.moveToNext();
		}
		close();	
		return detalles;	
	}	
	
	/***
	 * Función para insertar dellates a la Factura
	 * @param ID FACTURA
	 * @param OBJETO DETALLE
	 * @return -1 en caso de error o el ID del nuevo registro
	 */
	
	public long insertDetalles(long factura, Detalle detalle) {
		open();
		long resultado = -1;
		ContentValues values = new ContentValues();
	
		values.put(ID_FACTURA, factura);
		values.put(CANTIDAD, detalle.getCantidad());
		values.put(DESCRIPCION, detalle.getDescripcion());
		values.put(PRECIO_UNITARIO, detalle.getValor_unitario());
		resultado = database.insert(DATABASE_TABLE_DETALLES,null, values);
		close();
		return resultado;
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
		open();
		int resultado = 0;
		ContentValues values = new ContentValues();
		values.put(FECHA, fecha);
		values.put(LUGAR, lugar.toUpperCase());
		values.put(FOLIO, folio.toUpperCase());
		values.put(CLIENTE, cliente);
		values.put(IVA, iva);	
		resultado = database.update(DATABASE_TABLE_FACTURAS, values,ID + "=" + id, null);
		close();	
		return resultado;
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
		open();
		long resultado = 0;
		ContentValues values = new ContentValues();
		
		values.put(FECHA, fecha);
		values.put(LUGAR, lugar.toUpperCase());
		values.put(FOLIO, folio.toUpperCase());
		values.put(CLIENTE, cliente);
		values.put(IVA, iva);
		
		resultado = database.insert(DATABASE_TABLE_FACTURAS, null, values);
		close();
		return resultado;
	}		
	
	/***
	 * Desactiva Factura
	 * @param id
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateDesactivaFactura(long id) {
		open();
		int resultado = 0;
		ContentValues values = new ContentValues();
		values.put(ESTADO, CANCELADA);		
		resultado = database.update(DATABASE_TABLE_FACTURAS, values,ID + "=" + id, null);
		close();	
		return resultado;
	}
	
	/***
	 * Activa Factura
	 * @param id
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateActivaFactura(long id) {
		open();
		int resultado = 0;
		ContentValues values = new ContentValues();
		values.put(ESTADO, ACTIVA);		
		resultado = database.update(DATABASE_TABLE_FACTURAS, values,ID + "=" + id, null);
		close();	
		return resultado;
	}
	
	/***
	 * Elimina una factura y su detalle
	 * @param id
	 * @return número de registros eliminados,  -1 referencia de inegridad
	 */
	
	public int deleteFactura(long id) {
		int resultado = -1;
		open();
		resultado =  database.delete(DATABASE_TABLE_FACTURAS, ID + "=" + id, null);
		close();
		return resultado;
	}
	
	/***
	 * 	Regresa las facturas
	 * @return List<Factura>
	 */
	
	public List<Factura> fecthFacturas() {
		open();
		Cursor cursor = database.query(VIEW_FACTURAS, new String[] {ID, FECHA, LUGAR,  FOLIO, CLIENTE, IVA, ESTADO, NOMBRE_CLIENTE, MONTO_TOTAL}, null, null, null, null, FOLIO + " DESC");
        List<Factura> facturas = new ArrayList<Factura>();
        cursor.moveToFirst();

		while (cursor.isAfterLast() == false) {
		
			facturas.add(new Factura(
		
					cursor.getLong(cursor.getColumnIndexOrThrow(DBAdapter.ID)), 
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.FECHA)),
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.LUGAR)),
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.FOLIO)),
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.NOMBRE_CLIENTE)),
					cursor.getLong(cursor.getColumnIndexOrThrow(DBAdapter.CLIENTE)),
					cursor.getFloat(cursor.getColumnIndexOrThrow(DBAdapter.IVA)),
					cursor.getInt(cursor.getColumnIndexOrThrow(DBAdapter.ESTADO)),
					cursor.getFloat(cursor.getColumnIndexOrThrow(DBAdapter.MONTO_TOTAL))
			));
			
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) cursor.close();
		close();
		return facturas;	
	}
	
	/***
	 * Actualizamos Fecha de Folios
	 * @param fecha del folio en milisegundos
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateFechaFolios(String fecha_folios ){
		open();
		int resultado = 0;
		ContentValues values = new ContentValues();
		values.put(FECHA_FOLIOS, fecha_folios);		
		resultado = database.update(DATABASE_TABLE_CONTRIBUYENTES, values,null, null);
		close();	
		return resultado;
	}
	
	/***
	 * Actualizamos Imagen QR
	 * @param imagen QR
	 * @return número de registros afectados por la actualización
	 */
	
	public int updateImagenQR(byte[] imagen ){
		open();
		int resultado = 0;
		ContentValues values = new ContentValues();
		values.put(IMAGEN_QR, imagen);		
		resultado = database.update(DATABASE_TABLE_CONTRIBUYENTES, values,null, null);
		close();	
		return resultado;
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
		open();
		int resultado = 0;
		float _iva = 0;
		try { _iva =  Float.parseFloat(iva); }
		catch (Exception exc) { }
				
		ContentValues values = new ContentValues();
		values.put(NOMBRE, nombre.toUpperCase());
		values.put(RFC, rfc.toUpperCase());
		values.put(CURP_CONTRIBUYENTE, curp.toUpperCase());
		values.put(DIRECCION, direccion.toUpperCase());
		if(_iva == 0) {
			values.putNull(IVA);
		}
		else {
			values.put(IVA, _iva);
		}
		values.put(SICOFI_CONTRIBUYENTE, sicofi.toUpperCase());
		resultado = database.update(DATABASE_TABLE_CONTRIBUYENTES, values,null, null);
		close();	
		return resultado;
	}
	
	
	/***
	 * Regresa la información del contribuyente
	 * @return Contribuyente con los datos del contribuyente
	 */
	public Contribuyente fetchContribuyente() {
		open();
		Contribuyente contribuyente = null;
		Cursor cursor = database.query(DATABASE_TABLE_CONTRIBUYENTES, new String[] {NOMBRE, RFC, CURP_CONTRIBUYENTE, DIRECCION, IVA, SICOFI_CONTRIBUYENTE, IMAGEN_QR, FECHA_FOLIOS}, null, null, null, null, null);
        		
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			contribuyente = new Contribuyente(
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.NOMBRE)), 
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.RFC)), 
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.CURP_CONTRIBUYENTE)), 
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.DIRECCION)), 
					cursor.getFloat(cursor.getColumnIndexOrThrow(DBAdapter.IVA)), 
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.SICOFI_CONTRIBUYENTE)),
					cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.FECHA_FOLIOS)),
					cursor.getBlob(cursor.getColumnIndexOrThrow(DBAdapter.IMAGEN_QR)));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		close();		
		return contribuyente;	
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
		open();
		int resultado = 0;
		ContentValues values = new ContentValues();
		values.put(NOMBRE, nombre.toUpperCase());
		values.put(RFC, rfc.toUpperCase());
		values.put(DIRECCION, direccion.toUpperCase());
		resultado = database.update(DATABASE_TABLE_CLIENTES, values, ID + "=" + id, null);
		close();
		return resultado;
	}	
	
	/***
	 * Función para insertar un nuevo cliente
	 * @param nombre
	 * @param rfc
	 * @param direccion
	 * @return -1 en caso de error o el ID del nuevo registro
	 */
	
	public long insertCliente(String nombre, String rfc, String direccion) {
		open();
		long resultado = -1;
		ContentValues values = new ContentValues();
		values.put(NOMBRE, nombre.toUpperCase());
		values.put(RFC, rfc.toUpperCase());
		values.put(DIRECCION, direccion.toUpperCase());
		resultado = database.insert(DATABASE_TABLE_CLIENTES,null, values);
		close();
		return resultado;
	}
	
	/***
	 * Elimina un Cliente
	 * @param id
	 * @return número de registros eliminados,  -1 referencia de inegridad
	 */
	
	public int deleteCliente(long id) {
		open();
		int resultado = -1;
		try{
			resultado =  database.delete(DATABASE_TABLE_CLIENTES, ID + "=" + id, null);
			}
		catch(SQLiteConstraintException  constraintException) {
			resultado = -1;
			}
		close();
		return resultado;
	}
		
	/***
	 * Regresa la información de los Clientes
	 * @return List<Cliente> con el registro de los clientes
	 */
	public List<Cliente> fetchClientes() {		
		open();
	    
		Cursor cursor_clientes = database.query(DATABASE_TABLE_CLIENTES, new String[] {ID, NOMBRE, RFC, DIRECCION}, null, null, null, null, NOMBRE);
        List<Cliente> clientes = new ArrayList<Cliente>();
		
        cursor_clientes.moveToFirst();
		while (cursor_clientes.isAfterLast() == false) {
			clientes.add(new Cliente(
					cursor_clientes.getLong(cursor_clientes.getColumnIndexOrThrow(DBAdapter.ID)), 
					cursor_clientes.getString(cursor_clientes.getColumnIndexOrThrow(DBAdapter.NOMBRE_CLIENTE)),
					cursor_clientes.getString(cursor_clientes.getColumnIndexOrThrow(DBAdapter.RFC_CLIENTE)),
					cursor_clientes.getString(cursor_clientes.getColumnIndexOrThrow(DBAdapter.DIRECCION))));
			cursor_clientes.moveToNext();
		}
		if (cursor_clientes != null && !cursor_clientes.isClosed()) {
			cursor_clientes.close();
		}
		close();
		return clientes;	    
	}
	
	
	
}