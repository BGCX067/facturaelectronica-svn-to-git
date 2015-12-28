package com.micodroid.listadapter;


import java.util.List;

import com.micodroid.app.R;
import com.micodroid.baseclasses.Cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter_Clientes extends BaseAdapter {

	
	private List<Cliente> clientes;
	private static LayoutInflater inflater = null;

	
	public ListAdapter_Clientes(Context context, List<Cliente> clientes) {
		this.clientes = clientes;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return clientes.size();
	}

	public Object getItem(int position) {
		 return clientes.get(position);
	}

	public long getItemId(int position) {
        return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		if (convertView == null) {

			vi = inflater.inflate(R.layout.clientes_row, null);
			ViewHolder holder = new ViewHolder();
			holder.txtNombre = (TextView) vi.findViewById(R.id.tv_nombre_cliente);
			holder.txtRFC = (TextView) vi.findViewById(R.id.tv_rfc_cliente);
			vi.setTag(holder);

		}
		
		Cliente cliente = clientes.get(position);
	    if(cliente != null) {
	    	ViewHolder holder = (ViewHolder)vi.getTag();
			// set data to display
			holder.txtNombre.setText(cliente.getNombre());
			holder.txtRFC.setText(cliente.getRfc());
		
        }
		
		return vi;
	}
	
	static class ViewHolder {
		private TextView txtNombre;
		private TextView txtRFC;
	}


}
