package com.micodroid.app;

import com.micodroid.database.DataClass;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class SplashLoader extends Activity {

	private Context context;
	protected TextView _percentField;
	protected InitTask _initTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashloader);

		context = this;
		_percentField = (TextView) findViewById(R.id.tv_loader);
		_initTask = new InitTask();
		_initTask.execute(this);
	}

	/**
	 * Se precargan los datos a la clase
	 */

	private void LoaderData() {

		Intent lIntent = new Intent();
		lIntent.setClass(context, FacturaMovilActivity.class);
		startActivity(lIntent);
		finish();
	}

	/***
	 * Volvemos a cargar la instancia en caso de que sea necesario
	 */

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	protected class InitTask extends AsyncTask<Context, Integer, String> {
		@Override
		protected String doInBackground(Context... params) {
			DataClass.initInstance(context);
			try {
				Thread.sleep(1000);
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
			_percentField.setText(result);
			LoaderData();
		}
	}

}
