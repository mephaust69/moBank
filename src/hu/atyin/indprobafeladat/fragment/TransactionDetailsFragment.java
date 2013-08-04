package hu.atyin.indprobafeladat.fragment;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.pojos.TransactionListDatas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * 
 * Tranzakció részletek Fragment
 *
 */
public class TransactionDetailsFragment extends SherlockFragment {
	
	public static final String TAG = "TransactionDetailsFragment";
	
	// Konstansok
	private static final String TRANSACTION_LIST_DATAS = "TransListDatas";
	private static final String TRANSACTION_CURRENCY = "Currency";
	
	private TransactionListDatas transListDatas;
	private String transCurrency;
	
	private TextView transSourceAcc;
	private TextView transTargetAcc;
	private TextView transAmount;
	private TextView transDate;
	private TextView transDescription;

	/**
	 * Példányosítás
	 * 
	 * @param tld
	 * @param curr
	 * @return
	 */
	public static TransactionDetailsFragment newInstance(TransactionListDatas tld, String curr) {
		TransactionDetailsFragment result = new TransactionDetailsFragment();
		
		Bundle b = new Bundle();
		
		// Átadjuk a listát és a pénznemet
		b.putParcelable(TRANSACTION_LIST_DATAS, tld);
		b.putString(TRANSACTION_CURRENCY, curr);
		result.setArguments(b);
		
		return result;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getArguments() != null) {
			// Lekérjük a listát és a pénznemet
			transListDatas = getArguments().getParcelable(TRANSACTION_LIST_DATAS);
			transCurrency = getArguments().getString(TRANSACTION_CURRENCY);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.transaction_detail, container, false);
		
		// Referenciák lekérése
		transSourceAcc = (TextView) root.findViewById(R.id.tvTransDetailSourceAcc);
		transTargetAcc = (TextView) root.findViewById(R.id.tvTransDetailTargetAcc);
		transAmount = (TextView) root.findViewById(R.id.tvTransDetailAmount);
		transDate = (TextView) root.findViewById(R.id.tvTransDetailDate);
		transDescription = (TextView) root.findViewById(R.id.tvTransDetailDescription);
		
		// Elemek beállítása
		transSourceAcc.setText(transListDatas.getSourceAccount().toString());
		transTargetAcc.setText(transListDatas.getTargetAccount().toString());
		transAmount.setText(transListDatas.getAmount() + " " + transCurrency);
		transDate.setText(transListDatas.getDate().toString());
		transDescription.setText(transListDatas.getDescription().toString());
		
		return root;		
	}
	
}
