package hu.atyin.indprobafeladat.fragment;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.asynctask.GetTransactionsAsyncTask;
import hu.atyin.indprobafeladat.fragment.DatePickerDialogFragment.IDatePickerDialogFragment;
import hu.atyin.indprobafeladat.pojos.Account;
import hu.atyin.indprobafeladat.pojos.GetTransactionDatas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * 
 * Számlatörténet dialógus ablak
 *
 */
public class GetTransactionsFragment extends SherlockDialogFragment implements IDatePickerDialogFragment {

	public static final String TAG = "GetTransactionsFragment";
	
	// Konstansok
	private static final String ACCOUNTS_LIST = "AccList";
	private static final int DATE_FROM = 0;
	private static final int DATE_TO = 1;
	
	private ArrayList<Account> accList;
	private String today;
	
	// Dátumok
	private Calendar calInitDate = Calendar.getInstance();
	private Calendar dateFromCalSelected = Calendar.getInstance();
	private Calendar dateToCalSelected = Calendar.getInstance();
	
	private Spinner spinnerAccount;
	private TextView tvDateFrom;
	private TextView tvDateTo;
	private Button btnShow;
	private Button btnCancel;
	
	/**
	 * Példányosítás
	 * 
	 * @param list
	 * @return
	 */
	public static GetTransactionsFragment newInstance(ArrayList<Account> list) {
		GetTransactionsFragment result = new GetTransactionsFragment();
		
		Bundle b = new Bundle();
		// Számla lista átadása
		b.putParcelableArrayList(ACCOUNTS_LIST, list);
		result.setArguments(b);
		
		return result;
	}
	
	// Ha sikeres a tranzakció, bezárjuk a dialógus ablakot
	private BroadcastReceiver mSuccessGetTransactions = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			dismiss();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.gettransactions, container, false);
		
		getDialog().setTitle(R.string.dialog_transactions_title);
		
		// Számla lista lekérése
		if(getArguments() != null) {
			accList = getArguments().getParcelableArrayList(ACCOUNTS_LIST);
		}
		
		spinnerAccount = (Spinner) root.findViewById(R.id.SpinnerAcc);
		tvDateFrom = (TextView) root.findViewById(R.id.dateFrom);
		tvDateTo = (TextView) root.findViewById(R.id.dateTo);
		btnShow = (Button) root.findViewById(R.id.btnShowTransactions);
		btnCancel = (Button) root.findViewById(R.id.btnCancel);
		
		// Spinner inicializálása
		String [] spinnerValues = new String[accList.size()];
		
		for (int i = 0; i < spinnerValues.length; i++) {
			String accNum = accList.get(i).getAccountNr();
			String curr = accList.get(i).getCurrency();
			spinnerValues[i] = accNum + " (" + curr + ")";
		}
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, spinnerValues);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAccount.setAdapter(spinnerAdapter);
		
		// Dátumok inicializálása
		tvDateFrom.setText(getInitDate(DATE_FROM));
		tvDateTo.setText(getInitDate(DATE_TO));
		
		// Date from listener
		tvDateFrom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(DATE_FROM);
			}
		});
		
		// Date To listener
		tvDateTo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(DATE_TO);
			}
		});
		
		// Show Button listener
		btnShow.setOnClickListener(new OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				// Beállítások lekérése
				String acc = accList.get(spinnerAccount.getSelectedItemPosition()).getAccountNr();
				Log.d("Account Nr.", acc);
				
				String curr = accList.get(spinnerAccount.getSelectedItemPosition()).getCurrency();
				
				String df = tvDateFrom.getText().toString();
				Log.d("Date from", df);
				
				String dt = tvDateTo.getText().toString();
				Log.d("Date to", dt);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date dateDf = null;
				Date dateDt = null;
				Date dateToday = null;
				
				try {
					dateDf = sdf.parse(df);
					dateDt = sdf.parse(dt);
					dateToday = sdf.parse(today);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// Ha a kezdõdátum nagyobb mint a végdátum
				if(dateDf.compareTo(dateDt) > 0) {
					showDateErrorAlert(getSherlockActivity().getString(
							R.string.alert_date_error_one));
				}
				// Ha a kezdõ dátum nagyobb mint a mai nap
				else if(dateDf.compareTo(dateToday) > 0) {
					showDateErrorAlert(getSherlockActivity().getString(
							R.string.alert_date_error_two));
				}
				// Ha jó minden
				else {
					// Date to beállítása egy nappal késõbbre, mivel 0:00-ig veszi csak a napot
					dateDt.setTime(dateDt.getTime() + (86400*1000));
					dt = sdf.format(dateDt);
					Log.d("Date Dt", dt);
					
					GetTransactionDatas gtd = new GetTransactionDatas(acc, curr, df, dt);
					
					new GetTransactionsAsyncTask(getSherlockActivity()).execute(gtd);
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		LocalBroadcastManager.getInstance(getSherlockActivity()).registerReceiver(mSuccessGetTransactions, new IntentFilter("Success-Get-Transactions"));
		
		return root;
	}
	
	/**
	 * Dátum hiba dialog
	 * 
	 * @param message
	 */
	public void showDateErrorAlert(String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
		alert.setTitle(R.string.alert_date_error_title);
		alert.setMessage(message);
		alert.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}
	
	/**
	 * Dátumválasztó dialógus megnyitása
	 * 
	 * @param which
	 */
	public void showDatePickerDialog(int which) {
		FragmentManager fm = getFragmentManager();
		
		// Megnézzük melyiket választottuk ki
		Calendar selected = Calendar.getInstance();
		if(which == DATE_FROM)
			selected = dateFromCalSelected;
		else if(which == DATE_TO)
			selected = dateToCalSelected;
		
		DatePickerDialogFragment datePicker = DatePickerDialogFragment.newInstance(which, selected);
		datePicker.setTargetFragment(this, 0);
		datePicker.show(fm, DatePickerDialogFragment.TAG);
	}
	
	/**
	 * Belépéskor inicializálja a dátumokat
	 * 
	 * @param which
	 * @return
	 */
	public String getInitDate(int which) {
		if(which == DATE_FROM) {
			// Egy héttel korábbra állítás
			Date d = new Date(System.currentTimeMillis()-(86400*7*1000));
			calInitDate.setTime(d);
			dateFromCalSelected.setTime(d);
		}
		else if(which == DATE_TO) {
			// Mai napra állítás
			Date d = new Date(System.currentTimeMillis());
			calInitDate.setTime(d);
			dateToCalSelected.setTime(d);
		}
		
		StringBuilder initDate = new StringBuilder();
		
		initDate.append(calInitDate.get(Calendar.YEAR));
		initDate.append("-");
		
		if((calInitDate.get(Calendar.MONTH)+1) < 10)
			initDate.append("0" + (calInitDate.get(Calendar.MONTH)+1));
		else
			initDate.append(calInitDate.get(Calendar.MONTH)+1);
		
		initDate.append("-");
		
		if(calInitDate.get(Calendar.DAY_OF_MONTH) < 10)
			initDate.append("0" + calInitDate.get(Calendar.DAY_OF_MONTH));
		else
			initDate.append(calInitDate.get(Calendar.DAY_OF_MONTH));
		
		if(which == DATE_TO)
			today = initDate.toString();
		
		return initDate.toString();
	}

	/**
	 * Dátum kiválasztó listener
	 */
	@Override
	public void onDateSelected(Calendar selected, String date, int which) {
		if(which == DATE_FROM) {
			tvDateFrom.setText(date);
			dateFromCalSelected = selected;
		}		
		else if(which == DATE_TO) {
			tvDateTo.setText(date);
			dateToCalSelected = selected;
		}
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(getSherlockActivity()).unregisterReceiver(mSuccessGetTransactions);
		super.onDestroy();
	}
}
