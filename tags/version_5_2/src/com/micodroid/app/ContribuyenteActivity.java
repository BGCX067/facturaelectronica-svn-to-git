package com.micodroid.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import com.micodroid.database.DataClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ContribuyenteActivity extends Activity {

	private static final int SELECT_PICTURE = 2;
	private Context context;
	private EditText nombre_contribuyente;
	private EditText rfc_contribuyente;
	private EditText curp_contribuyente;
	private EditText direccion_contribuyente;
	private EditText iva_contribuyente;
	private EditText sicofi_contribuyente;

	private String selectedImagePath;
	private Bitmap bitmap;
	private ImageView imageview;
	private Uri selectedImageUri;
	private String filemanagerstring;
	private InputStream inputstream;
	private BufferedInputStream bufferedinputstream;

	private AlertDialog alertdialog_codigoqr;
	private AlertDialog alertdialog_folio;

	private View view_codigoqr;
	private View view_folio;


	private Toast toast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contribuyente);
		DataClass.initInstance(context);  
		
		context = this;
		
		nombre_contribuyente = (EditText) findViewById(R.id.et_nombre);
		rfc_contribuyente = (EditText) findViewById(R.id.et_rfc);
		curp_contribuyente = (EditText) findViewById(R.id.et_curp);
		direccion_contribuyente = (EditText) findViewById(R.id.et_direccion);
		iva_contribuyente = (EditText) findViewById(R.id.et_iva);
		sicofi_contribuyente = (EditText) findViewById(R.id.et_sicofi);

		
		
		LayoutInflater inflater = LayoutInflater.from(this);

		
		// Creamos un AlertDialog para  la fecha del FOLIO
		view_folio = inflater.inflate(R.layout.fechas_datos, null);
		AlertDialog.Builder alt_folio = new AlertDialog.Builder(this);
		alt_folio.setView(view_folio);
		alt_folio.setCancelable(true);
		alt_folio.setTitle(R.string.sutitulo_seccion_folios);
		alt_folio.setIcon(R.drawable.ic_light_informacion);		
		alertdialog_folio = alt_folio.create();
		

		
		// Creamos el diálogo custom para la imagen QR

		view_codigoqr = inflater.inflate(R.layout.codigoqr, null);
		AlertDialog.Builder alt_codigoqr = new AlertDialog.Builder(this);

		alt_codigoqr
				.setView(view_codigoqr)
				.setCancelable(true)
				.setTitle(R.string.titulo_seccion_misDatos)
				.setPositiveButton(
						getResources().getText(R.string.btn_guardar),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								int size = bitmap.getWidth()* bitmap.getHeight();
								ByteArrayOutputStream out = new ByteArrayOutputStream(size);
								bitmap.compress(Bitmap.CompressFormat.PNG, 100,	out);
								try {
									out.flush();
									out.close();
									byte[] imagen = out.toByteArray();

									if (DataClass.getInstance().updateImagenQR(imagen) >= 1) {
										imageview = (ImageView) findViewById(R.id.iv_codigoQR);
										imageview.setImageBitmap(bitmap);
										DataClass.getInstance().getContribuyente().setQr(imagen);
										toast = Toast.makeText(context,getResources().getText(R.string.ImagenGuardada),Toast.LENGTH_LONG);
										toast.show();
									} else {
										toast = Toast.makeText(context,getResources().getText(R.string.FileNotFoundException),Toast.LENGTH_LONG);
										toast.show();
									}

								} catch (IOException e) {
									toast = Toast.makeText(context,getResources().getText(R.string.FileNotFoundException),
													Toast.LENGTH_LONG);
									toast.show();
								}

							}
						})
				.setNegativeButton(
						getResources().getText(R.string.btn_cancelar),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		alertdialog_codigoqr = alt_codigoqr.create();
		alertdialog_codigoqr.setTitle(getResources().getText(R.string.titulo_seccion_misDatos));
		alertdialog_codigoqr.setIcon(R.drawable.ic_light_informacion);

		fillData();

	}

	public void btn_guardar(View view) {
		int rows = DataClass.getInstance().updateContribuyente(nombre_contribuyente.getText().toString(), rfc_contribuyente.getText().toString(),curp_contribuyente.getText().toString(),direccion_contribuyente.getText().toString(), iva_contribuyente.getText().toString(), sicofi_contribuyente.getText().toString());
		if (rows >= 1) {
			
			DataClass.getInstance().getContribuyente().setNombre(nombre_contribuyente.getText().toString());
			DataClass.getInstance().getContribuyente().setRfc(rfc_contribuyente.getText().toString());
			DataClass.getInstance().getContribuyente().setCurp(curp_contribuyente.getText().toString());
			DataClass.getInstance().getContribuyente().setDireccion(direccion_contribuyente.getText().toString());
			float _iva = 0;
			try { _iva =  Float.parseFloat(iva_contribuyente.getText().toString()); }
			catch (Exception exc) { }			
			DataClass.getInstance().getContribuyente().setIva(_iva);
			DataClass.getInstance().getContribuyente().setSicofi(sicofi_contribuyente.getText().toString());
						
			toast = Toast.makeText(context,getResources().getText(R.string.ContribuyenteGuardado),Toast.LENGTH_LONG);
			toast.show();
		}
		else {
			Toast.makeText(context,getResources().getText(R.string.error_save),Toast.LENGTH_SHORT);

		}
	}

	public void btn_qr(View view) {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent,	getResources().getText(R.string.titulo_seccion_misDatos)),SELECT_PICTURE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {

				selectedImageUri = data.getData();
				filemanagerstring = selectedImageUri.getPath();
				selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null || filemanagerstring != null) {

					try {
						inputstream = getContentResolver().openInputStream(selectedImageUri);
						bufferedinputstream = new BufferedInputStream(inputstream);
						bitmap = BitmapFactory.decodeStream(bufferedinputstream);

						alertdialog_codigoqr.show();

						imageview = (ImageView) view_codigoqr
								.findViewById(R.id.imageView_qr);
						imageview.setImageBitmap(bitmap);

					} catch (FileNotFoundException e) {
						toast = Toast.makeText(	this,getResources().getText(R.string.FileNotFoundException),Toast.LENGTH_LONG);
						toast.show();
					}
				}
			}
		}

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void fillData() {

		nombre_contribuyente.setText(DataClass.getInstance().getContribuyente().getNombre());
		rfc_contribuyente.setText(DataClass.getInstance().getContribuyente().getRfc());
		curp_contribuyente.setText(DataClass.getInstance().getContribuyente().getCurp());
		direccion_contribuyente.setText(DataClass.getInstance().getContribuyente().getDireccion());
		iva_contribuyente.setText(String.valueOf(DataClass.getInstance().getContribuyente().getIva()));
		sicofi_contribuyente.setText(DataClass.getInstance().getContribuyente().getSicofi());
		if (DataClass.getInstance().getContribuyente().getBitmap() != null) {

			imageview = (ImageView) findViewById(R.id.iv_codigoQR);
			imageview.setImageBitmap(DataClass.getInstance().getContribuyente().getBitmap());
		}
	}

	
	
	
	/***
	 * Mostramos el Alert para los Folios
	 * @param view
	 */
	
	public void btn_folio(View view) {
		alertdialog_folio.show();
		Calendar calendar;
		DatePicker datePicker = ((DatePicker) alertdialog_folio.findViewById(R.id.datePicker_fechafolios));
		calendar = createAsCalendar(datePicker);
		calendar.setTimeInMillis(Long.parseLong(DataClass.getInstance().getContribuyente().getFecha_folios()));
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
	}
	
	
	/***
	 * Se cancela en Alert del Folio
	 * @param view
	 */
	
	public void btn_folio_cancelar(View view) {
		alertdialog_folio.cancel();
	}
	
	
	/***
	 * Se guarda o actualiza el Folio
	 * @param view
	 */
	
	public void btn_folio_aceptar(View view) {
		Calendar calendar;
		calendar = createAsCalendar((DatePicker) alertdialog_folio.findViewById(R.id.datePicker_fechafolios));
						
		int rows = DataClass.getInstance().updateFechaFolios(String.valueOf(calendar.getTimeInMillis()));
		if (rows >= 1) {
			DataClass.getInstance().getContribuyente().setFecha_folios(String.valueOf(calendar.getTimeInMillis()));
			Toast.makeText(context,getResources().getText(R.string.folio_save),Toast.LENGTH_LONG).show();
			alertdialog_folio.cancel();
		}
		else {
			Toast.makeText(context,getResources().getText(R.string.error_save),Toast.LENGTH_SHORT);
			alertdialog_folio.cancel();
		}
		
		
	}

	// Calendario para guardar fecha en base de datos
	
	private Calendar createAsCalendar(DatePicker dp)
	{
	    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-6"));
	 
	    cal.set(Calendar.YEAR, dp.getYear());
	    cal.set(Calendar.MONTH, dp.getMonth());
	    cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
	    return cal;
	}
	
	/***
	 * Volvemos a cargar la instancia en caso de que sea necesario
	 */
    
    @Override
    protected void onResume() {
    	super.onResume();   
    	DataClass.initInstance(context);
    }
	

}