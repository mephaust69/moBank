package hu.atyin.indprobafeladat;

import hu.atyin.indprobafeladat.asynctask.LoginAsyncTask;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * Login felület
 * 
 * Felhasználónév és jelszó megadása
 *
 */
public class LoginActivity extends Activity {
	
	private Button btnLogin = null;
	private EditText edLoginUsername = null;
	private EditText edLoginPassword = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		// Referenciák lekérése
		btnLogin = (Button) findViewById(R.id.btnLogin);
		edLoginUsername = (EditText) findViewById(R.id.edLoginUsername);
		edLoginPassword = (EditText) findViewById(R.id.edLoginPassword);
		
		// Login gomb eseménykezelõ
		btnLogin.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				
				final String username = edLoginUsername.getText().toString();
				final String password = edLoginPassword.getText().toString();
				Log.d("String", username);
				Log.d("String", password);
				ArrayList<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("username", username));
				list.add(new BasicNameValuePair("password", password));
				
				// Csatlakozás a szerverhez AsyncTask-al
				new LoginAsyncTask(LoginActivity.this).execute(list);				
			}
		});
	}
	
	
}
