package com.micodroid.app;

import java.util.Collections;

import com.micodroid.baseclasses.Cliente;
import com.micodroid.listadapter.ListAdapter_Clientes;
import com.micodroid.tools.IgnoreClienteCaseComparator;
import com.micodroid.tools.Validaciones;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ClientesActivity extends Activity {

	private View view_custom;
	private AlertDialog alertdialog_custom;
	private ListView listView;
	private ListAdapter_Clientes adapter;

	private EditText cliente_nombre;
	private EditText cliente_rfc;
	private EditText cliente_direccion;	
	private Cliente cliente = null;
	private boolean modoEdicion = false;
	
	private int position_cliente;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.clientes);
		
		DataClass.initInstance(this);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		view_custom = inflater.inflate(R.layout.clientes_add, null);
		AlertDialog.Builder alt_dialog = new AlertDialog.Builder(this);
		alt_dialog.setView(view_custom).setCancelable(true).setTitle(R.string.titulo_seccion_clientes);
		alertdialog_custom = alt_dialog.create();
		alertdialog_custom.setTitle(getResources().getText(R.string.titulo_seccion_clientes));
		alertdialog_custom.setIcon(R.drawable.ic_light_add_cliente);
		listView = (ListView) findViewById(R.id.lv_clientes);

		fill_data();	
		registerForContextMenu(listView);
	}

	/**
	 * Se crea menú contextual
	 */
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.lv_clientes) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			position_cliente = info.position;
			cliente = DataClass.getInstance().getClientes().get(position_cliente);
			menu.setHeaderTitle(cliente.getNombre());
			menu.setHeaderIcon(R.drawable.ic_light_cliente);

			String[] menuItems = getResources().getStringArray(R.array.menu_contextual_clientes);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		cliente = DataClass.getInstance().getClientes().get(info.position);
		switch(item.getItemId()) {
		case 0:
			alertdialog_custom.show();
			get_fields();
			clear_fields();			
			cliente_nombre.setText(cliente.getNombre());
			cliente_rfc.setText(cliente.getRfc());
			cliente_direccion.setText(cliente.getDireccion());
			modoEdicion = true;
			break;
		case 1:
			switch(DataClass.getInstance().deleteCliente(cliente.getId())) {
			case -1:
				Toast.makeText(this,getResources().getText(R.string.registro_cliente_delete_constraint),Toast.LENGTH_LONG).show();
				break;
			default:
				DataClass.getInstance().getClientes().remove(info.position);
				adapter.notifyDataSetChanged();
				DataClass.getInstance().actualizar_facturas = true;
				Toast.makeText(this,getResources().getText(R.string.registro_cliente_delete),Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
		return true;
	}
	
	
	/***
	 * Se obtiene List<Cliente> de los clientes
	 */
	
	private void fill_data() {
		adapter = new ListAdapter_Clientes(this, DataClass.getInstance().getClientes());
		listView.setAdapter(adapter);
	}
	

	/***
	 * Se guarda o actualiza el registro
	 * @param view
	 */
	
	public void btn_aceptar(View view) {

		long new_id = -1;
		
		get_fields();
		
		if (Validaciones.EditText_NotNull(this, cliente_nombre)
				&& Validaciones.EditText_NotNull(this, cliente_rfc)
				&& Validaciones.EditText_NotNull(this, cliente_direccion)) {
			if(modoEdicion == true) {
				
				new_id = DataClass.getInstance().updateCliente(cliente.getId(), cliente_nombre.getText().toString(), cliente_rfc.getText().toString(), cliente_direccion.getText().toString());
				if(new_id > 0) {
					
					DataClass.getInstance().getClientes().get(position_cliente).setNombre(cliente_nombre.getText().toString());
					DataClass.getInstance().getClientes().get(position_cliente).setRfc(cliente_rfc.getText().toString());
					DataClass.getInstance().getClientes().get(position_cliente).setDireccion(cliente_direccion.getText().toString());
					DataClass.getInstance().actualizar_facturas = true;
					
					
					IgnoreClienteCaseComparator icc = new IgnoreClienteCaseComparator();
					Collections.sort(DataClass.getInstance().getClientes(),icc);
					adapter.notifyDataSetChanged();
					Toast.makeText(this,getResources().getText(R.string.registro_edit),Toast.LENGTH_SHORT).show();
					
					
					
					alertdialog_custom.cancel();
				}
				else {
					Toast.makeText(this,getResources().getText(R.string.error_save),Toast.LENGTH_SHORT).show();
				}
				
			}
			else {
				new_id = DataClass.getInstance().insertCliente(cliente_nombre.getText().toString(), cliente_rfc.getText().toString(), cliente_direccion.getText().toString());
				if (new_id != -1) {
					
					DataClass.getInstance().getClientes().add(new Cliente(new_id, cliente_nombre.getText().toString(), cliente_rfc.getText().toString(), cliente_direccion.getText().toString()));
					IgnoreClienteCaseComparator icc = new IgnoreClienteCaseComparator();
					Collections.sort(DataClass.getInstance().getClientes(),icc);
					adapter.notifyDataSetChanged();
					DataClass.getInstance().actualizar_facturas = true;
					Toast.makeText(this,getResources().getText(R.string.registro_add),Toast.LENGTH_SHORT).show();
					alertdialog_custom.cancel();
				} 
				else {
					Toast.makeText(this,getResources().getText(R.string.error_save),Toast.LENGTH_SHORT).show();
				}
			}			
		}
		
		modoEdicion = false;
	}

	/***
	 * Obtenemos los objetos del formulario 
	 */
	
	private void get_fields(){
		cliente_nombre = (EditText) view_custom.findViewById(R.id.et_cliente_nombre);
		cliente_rfc = (EditText) view_custom.findViewById(R.id.et_cliente_rfc);
		cliente_direccion = (EditText) view_custom.findViewById(R.id.et_cliente_direccion);
	}
	
	/***
	 * Se limpia el formulario
	 */
	
	private void clear_fields(){
		
		cliente_nombre.setText("");
		cliente_rfc.setText("");
		cliente_direccion.setText("");
		cliente_nombre.setError(null);	
		cliente_rfc.setError(null);	
		cliente_direccion.setError(null);	
		
	}
	
	/***
	 * Cancelamos el diálogo
	 * @param view
	 */
	
	public void btn_cancelar(View view) {
		modoEdicion = false;
		alertdialog_custom.cancel();
	}

	/***
	 * Mostramos el diálogo para agregar el nuevo elemento
	 * @param view
	 */
	
	
	public void btn_agregar(View view) {
		alertdialog_custom.show();
		get_fields();
		clear_fields();
	}
		
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if(cliente != null) {
			/*
			savedInstanceState.putLong("id",cliente.getId());
			savedInstanceState.putString("nombre", cliente.getNombre());
			savedInstanceState.putString("rfc", cliente.getRfc());
			savedInstanceState.putString("direccion", cliente.getDireccion());
			super.onSaveInstanceState(savedInstanceState);
			*/
		}
		
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  //cliente = new Cliente(savedInstanceState.getLong("id"), savedInstanceState.getString("nombre"), savedInstanceState.getString("rfc"),savedInstanceState.getString("direccion"));
	  
	}
	
	/***
	 * Volvemos a cargar la instancia en caso de que sea necesario
	 */
    
    @Override
    protected void onResume() {
    	super.onResume();   
    	DataClass.initInstance(this);
    }
	
	

}
