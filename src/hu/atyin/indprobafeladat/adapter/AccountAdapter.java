package hu.atyin.indprobafeladat.adapter;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.pojos.Account;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter a számla listához
 *
 */
public class AccountAdapter extends BaseAdapter {

	// Számlák listája
	private final ArrayList<Account> accounts;
	
	public AccountAdapter(Context context, final ArrayList<Account> aAccounts) {
		accounts = aAccounts;
	}

	@Override
	public int getCount() {
		return accounts.size();
	}

	@Override
	public Object getItem(int position) {
		return accounts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Account account = accounts.get(position);
		
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Layout betöltése
		View itemView = inflater.inflate(R.layout.accounts_row, null);
		
		// Referenciák lekérése
		TextView tvAccountNumber = (TextView) itemView.findViewById(R.id.rowAccountNr);
		TextView tvAmount = (TextView) itemView.findViewById(R.id.rowAmount);
		TextView tvCurrency = (TextView) itemView.findViewById(R.id.rowCurrency);
		
		// Listaelem beállítása
		tvAccountNumber.setText(account.getAccountNr());
		tvAmount.setText(String.valueOf(account.getAmount()));
		tvCurrency.setText(account.getCurrency());
		
		return itemView;
	}

}
