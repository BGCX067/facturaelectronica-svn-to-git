package com.micodroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/***
 * Clase Base para el manejo de la base de datos en SQLite
 * @author micogeek
 *
 */

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "applicationdata";
 	private static final int DATABASE_VERSION = 2;
  	
	/**
	 * Sentencia de creación de la Base de Datos
	 */
	private static final String DATABASE_CREATE = "CREATE TABLE contribuyente (nombre TEXT, rfc TEXT, curp TEXT, direccion TEXT, iva NUMERIC DEFAULT 0, sicofi TEXT, qr BLOB, fecha_folios TEXT);";
	private static final String DATABASE_CREATE2 =	"CREATE TABLE clientes (id INTEGER PRIMARY KEY AUTOINCREMENT , nombre TEXT, rfc TEXT, direccion TEXT);";
	private static final String DATABASE_CREATE3 =	"CREATE TABLE facturas (id INTEGER PRIMARY KEY AUTOINCREMENT , fecha TEXT, lugar TEXT, folio TEXT, cliente INTEGER, iva NUMERIC DEFAULT 0, estado INTEGER DEFAULT 1);";
	private static final String DATABASE_CREATE4 =	"CREATE TABLE detalles (id INTEGER PRIMARY KEY AUTOINCREMENT , factura INTEGER, cantidad INTEGER, descripcion TEXT, precio_unitario NUMERIC);";

	private static final String INICIALIZA_DATOS = "INSERT INTO contribuyente VALUES ('','','','',null,'', null,'362327000000');";
	
	private static final String VIEW_FACTURAS = "CREATE VIEW view_facturas AS " +
			"SELECT facturas.id, facturas.fecha, facturas.lugar, facturas.folio, facturas.cliente, facturas.iva, facturas.estado,  clientes.nombre , (SELECT  SUM(cantidad*precio_unitario)* (CAST(16 AS REAL)/100+1) FROM detalles WHERE factura = facturas.id )  AS monto_total " +
			"FROM facturas " +
			"LEFT JOIN clientes on clientes.id = facturas.cliente;";
	
	
	private static final String CREATE_TRIGGER_DELETE_BEFORE_CLIENTE = "CREATE TRIGGER delete_before_cliente " +
	 		"BEFORE DELETE ON clientes " +
	 		"FOR EACH ROW BEGIN " +
	 		"SELECT CASE " +
	 		"WHEN ((SELECT cliente FROM facturas WHERE cliente = OLD.id) IS NOT NULL) " +
	 		"THEN RAISE(ABORT, 'delete on table clientes violates foreign key  constraint delete_before_cliente') " +
	 		"END;END;";
	private static final String CREATE_TRIGGER_DELETE_BEFORE_FACTURA = "CREATE TRIGGER delete_before_factura " +
			"BEFORE DELETE ON facturas " +
			"FOR EACH ROW BEGIN " +
			"DELETE from detalles WHERE detalles.factura = OLD.id ; END;";     
	
	
	
	
	/***
	 * Constructor de la clase
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
 
	/***
	 * Evento lanzado para crear la base de datos
	 */
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE2);
		database.execSQL(DATABASE_CREATE3);
		database.execSQL(DATABASE_CREATE4);
		database.execSQL(VIEW_FACTURAS);
		database.execSQL(CREATE_TRIGGER_DELETE_BEFORE_CLIENTE);
		database.execSQL(CREATE_TRIGGER_DELETE_BEFORE_FACTURA);
		database.execSQL(INICIALIZA_DATOS);
		


	}
 
	/***
	 * Evento iniciado al actualizar la base de datos regida por el número de versión
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(DataBaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " 	+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS contribuyente;");
		database.execSQL("DROP TABLE IF EXISTS clientes");
		database.execSQL("DROP TABLE IF EXISTS facturas");
		database.execSQL("DROP TABLE IF EXISTS detalles");
		database.execSQL("DROP VIEW IF EXISTS view_facturas");

		database.execSQL("DROP TRIGGER IF EXISTS delete_before_cliente");
		database.execSQL("DROP TRIGGER IF EXISTS delete_before_factura");
		onCreate(database);
	}

}
