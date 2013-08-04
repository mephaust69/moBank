package hu.atyin.indprobafeladat.fragment;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.adapter.TransactionAdapter;
import hu.atyin.indprobafeladat.pojos.GetTransactionDatas;
import hu.atyin.indprobafeladat.pojos.TransactionListDatas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * 
 * Számlatörténet ListFragment
 *
 */
public class TransactionsListFragment extends SherlockListFragment {
	public static final String TAG = "TransactionsListFragment";
	
	// Konstansok
	private static final String TRANSACTIONS_LIST = "TransList";
	private static final String GET_TRANSACTIONS_DATAS = "GetTransDatas";
	
	private ArrayList<TransactionListDatas> transList;
	private GetTransactionDatas getDatas;
	
	private TransactionAdapter adapter;
	private PopupWindow popupWindow;
	
	// listener
	private ITransactionsListFragment listener;
	
	/**
	 * Comparator rendezéshez (növekvõ) dátum szerint
	 */
	public class TransactionSortAscComparator implements Comparator<TransactionListDatas> {
		@Override
		public int compare(TransactionListDatas o1, TransactionListDatas o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	}
	
	/**
	 * Comparator rendezéshez (növekvõ) dátum szerint
	 */
	public class TransactionSortDescComparator implements Comparator<TransactionListDatas> {
		@Override
		public int compare(TransactionListDatas o1, TransactionListDatas o2) {
			return o2.getDate().compareTo(o1.getDate());
		}
	}
	
	/**
	 * Példányosítás
	 * 
	 * @param list
	 * @param getDatas
	 * @return
	 */
	public static TransactionsListFragment newInstance(ArrayList<TransactionListDatas> list, GetTransactionDatas getDatas) {
		TransactionsListFragment result = new TransactionsListFragment();
		
		Bundle b = new Bundle();
		// Átadjuk a tranzakció listát és a beállított adatokat
		b.putParcelableArrayList(TRANSACTIONS_LIST, list);
		b.putParcelable(GET_TRANSACTIONS_DATAS, getDatas);
		result.setArguments(b);
		
		return result;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (ITransactionsListFragment) activity;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(getArguments() != null) {
			// Lekérjük a tranzakció listát és a beállított adatokat
			transList = getArguments().getParcelableArrayList(TRANSACTIONS_LIST);
			getDatas = getArguments().getParcelable(GET_TRANSACTIONS_DATAS);
			
			// Header beállítása
			ListView lv = getListView();
			View header = getLayoutInflater(null).inflate(R.layout.transactions_header, null);
			
			final ImageView sortIcon = (ImageView) header.findViewById(R.id.transactions_header_sort_icon);
			TextView tvHeaderAccNum = (TextView) header.findViewById(R.id.tvTransHeaderAccNum);
			TextView tvHeaderDate = (TextView) header.findViewById(R.id.tvTransHeaderDate);
			
			tvHeaderAccNum.setText(getDatas.getAccount().toString());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dateDt = null;
			
			try {
				dateDt = sdf.parse(getDatas.getDateTo().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			// Date To visszaállítása egy nappal
			dateDt.setTime(dateDt.getTime() - (86400*1000));
			
			tvHeaderDate.setText(getDatas.getDateFrom().toString() + " -> " + sdf.format(dateDt));
			
			View popupView = getLayoutInflater(null).inflate(R.layout.transactions_sort_popup, null);
			popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			final LinearLayout layoutSortAsc = (LinearLayout) popupView.findViewById(R.id.transactionsSortByDateAsc);
			final LinearLayout layoutSortDesc = (LinearLayout) popupView.findViewById(R.id.transactionsSortByDateDesc);
			
			// Rendezõ ikon listener
			sortIcon.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					popupWindow.setBackgroundDrawable(new BitmapDrawable());
					popupWindow.setOutsideTouchable(true);
					popupWindow.setFocusable(true);
					popupWindow.showAsDropDown(sortIcon, 0, 0);
				}
			});
			
			// Rendezõ menüelem listener
			layoutSortAsc.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					// Rendezés és lista frissítése
					Collections.sort(transList, new TransactionSortAscComparator());
					adapter.notifyDataSetChanged();
				}
			});
			
			// Rendezõ menüelem listener
			layoutSortDesc.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					// Rendezés és lista frissítése
					Collections.sort(transList, new TransactionSortDescComparator());
					adapter.notifyDataSetChanged();
				}
			});
			
			// adapter beállítása
			adapter = new TransactionAdapter(getSherlockActivity(), transList, getDatas.getAccount());
			
			setListAdapter(null);
			lv.addHeaderView(header, null, false);
			setListAdapter(adapter);
		}
	}
	
	/**
	 * Lista elemre kattintás listener
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Kiválasztott elem lekérése
		TransactionListDatas selected = (TransactionListDatas) getListAdapter().getItem(position-1);
		
		if(listener != null) {
			listener.onTransactionSelected(selected, getDatas.getCurrency());
		}
	}
	
	public interface ITransactionsListFragment {
		public void onTransactionSelected(TransactionListDatas tld, String curr);
	}
}
