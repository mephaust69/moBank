package hu.atyin.indprobafeladat.asynctask;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.network.NetworkManager;
import hu.atyin.indprobafeladat.pojos.GetTransactionDatas;
import hu.atyin.indprobafeladat.pojos.TransactionListDatas;

import java.util.ArrayList;

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
 * AsyncTask a számlatörténet lekéréséhez
 *
 */
public class GetTransactionsAsyncTask extends AsyncTask<GetTransactionDatas, Void, ArrayList<TransactionListDatas>> {
	
	// Konstansok
	private static final String TRANSACTIONS_LIST = "TransList";
	private static final String GET_TRANSACTIONS_DATAS = "GetTransDatas";
	
	private volatile boolean running = true;
	private Context context = null;
	private ProgressDialog pd = null;
	private JSONObject resJsonObj = null;
	private GetTransactionDatas transGetDatas;
	private ArrayList<TransactionListDatas> resultList;

	String resultString = null;
	int errorCode;
	
	public GetTransactionsAsyncTask(Context aContext) {
		context = aContext;
	}
	
	@Override
	protected void onPreExecute() {
		// Progress Dialog beállítása
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.pd_get_transactions_message));
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
	protected ArrayList<TransactionListDatas> doInBackground(GetTransactionDatas... params) {
		while(running) {
			// Csatlakozás a szerverhez
			NetworkManager nm = new NetworkManager();
			
			transGetDatas = params[0];
			resultString = nm.getTransactions(context, params[0].getDateFrom(), params[0].getDateTo(), params[0].getAccount());
			
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
					resultList = new ArrayList<TransactionListDatas>();
					int jsonArrayLength = 0;
					
					String sourceAcc = null;
					String targetAcc = null;
					int amount = 0;
					String date = null;
					String desc = null;
					
					try {
						jsonArrayLength = resJsonObj.getJSONArray("body").length();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					for (int i = 0; i < jsonArrayLength; i++) {
						try {
							sourceAcc = resJsonObj.getJSONArray("body").getJSONObject(i).getString("sourceAccount");
							targetAcc = resJsonObj.getJSONArray("body").getJSONObject(i).getString("targetAccount");
							amount = resJsonObj.getJSONArray("body").getJSONObject(i).getInt("amount");
							date = resJsonObj.getJSONArray("body").getJSONObject(i).getString("date");
							desc = resJsonObj.getJSONArray("body").getJSONObject(i).getString("description");
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
						
						resultList.add(new TransactionListDatas(sourceAcc, targetAcc, amount, date, desc));
					}
				}
			}
			
			return resultList;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(ArrayList<TransactionListDatas> result) {
		pd.dismiss();
		
		// Ha hiba volt
		if(result == null) {
			NetworkManager.showNetworkErrorAlert(context, resultString);
		}
		else {
			// Hibakód lekérése
			errorCode = NetworkManager.getErrorCode(resJsonObj);
			// Ha minden rendben volt
			if(errorCode == 0) {
				// Ha van eredményrekord
				if(result.size() != 0) {
					// LocalBroadcast küldése a MainActivity-nek
					Intent in = new Intent("Success-Get-Transactions");
					in.putParcelableArrayListExtra(TRANSACTIONS_LIST, result);
					in.putExtra(GET_TRANSACTIONS_DATAS, transGetDatas);
					LocalBroadcastManager.getInstance(context).sendBroadcast(in);
				}
				// Ha nincs eredményrekord
				else {
					showGetTransactionsAlertMessage(context.getString(R.string.transactions_no_result_message), context.getString(R.string.transactions_no_result_title));
				}
			}
			// Hibakód alapján hibaüzenet megjelenítése
			else {
				showGetTransactionsAlertMessage(NetworkManager.getErrorMessage(resJsonObj, context), context.getString(R.string.transactions_error_title));
			}
		}
	}
	
	/**
	 * Számlatörténet lekéréséhez dialógus ablak
	 * 
	 * @param message
	 * @param title
	 */
	public void showGetTransactionsAlertMessage(String message, String title) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}
}
