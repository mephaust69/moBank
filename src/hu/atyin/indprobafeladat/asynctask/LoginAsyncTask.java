package hu.atyin.indprobafeladat.asynctask;

import hu.atyin.indprobafeladat.MainActivity;
import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.network.NetworkManager;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 
 * AsyncTask a bejelentkezéshez
 *
 */
public class LoginAsyncTask extends AsyncTask<ArrayList<BasicNameValuePair>, Void, String> {

	private static final String USERNAME = "username";
	
	private volatile boolean running = true;
	private Context context = null;
	private ProgressDialog pd = null;
	private JSONObject resJsonObj = null;
	int errorCode;
	String retVal = null;
	String name = null;
	
	public LoginAsyncTask(Context aContext) {
		context = aContext;
	}
	
	@Override
	protected void onPreExecute() {
		// Progress Dialog beállítása
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.pd_login_message));
		pd.show();
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancel(true);
			}
		});
	}
	
	@Override
	protected void onCancelled() {
		running = false;
	}
	
	@Override
	protected String doInBackground(ArrayList<BasicNameValuePair>... params) {
		while(running) {
			retVal = null;
			
			String username = params[0].get(0).getValue();
			String password = params[0].get(1).getValue();
			
			Log.d("LoginAsyncTask", username + ", " + password);
			
			// Csatlakozás a szerverhez
			NetworkManager nm = new NetworkManager();
			retVal = nm.getJSONLoginDatas(context, username, password);
			
			return retVal;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		pd.dismiss();
		
		// Ha hiba van
		if(retVal.startsWith("Error") || retVal.equals(null)) {
			NetworkManager.showNetworkErrorAlert(context, retVal);
		}
		else {
			try {
				// JSON objektum
				resJsonObj = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Hibakód lekérése
			errorCode = NetworkManager.getErrorCode(resJsonObj);
			if(resJsonObj != null) {
				showLoginAlertMessage(resJsonObj);
			}
		}
	}
	
	/**
	 * Login dialógus ablak
	 * 
	 * @param json
	 */
	public void showLoginAlertMessage(JSONObject json) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.login_alert_title);
		// Ha nem volt hiba
		if(errorCode == 0) {
			try {
				// Username lekérése
				name = json.getJSONObject("body").getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Üdvözlõ üzenet beállítása
			alert.setMessage(context.getString(R.string.login_alert_message) + name + "!");
		}
		// Ha hiba volt
		else {
			alert.setMessage(NetworkManager.getErrorMessage(json, context));
		}
		
		alert.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// Ha nem volt hiba
				if(errorCode == 0) {
					// MainActivity indítása username átadásával
					Intent in = new Intent();
					in.setClass(context, MainActivity.class);
					in.putExtra(USERNAME, name);
					context.startActivity(in);
				}
			}
		});
		alert.show();
	}

}
