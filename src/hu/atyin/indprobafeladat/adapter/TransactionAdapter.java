package hu.atyin.indprobafeladat.adapter;

import hu.atyin.indprobafeladat.R;
import hu.atyin.indprobafeladat.pojos.TransactionListDatas;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter a sz�mlat�rt�nethez
 *
 */
public class TransactionAdapter extends BaseAdapter {

	// Tranzakci� adatok list�ja
	private final ArrayList<TransactionListDatas> transactions;
	// Kiv�lasztott sz�mla
	private final String tAccNum;
	
	public TransactionAdapter(Context context, ArrayList<TransactionListDatas> aTransactions, String aTAccNum) {
		transactions = aTransactions;
		tAccNum = aTAccNum;
	}
	
	@Override
	public int getCount() {
		return transactions.size();
	}

	@Override
	public Object getItem(int arg0) {
		return transactions.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TransactionListDatas transaction = transactions.get(position);
		
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Layout bet�lt�se
		View itemView = inflater.inflate(R.layout.transactions_row, null);
		
		// Referenci�k lek�r�se
		TextView tvTransactionsAccNum = (TextView) itemView.findViewById(R.id.tvTransactionsAccNum);
		TextView tvTransactionsAmount = (TextView) itemView.findViewById(R.id.tvTransactionsAmount);
		
		// Ha a sz�ml�r�l utaltak, akkor piros �s minusz
		if(tAccNum.equals(transaction.getSourceAccount())) {
			tvTransactionsAccNum.setText(transaction.getTargetAccount().toString());
			tvTransactionsAmount.setText("-" + transaction.getAmount());
			tvTransactionsAmount.setTextColor(Color.RED);
		}
		// Ha a sz�ml�ra utaltak, akkor z�ld
		else if(tAccNum.equals(transaction.getTargetAccount())) {
			tvTransactionsAccNum.setText(transaction.getSourceAccount().toString());
			tvTransactionsAmount.setText("" + transaction.getAmount());
			tvTransactionsAmount.setTextColor(Color.GREEN);
		}
		
		return itemView;
	}
}
