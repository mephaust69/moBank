package hu.atyin.indprobafeladat.fragment;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.adapter.AccountAdapter;
import hu.atyin.indprobafeladat.pojos.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ActionBar.LayoutParams;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * 
 * Számlák Fragment
 *
 */
public class AccountsListFragment extends SherlockListFragment {
	
	// Konstansok
	public static final String TAG = "AccountsListFragment";
	private static final String ACCOUNTS_LIST = "AccList";
	public static boolean hasAccounts = true;
	
	private AccountAdapter adapter;
	private PopupWindow popupWindow;
	
	ArrayList<Account> accountsList;
	
	/**
	 * Comparator rendezéshez (növekvõ) összeg szerint
	 */
	public class AccountSortAscComparator implements Comparator<Account> {
		@Override
		public int compare(Account o1, Account o2) {
			return ((Long)(o1.getAmount())).compareTo(((Long)o2.getAmount()));
		}
	}
	
	/**
	 * Comparator rendezéshez (csökkenõ) összeg szerint
	 */
	public class AccountSortDescComparator implements Comparator<Account> {
		@Override
		public int compare(Account o1, Account o2) {
			return ((Long)(o2.getAmount())).compareTo(((Long)o1.getAmount()));
		}
	}
	
	/**
	 * Példányosítás
	 * 
	 * @param list
	 * @return
	 */
	public static AccountsListFragment newInstance(ArrayList<Account> list) {
		AccountsListFragment fragment = new AccountsListFragment();
		
		Bundle b = new Bundle();
		// Lista átadása
		b.putParcelableArrayList(ACCOUNTS_LIST, list);
		fragment.setArguments(b);
		
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(getArguments() != null) {
			// Lista lekérése
			accountsList = getArguments().getParcelableArrayList(ACCOUNTS_LIST);
		}
		
		// Ha vannak számlák
		if(accountsList.size() != 0) {
			hasAccounts = true;
			
			ListView lv = getListView();
			// lista header beállítása
			View header = getLayoutInflater(null).inflate(R.layout.accounts_header, null);
			
			final ImageView sortIcon = (ImageView) header.findViewById(R.id.accounts_header_sort_icon);
			
			View popupView = getLayoutInflater(null).inflate(R.layout.accounts_sort_popup, null);
			popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			final LinearLayout layoutSortAsc = (LinearLayout) popupView.findViewById(R.id.accountsSortByAmountAsc);
			final LinearLayout layoutSortDesc = (LinearLayout) popupView.findViewById(R.id.accountsSortByAmountDesc);
			
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
					Collections.sort(accountsList, new AccountSortAscComparator());
					adapter.notifyDataSetChanged();
				}
			});
			
			// Rendezõ menüelem listener
			layoutSortDesc.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					Collections.sort(accountsList, new AccountSortDescComparator());
					adapter.notifyDataSetChanged();
				}
			});
			
			// adapter beállítása
			adapter = new AccountAdapter(getSherlockActivity(), accountsList);
			
			setListAdapter(null);
			lv.addHeaderView(header, null, false);
			setListAdapter(adapter);
			
		}
		// Ha nincs számla
		else {
			getSherlockActivity().setContentView(R.layout.main_layout_empty);
			hasAccounts = false;
		}
	}
	
}