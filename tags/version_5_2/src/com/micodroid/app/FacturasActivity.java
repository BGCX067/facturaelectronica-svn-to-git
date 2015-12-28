package com.micodroid.app;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import com.micodroid.baseclasses.Cliente;
import com.micodroid.baseclasses.Detalle;
import com.micodroid.baseclasses.Factura;
import com.micodroid.database.DBAdapter;
import com.micodroid.database.DataClass;
import com.micodroid.listadapter.ListAdapter_Detalles;
import com.micodroid.listadapter.ListAdapter_Facturas;
import com.micodroid.listadapter.SpinnerAdapter_Clientes;
import com.micodroid.tools.IgnoreFacturaCaseComparator;
import com.micodroid.tools.CreatePDF;
import com.micodroid.tools.Validaciones;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FacturasActivity extends Activity {

	private EditText add_factura_folio;
	private EditText add_factura_lugar;

	private EditText add_detalle_cantidad;
	private EditText add_detalle_descripcion;
	private EditText add_detalle_precio;

	private List<Detalle> detalles = new ArrayList<Detalle>();
	private TextView add_factura_fecha;

	private DateFormatSymbols dfs = new DateFormatSymbols();
	private String[] meses = dfs.getMonths();
	private Calendar cal;

	private ListView listFacturas;
	private ListView listDetalles;

	private ListAdapter_Detalles adapter_detalles;
	private ListAdapter_Facturas adapter_facturas;
	private Factura factura = null;

	private View view_custom;
	private View view_fecha;
	private View view_detalles;

	private AlertDialog alertdialog_custom;
	private AlertDialog alertdialog_fecha;
	private AlertDialog alertdialog_detalles;

	private Spinner spinner_clientes;
	private SpinnerAdapter_Clientes adapter_spinner_cientes;
	private boolean detalles_edit = false;
	private boolean facturas_edit = false;
	private int detalles_select_edit = 0;
	private int factura_select_edit = 0;

	private ProgressDialog pbarDialog;
	// private Calendar calendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.facturas);
		DataClass.initInstance(this);

		LayoutInflater inflater = LayoutInflater.from(this);

		// Creamos un AlertDialog para los detalles

		view_detalles = inflater.inflate(R.layout.detalles_add, null);
		AlertDialog.Builder alt_detalles = new AlertDialog.Builder(this);
		alt_detalles.setView(view_detalles);
		alt_detalles.setCancelable(true);
		alt_detalles.setTitle(R.string.titulo_seccion_detalles);
		alt_detalles.setIcon(R.drawable.ic_light_detalles);
		alertdialog_detalles = alt_detalles.create();

		// Creamos un AlertDialog para la fecha del FOLIO
		view_fecha = inflater.inflate(R.layout.fechas_datos, null);
		AlertDialog.Builder alt_fecha = new AlertDialog.Builder(this);
		alt_fecha.setView(view_fecha);
		alt_fecha.setCancelable(true);
		alt_fecha.setTitle(R.string.sutitulo_seccion_fecha_factura);
		alt_fecha.setIcon(R.drawable.ic_light_facturas);
		alertdialog_fecha = alt_fecha.create();

		// Creamos un AlertDialog para agregar una FACTURA
		view_custom = inflater.inflate(R.layout.facturas_add, null);
		AlertDialog.Builder alt_dialog = new AlertDialog.Builder(this);
		alt_dialog.setView(view_custom).setCancelable(true)
				.setTitle(R.string.titulo_seccion_facturas);
		alertdialog_custom = alt_dialog.create();
		alertdialog_custom.setTitle(getResources().getText(R.string.titulo_seccion_facturas));
		alertdialog_custom.setIcon(R.drawable.ic_light_add_factura);

		spinner_clientes = (Spinner) view_custom.findViewById(R.id.spinner_clientes);
		adapter_spinner_cientes = new SpinnerAdapter_Clientes(this,	android.R.layout.simple_spinner_item, DataClass.getInstance().getClientes());
		adapter_spinner_cientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_clientes.setAdapter(adapter_spinner_cientes);

		listFacturas = (ListView) findViewById(R.id.lv_facturas);
		fill_data();
		registerForContextMenu(listFacturas);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		if (v.getId() == R.id.lv_facturas) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			menu.setHeaderTitle(DataClass.getInstance().getFacturas()
					.get(info.position).getFolio());
			menu.setHeaderIcon(R.drawable.ic_light_facturas);

			String[] menuItems = getResources().getStringArray(
					R.array.menu_contextual_facturas);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}

			if (DataClass.getInstance().getFacturas().get(info.position)
					.getEstado() == DBAdapter.CANCELADA) {
				menu.getItem(0).setTitle(
						getString(R.string.menu_activar_factura));
			}

		}

		if (v.getId() == R.id.lv_detalles) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			menu.setHeaderTitle(detalles.get(info.position).getDescripcion());
			menu.setHeaderIcon(R.drawable.ic_light_detalles);

			String[] menuItems = getResources().getStringArray(
					R.array.menu_contextual_detalles);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
				menu.getItem(i).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {

							public boolean onMenuItemClick(MenuItem item) {
								AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
										.getMenuInfo();
								switch (item.getItemId()) {
								case 0:
									detalles_edit = true;
									detalles_select_edit = info.position;
									get_fields_detalles();
									clear_fields_detalles();
									alertdialog_detalles.show();
									add_detalle_cantidad.setText(String
											.valueOf(detalles
													.get(info.position)
													.getCantidad()));
									add_detalle_descripcion.setText(String
											.valueOf(detalles
													.get(info.position)
													.getDescripcion()));
									add_detalle_precio.setText(String
											.valueOf(detalles
													.get(info.position)
													.getValor_unitario()));
									break;
								case 1:
									detalles.remove(info.position);
									adapter_detalles.notifyDataSetChanged();
									break;
								}
								return true;

							}
						});
			}

		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		factura = DataClass.getInstance().getFacturas().get(info.position);
		switch (item.getItemId()) {
		case 0:
			if (factura.getEstado() == DBAdapter.ACTIVA) {
				DataClass.getInstance().updateDesactivaFactura(factura.getId());
				DataClass.getInstance().getFacturas().get(info.position)
						.setEstado(DBAdapter.CANCELADA);
				Toast.makeText(this,
						getResources().getText(R.string.registro_cancel),
						Toast.LENGTH_LONG).show();
			} else {
				DataClass.getInstance().updateActivaFactura(factura.getId());
				DataClass.getInstance().getFacturas().get(info.position)
						.setEstado(DBAdapter.ACTIVA);
				Toast.makeText(this,
						getResources().getText(R.string.registro_activado),
						Toast.LENGTH_LONG).show();
			}
			adapter_facturas.notifyDataSetChanged();
			break;

		case 1:
			facturas_edit = true;
			factura_select_edit = info.position;

			detalles.clear();
			detalles = null;
			detalles = new ArrayList<Detalle>();
			detalles = DataClass.getInstance().getFacturas().get(info.position).getDetalles();

			get_fields();
			registerForContextMenu(listDetalles);
			listDetalles.setOnCreateContextMenuListener(this);

			add_factura_folio.setText(DataClass.getInstance().getFacturas().get(info.position).getFolio());
			add_factura_lugar.setText(DataClass.getInstance().getFacturas().get(info.position).getLugar());

			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));
			cal.setTimeInMillis(Long.parseLong(DataClass.getInstance().getFacturas().get(info.position).getFecha()));
			add_factura_fecha.setText("Fecha: "
					+ cal.get(Calendar.DAY_OF_MONTH) + " "
					+ meses[cal.get(Calendar.MONTH)].substring(0, 3) + " "
					+ cal.get(Calendar.YEAR));

			
			spinner_clientes.setSelection(positionCliente(DataClass.getInstance().getFacturas().get(info.position).getCliente()));
			alertdialog_custom.show();

			break;
		case 2:

			switch (DataClass.getInstance().deleteFactura(factura.getId())) {
			case 0:

				Toast.makeText(this,
						getResources().getText(R.string.error_delete),
						Toast.LENGTH_LONG).show();
				break;
			default:
				DataClass.getInstance().getFacturas().remove(info.position);
				adapter_facturas.notifyDataSetChanged();
				Toast.makeText(
						this,
						getResources()
								.getText(R.string.registro_factura_delete),
						Toast.LENGTH_SHORT).show();
				break;
			}

			break;

		case 3:
			
			factura_select_edit = info.position;
			
			pbarDialog = new ProgressDialog( this );
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pbarDialog.setMessage(getString(R.string.generando_factura));
			pbarDialog.setCancelable(false);
			pbarDialog.show();
			InitTask _initTask ;
			_initTask = new InitTask();
			_initTask.execute(this);
			
			break;
		}
		return true;
	}

	
	private int positionCliente(long id) {
		int pos = -1;
		int item;
		for(item=0; item<DataClass.getInstance().getClientes().size() && id != DataClass.getInstance().getClientes().get(item).getId(); item++) ;
		if(item<DataClass.getInstance().getClientes().size()) pos = item;
		return pos;
	}
	
	/***
	 * Se obtiene List<Factura> de las Facturas
	 */

	private void fill_data() {
		adapter_facturas = new ListAdapter_Facturas(this, DataClass
				.getInstance().getFacturas());
		listFacturas.setAdapter(adapter_facturas);
	}

	/***
	 * Volvemos a cargar la instancia en caso de que sea necesario
	 */

	@Override
	protected void onResume() {
		super.onResume();
		DataClass.initInstance(this);

		if (DataClass.getInstance().actualizar_facturas == true) {
			DataClass.getInstance().ActualizaFacturas();
			adapter_spinner_cientes.notifyDataSetChanged();
			DataClass.getInstance().actualizar_facturas = false;
			fill_data();
		}
	}

	/***
	 * Obtenemos los objetos del formulario
	 */

	private void get_fields() {

		add_factura_folio = (EditText) view_custom.findViewById(R.id.et_facturas_add_folio);
		add_factura_lugar = (EditText) view_custom.findViewById(R.id.et_facturas_add_lugar);
		add_factura_fecha = (TextView) view_custom.findViewById(R.id.TV_fecha);
		listDetalles = (ListView) view_custom.findViewById(R.id.lv_detalles);

		adapter_detalles = new ListAdapter_Detalles(this, detalles);
		listDetalles.setAdapter(adapter_detalles);

	}

	/***
	 * Se limpia el formulario
	 */

	private void clear_fields() {

		add_factura_folio.setText("");
		add_factura_lugar.setText("");
		add_factura_folio.setError(null);
		add_factura_lugar.setError(null);
		detalles.clear();

		cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));
		add_factura_fecha.setText("Fecha: " + cal.get(Calendar.DAY_OF_MONTH)
				+ " " + meses[cal.get(Calendar.MONTH)].substring(0, 3) + " "
				+ cal.get(Calendar.YEAR));

		spinner_clientes.setSelection(0);
	}

	/***
	 * Mostramos el diálogo para agregar el nuevo elemento
	 * 
	 * @param view
	 */

	public void btn_agregar(View view) {
		if (DataClass.getInstance().getClientes().size() > 0) {

			get_fields();
			clear_fields();
			registerForContextMenu(listDetalles);
			listDetalles.setOnCreateContextMenuListener(this);
			alertdialog_custom.show();

		} else {
			Toast.makeText(this,getResources().getText(	R.string.facturas_add_cliente_constraint),	Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * Aceptamos el diálogo
	 * 
	 * @param view
	 */

	public void btn_add_factura_aceptar(View view) {
		long new_id = -1;
		get_fields();

		if (Validaciones.EditText_NotNull(this, add_factura_folio) && Validaciones.EditText_NotNull(this, add_factura_lugar)) {

			
			if (facturas_edit == false) {
			
				new_id = DataClass.getInstance().insertFactura(
						String.valueOf(cal.getTimeInMillis()),
						add_factura_lugar.getText().toString(),
						add_factura_folio.getText().toString(),
						spinner_clientes.getSelectedItemId(),
						DataClass.getInstance().getContribuyente().getIva());
			
				if (new_id > 0) {


					float monto_total = 0;
					for (int count = 0; count < detalles.size(); count++) {
						DataClass.getInstance().insertDetalles(new_id, detalles.get(count));
						monto_total += detalles.get(count).getCantidad() * detalles.get(count).getValor_unitario();
					}

					monto_total = ((DataClass.getInstance().getContribuyente().getIva() / 100) + 1) * monto_total;
					DecimalFormat newFormat = new DecimalFormat("#.##");
					monto_total = Float.valueOf(newFormat.format(monto_total));

					DataClass.getInstance().getFacturas()
							.add(new Factura(new_id, String.valueOf(cal
									.getTimeInMillis()), add_factura_lugar
									.getText().toString(), add_factura_folio
									.getText().toString(),
									((Cliente) spinner_clientes
											.getSelectedItem()).getNombre(),
									spinner_clientes.getSelectedItemId(),
									DataClass.getInstance().getContribuyente()
											.getIva(), 1, monto_total));

					IgnoreFacturaCaseComparator icc = new IgnoreFacturaCaseComparator();
					Collections.sort(DataClass.getInstance().getFacturas(), icc);
					adapter_facturas.notifyDataSetChanged();

					Toast.makeText(this, getResources().getText(R.string.registro_add), Toast.LENGTH_SHORT).show();
					alertdialog_custom.cancel();
				} else {
					Toast.makeText(this,getResources().getText(R.string.error_save),Toast.LENGTH_SHORT).show();
				}
			} else {

				if(	DataClass.getInstance().updateFactura(
						DataClass.getInstance().getFacturas().get(factura_select_edit).getId(),
						String.valueOf(cal.getTimeInMillis()),
						add_factura_lugar.getText().toString(),
						add_factura_folio.getText().toString(),						
						spinner_clientes.getSelectedItemId(),
						DataClass.getInstance().getContribuyente().getIva()) > 0){
					
						DataClass.getInstance().getFacturas().get(factura_select_edit).setFecha(String.valueOf(cal.getTimeInMillis()));						
						DataClass.getInstance().getFacturas().get(factura_select_edit).setLugar(add_factura_lugar.getText().toString());
						DataClass.getInstance().getFacturas().get(factura_select_edit).setFolio(add_factura_folio.getText().toString());
						DataClass.getInstance().getFacturas().get(factura_select_edit).setIva(DataClass.getInstance().getContribuyente().getIva());
						DataClass.getInstance().getFacturas().get(factura_select_edit).setCliente(spinner_clientes.getSelectedItemId());
						DataClass.getInstance().getFacturas().get(factura_select_edit).setNombre_cliente(((Cliente) spinner_clientes.getSelectedItem()).getNombre());
						
						DataClass.getInstance().deleteDetalles(DataClass.getInstance().getFacturas().get(factura_select_edit).getId());
						
						float monto_total = 0;
						for (int count = 0; count < detalles.size(); count++) {
							DataClass.getInstance().insertDetalles(DataClass.getInstance().getFacturas().get(factura_select_edit).getId(), detalles.get(count));
							monto_total += detalles.get(count).getCantidad() * detalles.get(count).getValor_unitario();
						}

						monto_total = ((DataClass.getInstance().getContribuyente().getIva() / 100) + 1) * monto_total;
						DecimalFormat newFormat = new DecimalFormat("#.##");
						monto_total = Float.valueOf(newFormat.format(monto_total));
						DataClass.getInstance().getFacturas().get(factura_select_edit).setMontoTotal(monto_total);
						
						IgnoreFacturaCaseComparator icc = new IgnoreFacturaCaseComparator();
						Collections.sort(DataClass.getInstance().getFacturas(), icc);
						adapter_facturas.notifyDataSetChanged();
					
						Toast.makeText(this, getResources().getText(R.string.registro_update), Toast.LENGTH_SHORT).show();
						alertdialog_custom.cancel();
					
				}
				else {
					Toast.makeText(this,getResources().getText(R.string.error_update),Toast.LENGTH_SHORT).show();
				}
				facturas_edit =  false;
			}
		}

	}

	/***
	 * Cancelamos el diálogo
	 * 
	 * @param view
	 */

	public void btn_add_factura_cancelar(View view) {
		alertdialog_custom.cancel();
		facturas_edit = false;
	}

	/***
	 * Mostramos el Alert para la Fecha
	 * 
	 * @param view
	 */

	public void btn_factura_add_fecha(View view) {
		alertdialog_fecha.show();
		DatePicker datePicker = ((DatePicker) alertdialog_fecha.findViewById(R.id.datePicker_fechafolios));
		cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));

		if (facturas_edit == true) {
			cal.setTimeInMillis(Long.parseLong(DataClass.getInstance().getFacturas().get(factura_select_edit).getFecha()));
		}

		datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH), null);
	}

	/***
	 * Se acepta en Alert del Folio
	 * 
	 * @param view
	 */

	public void btn_folio_aceptar(View view) {
		cal = createAsCalendar((DatePicker) alertdialog_fecha.findViewById(R.id.datePicker_fechafolios));
		add_factura_fecha.setText("Fecha: " + cal.get(Calendar.DAY_OF_MONTH)
				+ " " + meses[cal.get(Calendar.MONTH)].substring(0, 3) + " "
				+ cal.get(Calendar.YEAR));
		alertdialog_fecha.cancel();
	}

	public void btn_folio_cancelar(View view) {
		cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));
		alertdialog_fecha.cancel();
	}

	// Calendario para guardar fecha en base de datos

	private Calendar createAsCalendar(DatePicker dp) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));

		cal.set(Calendar.YEAR, dp.getYear());
		cal.set(Calendar.MONTH, dp.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
		return cal;
	}

	// ----------------------------------------------- DETALLES
	// --------------------------------------------------

	private void get_fields_detalles() {

		add_detalle_cantidad = (EditText) view_detalles
				.findViewById(R.id.et_detalles_cantidad);
		add_detalle_descripcion = (EditText) view_detalles
				.findViewById(R.id.et_detalles_descripcion);
		add_detalle_precio = (EditText) view_detalles
				.findViewById(R.id.et_detalles_precio);
	}

	/***
	 * Se limpia el formulario
	 */

	private void clear_fields_detalles() {
		add_detalle_cantidad.setText("");
		add_detalle_descripcion.setText("");
		add_detalle_precio.setText("");
		add_detalle_cantidad.setError(null);
		add_detalle_descripcion.setError(null);
		add_detalle_precio.setError(null);
	}

	public void btn_add_detalles(View view) {
		detalles_select_edit = 0;
		alertdialog_detalles.show();
		get_fields_detalles();
		clear_fields_detalles();
	}

	public void btn_detalles_aceptar(View view) {

		if (Validaciones.EditText_NotNull(this, add_detalle_cantidad)
				&& Validaciones.EditText_NotNull(this, add_detalle_descripcion)
				&& Validaciones.EditText_NotNull(this, add_detalle_precio)) {

			boolean validados = true;
			float precio = 0f;
			int cantidad = 0;

			try {
				precio = Float.valueOf(add_detalle_precio.getText().toString().trim()).floatValue();
			} catch (NumberFormatException nfe) {
				add_detalle_precio.setError(this.getResources().getText(R.string.formato_invalido));
				validados = false;
			}

			try {
				cantidad = Integer.valueOf(add_detalle_cantidad.getText().toString().trim()).intValue();
			} catch (NumberFormatException nfe) {
				add_detalle_cantidad.setError(this.getResources().getText(R.string.formato_invalido));
				validados = false;

			}

			if (validados) {

				if (detalles_edit == false)
					detalles.add(new Detalle(-1, cantidad,	add_detalle_descripcion.getText().toString(),precio));
				else {
					detalles.get(detalles_select_edit).setCantidad(cantidad);
					detalles.get(detalles_select_edit).setDescripcion(add_detalle_descripcion.getText().toString());
					detalles.get(detalles_select_edit).setValor_unitario(precio);
				}

				adapter_detalles.notifyDataSetChanged();
				alertdialog_detalles.cancel();

			}

		}

	}

	public void btn_detalles_cancelar(View view) {
		alertdialog_detalles.cancel();
		detalles_edit = false;
	}
	
	
	
	
	protected class InitTask extends AsyncTask<Context, Integer, String> {
		String path;
		
		@Override
		protected String doInBackground(Context... params) {
			try {
				CreatePDF pruebaPDF =  new CreatePDF();
				path = pruebaPDF.PruebaPDFDescargar(factura_select_edit);
				
			} catch (Exception e) {
			
			}

			return "COMPLETO";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			
			
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pbarDialog.dismiss();
						
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("application/pdf");
			share.putExtra(Intent.EXTRA_SUBJECT, DataClass.getInstance().getContribuyente().getNombre() + " " + DataClass.getInstance().getFacturas().get(factura_select_edit).getFolio()) ;
			share.putExtra(Intent.EXTRA_STREAM,  Uri.parse("file://"+ path));
			share.putExtra(Intent.EXTRA_TEXT,  DataClass.getInstance().getFacturas().get(factura_select_edit).getFolio());
		
			startActivity(Intent.createChooser(share, "Compartir Factura"));
			 	
		}
	}
	
	
	

}
