package com.micodroid.app;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class FacturaMovilActivity extends TabActivity implements OnTabChangeListener  {

	private TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.main);

	    tabHost = getTabHost();  
	    TabHost.TabSpec spec;  
	    Intent intent;  

	    intent = new Intent().setClass(this, FacturasActivity.class);
	    spec = tabHost.newTabSpec("facturas").setIndicator(getString(R.string.titulo_seccion_facturas),getResources().getDrawable(R.drawable.ic_light_facturas)).setContent(intent);
	    tabHost.addTab(spec);
	    	    	    
	    intent = new Intent().setClass(this, ClientesActivity.class);
	    spec = tabHost.newTabSpec("clientes").setIndicator(getString(R.string.titulo_seccion_clientes),getResources().getDrawable(R.drawable.ic_light_clientes)).setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, ContribuyenteActivity.class);
	    spec = tabHost.newTabSpec("mis datos").setIndicator(getString(R.string.titulo_seccion_misDatos),getResources().getDrawable(R.drawable.ic_light_informacion)).setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(this);
     
        for(int i=0;i < getTabWidget().getTabCount(); i++) tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(getString(R.color.black)));
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor(getString(R.color.selected)));
        
        
        
	}

	public void onTabChanged(String tabId) {
		for(int i=0;i < getTabWidget().getTabCount(); i++) 	tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(getString(R.color.black)));
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor(getString(R.color.selected)));
	}
}
