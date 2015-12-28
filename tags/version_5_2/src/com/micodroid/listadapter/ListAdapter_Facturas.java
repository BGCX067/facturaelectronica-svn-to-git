package com.micodroid.listadapter;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micodroid.app.R;
import com.micodroid.baseclasses.Factura;

public class ListAdapter_Facturas extends BaseAdapter {

	
	private List<Factura> facturas;
	private static LayoutInflater inflater = null;

	
	public ListAdapter_Facturas(Context context, List<Factura> facturas) {
		this.facturas = facturas;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return facturas.size();
	}

	public Object getItem(int position) {
		 return facturas.get(position);
	}

	public long getItemId(int position) {
        return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
	    DateFormatSymbols dfs = new DateFormatSymbols();

		String[] meses = dfs.getMonths();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));
		
		View vi = convertView;
		if (convertView == null) {

			
			
			vi = inflater.inflate(R.layout.factura_row, null);
			ViewHolder holder = new ViewHolder();
			holder.txtFolio = (TextView) vi.findViewById(R.id.tv_factura_folio);
			holder.txtNombreCliente = (TextView) vi.findViewById(R.id.tv_nombre_cliente);
			
			
			
			
			holder.txtFecha =  (TextView) vi.findViewById(R.id.tv_factura_fecha);
			
			
			
			
			holder.txtMonto =  (TextView) vi.findViewById(R.id.tv_factura_monto);
			holder.icon = (ImageView) vi.findViewById(R.id.image_icon);
			
			vi.setTag(holder);

		}
		
		Factura factura = facturas.get(position);
	    if(factura != null) {
	    	ViewHolder holder = (ViewHolder)vi.getTag();
			// set data to display
			holder.txtFolio.setText(factura.getFolio());
			
			
			cal.setTimeInMillis(Long.parseLong(factura.getFecha()));
			
			
			holder.txtFecha.setText(cal.get(Calendar.DAY_OF_MONTH) + " " + meses[cal.get(Calendar.MONTH)].substring(0, 3) +  " " + cal.get(Calendar.YEAR), null);
			
			
			
			holder.txtNombreCliente.setText(factura.getNombreCliente());
			holder.txtMonto.setText("$ " + factura.getMontoTotal());
			
			if(factura.getEstado() == 0) {
				vi.setBackgroundResource(R.drawable.list_selector_cancelado);
				holder.icon.setImageResource(R.drawable.ic_light_cancelar);
			}
			else {
				vi.setBackgroundResource(R.drawable.list_selector);
				holder.icon.setImageResource(R.drawable.ic_light_facturas);
			}
			
		
        }
		
		return vi;
	}
	
	static class ViewHolder {
		private TextView txtFolio;
		private TextView txtNombreCliente;
		private TextView txtFecha;
		private TextView txtMonto;
		private ImageView icon;
		
	}


}
