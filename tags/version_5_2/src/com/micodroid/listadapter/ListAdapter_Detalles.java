package com.micodroid.listadapter;

import java.util.List;
import com.micodroid.app.R;
import com.micodroid.baseclasses.Detalle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter_Detalles extends BaseAdapter {

	
	private List<Detalle> detalles;
	private static LayoutInflater inflater = null;

	
	public ListAdapter_Detalles(Context context, List<Detalle> detalles) {
		this.detalles = detalles;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return detalles.size();
	}

	public Object getItem(int position) {
		 return detalles.get(position);
	}

	public long getItemId(int position) {
        return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
	   		
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.detalles_row, null);
			ViewHolder holder = new ViewHolder();
			holder.txtDescripcion = (TextView) vi.findViewById(R.id.tv_detalles_descripcion);
			holder.txtMonto =  (TextView) vi.findViewById(R.id.tv_detalles_precio);
			vi.setTag(holder);

		}
		
		Detalle detalle = detalles.get(position);
	    if(detalle != null) {
	    	ViewHolder holder = (ViewHolder)vi.getTag();
			// set data to display
			holder.txtDescripcion.setText(detalle.getDescripcion());
			holder.txtMonto.setText("$ " + String.valueOf(detalle.getValor_unitario()) + " x " + String.valueOf(detalle.getCantidad()));
        }
		
		return vi;
	}
	
	static class ViewHolder {
		private TextView txtDescripcion;
		private TextView txtMonto;
	}


}
