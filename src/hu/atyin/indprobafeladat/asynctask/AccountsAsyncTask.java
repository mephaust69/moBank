package hu.atyin.indprobafeladat.asynctask;

import java.util.ArrayList;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.network.NetworkManager;
import hu.atyin.indprobafeladat.pojos.Account;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

/**
 * 
 * AsyncTask a számlák lekéréséhez
 *
 */
public class AccountsAsyncTask extends AsyncTask<Void, Void, ArrayList<Account>> {

	private volatile boolean running = true;
	private Context context = null;
	private ProgressDialog pd = null;
	private JSONObject resJsonObj = null;
	private ArrayList<Account>resultList = null;
	String resultString = null;
	
	public AccountsAsyncTask(Context aContext) {
		context = aContext;
	}
	
	@Override
	protected void onPreExecute() {
		// Progress Dialog beállítása
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.pd_accounts_message));
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
	protected ArrayList<Account> doInBackground(Void... params) {
		while(running) {
			resultString = null;
			
			// Csatlakozás a szerverhez
			NetworkManager nm = new NetworkManager();
			resultString = nm.getJSONAccounts(context);
			
			// Ha hiba van
			if(resultString.startsWith("Error") || resultString.equals(null)) {
				resultList = null;
			}
			else {
				try {
					// JSON objektum
					resJsonObj = new JSONObject(resultString);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				// Adatok kinyerése JSON-bõl
				if(resJsonObj != null) {
					resultList = new ArrayList<Account>();
					int jsonArrayLength = 0;
					
					String curr = null;
					int id = -1;
					long amount = 0;
					String accNr = null;
					
					try {
						jsonArrayLength = resJsonObj.getJSONArray("body").length();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					for (int i = 0; i < jsonArrayLength; i++) {
						try {
							id = resJsonObj.getJSONArray("body").getJSONObject(i).getInt("userId");
							curr = resJsonObj.getJSONArray("body").getJSONObject(i).getString("currency");
							amount = resJsonObj.getJSONArray("body").getJSONObject(i).getInt("amount");
							accNr = resJsonObj.getJSONArray("body").getJSONObject(i).getString("accountNr");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						resultList.add(new Account(id, accNr, amount, curr));
					}
				}
			}
			
			return resultList;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Account> result) {
		pd.dismiss();
		
		// Ha hiba volt
		if(result == null) {
			NetworkManager.showNetworkErrorAlert(context, resultString);
		}
	}

}
