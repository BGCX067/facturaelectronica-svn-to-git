package com.micodroid.tools;


import com.micodroid.app.R;

import android.content.Context;
import android.widget.EditText;

public class Validaciones {
	
	public static boolean EditText_NotNull(Context context, EditText editview) {
		if(editview.getText().length() == 0) {
			editview.setError(context.getResources().getText(R.string.campo_requerido));	
			return false;
		}
		else return true;
	}
	
}
