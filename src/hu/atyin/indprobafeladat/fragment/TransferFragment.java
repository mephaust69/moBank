package hu.atyin.indprobafeladat.fragment;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.asynctask.TransferAsyncTask;
import hu.atyin.indprobafeladat.pojos.Account;
import hu.atyin.indprobafeladat.pojos.TransferDatas;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * 
 * Átutalás Dialógus Fragment
 *
 */
public class TransferFragment extends SherlockDialogFragment {
	
	public static final String TAG = "TransferFragment";

	private static final String ACCOUNTS_LIST = "AccList";
	
	private ArrayList<Account> accList;
	
	private Spinner spinnerSourceAcc;
	private TextView tvSourceAccCurrency;
	private EditText edTargetAccPartOne;
	private EditText edTargetAccPartTwo;
	private EditText edTargetAccPartThree;
	private EditText edValue;
	private EditText edComment;
	private Button btnTransfer;
	private Button btnCancel;
	
	/**
	 * Példányosítás
	 * 
	 * @param list
	 * @return
	 */
	public static TransferFragment newInstance(ArrayList<Account> list) {
		TransferFragment result = new TransferFragment();
		
		Bundle b = new Bundle();
		// Számla lista beállítása
		b.putParcelableArrayList(ACCOUNTS_LIST, list);
		result.setArguments(b);
		
		return result;
	}
	
	// Ha sikeres az átutalás, eltüntetjük a dialógus ablakot
	private BroadcastReceiver mSuccessTransfer = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			dismiss();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.transfer, container, false);
		
		getDialog().setTitle(R.string.titleTransfer);
		
		if(getArguments() != null) {
			// Számla lista lekérése
			accList = getArguments().getParcelableArrayList(ACCOUNTS_LIST);
		}
		
		// Referenciák lekérése
		spinnerSourceAcc = (Spinner) root.findViewById(R.id.SpinnerSourceAcc);
		tvSourceAccCurrency = (TextView) root.findViewById(R.id.tvSourceAccCurrency);
		edTargetAccPartOne = (EditText) root.findViewById(R.id.edTargetAccPartOne);
		edTargetAccPartTwo = (EditText) root.findViewById(R.id.edTargetAccPartTwo);
		edTargetAccPartThree = (EditText) root.findViewById(R.id.edTargetAccPartThree);
		edValue = (EditText) root.findViewById(R.id.edValue);
		edComment = (EditText) root.findViewById(R.id.edComment);
		btnTransfer = (Button) root.findViewById(R.id.btnTransfer);
		btnCancel = (Button) root.findViewById(R.id.btnCancel);
		
		// Spinner inicializálása
		String [] spinnerValues = new String[accList.size()];
		
		for (int i = 0; i < accList.size(); i++) {
			String accNum = accList.get(i).getAccountNr();
			String curr = accList.get(i).getCurrency();
			spinnerValues[i] = accNum + " (" + curr + ")";
		}
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, spinnerValues);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSourceAcc.setAdapter(spinnerAdapter);
		spinnerSourceAcc.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				tvSourceAccCurrency.setText(accList.get(position).getCurrency().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		// Cél számla EditText-ek beállítása, hogy automatikusan a következõre ugorjon
		edTargetAccPartOne.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(edTargetAccPartOne.getText().length() == 3) {
					edTargetAccPartTwo.requestFocus();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
		});
		
		// Cél számla EditText-ek beállítása, hogy automatikusan a következõre ugorjon
		edTargetAccPartTwo.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(edTargetAccPartTwo.getText().length() == 6) {
					edTargetAccPartThree.requestFocus();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		// Cél számla EditText-ek beállítása, hogy automatikusan a következõre ugorjon
		edTargetAccPartThree.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(edTargetAccPartThree.getText().length() == 3) {
					edValue.requestFocus();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		// Utal gomb listener
		btnTransfer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Beállítások lekérése
				String sourceAcc = accList.get(spinnerSourceAcc.getSelectedItemPosition()).getAccountNr();
				Log.d("sourceAccNr", sourceAcc);
				String targetAcc = edTargetAccPartOne.getText().toString() + "-" + edTargetAccPartTwo.getText().toString() + "-" + edTargetAccPartThree.getText().toString();
				Log.d("targetAccNr", targetAcc);
				int amount;
				
				try {
					amount = Integer.valueOf(edValue.getText().toString());
				}
				catch (NumberFormatException e) {
					amount = -1;
				}
				
				Log.d("Amount", String.valueOf(amount));
				String description = edComment.getText().toString();
				Log.d("Desc.", description);
				
				TransferDatas tf = new TransferDatas(sourceAcc, targetAcc, amount, description);
				
				new TransferAsyncTask(getSherlockActivity()).execute(tf);
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		LocalBroadcastManager.getInstance(getSherlockActivity()).registerReceiver(mSuccessTransfer, new IntentFilter("Success-Transfer"));
		
		return root;
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(getSherlockActivity()).unregisterReceiver(mSuccessTransfer);
		super.onDestroy();
	}
	
}
