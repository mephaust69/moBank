package hu.atyin.indprobafeladat.asynctask;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.network.NetworkManager;
import hu.atyin.indprobafeladat.pojos.TransferDatas;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 
 * AsyncTask az átutaláshoz
 *
 */
public class TransferAsyncTask extends AsyncTask<TransferDatas, Void, String>{

	private volatile boolean running = true;
	private Context context = null;
	private ProgressDialog pd = null;
	private JSONObject resJsonObj = null;
	String resultString = null;
	int errorCode;
	
	public TransferAsyncTask(Context aContext) {
		context = aContext;
	}
	
	@Override
	protected void onPreExecute() {
		// Progress Dialog beállítása
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.pd_transfer_message));
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
	protected String doInBackground(TransferDatas... params) {
		while(running) {
			// Csatlakozás a szerverhez
			NetworkManager nm = new NetworkManager();
			resultString = nm.doTransfer(context, params[0].getSourceAccount(), params[0].getTargetAccount(), params[0].getAmount(), params[0].getDescription());
			
			return resultString;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		pd.dismiss();
		
		// Ha hiba volt
		if(result.startsWith("Error") || result.equals(null)) {
			NetworkManager.showNetworkErrorAlert(context, result);
		}
		else {
			try {
				// JSON objektum
				resJsonObj = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			errorCode = NetworkManager.getErrorCode(resJsonObj);
			// Ha nem volt hiba
			if(errorCode == 0) {
				// Sikeres tranzakció dialógus
				showTransferAlertMessage(context.getString(R.string.transfer_alert_message), context.getString(R.string.transfer_alert_title));
				// LocalBroadcast küldése a MainActivity-nek
				Intent in = new Intent("Success-Transfer");
				LocalBroadcastManager.getInstance(context).sendBroadcast(in);
			}
			// Ha hiba volt
			else {
				showTransferAlertMessage(NetworkManager.getErrorMessage(resJsonObj, context), context.getString(R.string.transfer_alert_error_title));
			}
		}
	}
	
	/**
	 * Átutalás dialógus ablak
	 * 
	 * @param message
	 * @param title
	 */
	public void showTransferAlertMessage(String message, String title) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					if(errorCode == 0) {
						// Ha sikeres volt, akkor a számlák lista frissítése
						Intent in = new Intent("Success-Transfer-Refresh");
						LocalBroadcastManager.getInstance(context).sendBroadcast(in);
					}
			}
		});
		alert.show();
	}
}
