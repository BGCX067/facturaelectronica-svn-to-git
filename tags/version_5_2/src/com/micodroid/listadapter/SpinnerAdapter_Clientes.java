package com.micodroid.listadapter;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.micodroid.baseclasses.Cliente;

public class SpinnerAdapter_Clientes extends ArrayAdapter<Cliente> {

    private Context context;
    private List<Cliente> clientes;

    public SpinnerAdapter_Clientes(Context context, int textViewResourceId,   List<Cliente> clientes) {
        super(context, textViewResourceId, clientes);
        this.context = context;
        this.clientes = clientes;
    }

    @Override
	public int getCount(){
       return clientes.size();
    }

    @Override
	public Cliente getItem(int position){
       return clientes.get(position);
    }

    @Override
	public long getItemId(int position){
       return clientes.get(position).getId();
    }
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(clientes.get(position).getNombre());
        return label;
    }
 
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(18f);
        label.setPadding(20, 20, 20, 20);
        label.setText(clientes.get(position).getNombre());

        return label;
    }

}
