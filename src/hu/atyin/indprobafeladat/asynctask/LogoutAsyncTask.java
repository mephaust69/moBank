package hu.atyin.indprobafeladat.asynctask;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.network.NetworkManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

/**
 * 
 * AsyncTask a kijelentkezéshez
 *
 */
public class LogoutAsyncTask extends AsyncTask<Void, Void, String> {

	private volatile boolean running = true;
	private Context context = null;
	private ProgressDialog pd = null;
	private String resultString = null;
	
	public LogoutAsyncTask(Context aContext) {
		context = aContext;
	}
	
	@Override
	protected void onPreExecute() {
		// Progress Dialog beállítása
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.pd_logout_message));
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
	protected String doInBackground(Void... params) {
		while(running) {
			// Csatlakozás a szerverhez
			NetworkManager nm = new NetworkManager();
			resultString = nm.logout(context);
			return resultString;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		pd.dismiss();
	}
}
