package hu.atyin.indprobafeladat;

import hu.atyin.indprobafeladat.asynctask.AccountsAsyncTask;
import hu.atyin.indprobafeladat.asynctask.GetTransactionsAsyncTask;
import hu.atyin.indprobafeladat.asynctask.LogoutAsyncTask;
import hu.atyin.indprobafeladat.fragment.AccountsListFragment;
import hu.atyin.indprobafeladat.fragment.GetTransactionsFragment;
import hu.atyin.indprobafeladat.fragment.TransactionDetailsFragment;
import hu.atyin.indprobafeladat.fragment.TransactionsListFragment;
import hu.atyin.indprobafeladat.fragment.TransactionsListFragment.ITransactionsListFragment;
import hu.atyin.indprobafeladat.fragment.TransferFragment;
import hu.atyin.indprobafeladat.network.NetworkManager;
import hu.atyin.indprobafeladat.pojos.Account;
import hu.atyin.indprobafeladat.pojos.GetTransactionDatas;
import hu.atyin.indprobafeladat.pojos.TransactionListDatas;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * 
 * Fõ kezelõfelület, ide töltõdnek be a Fragment-ek
 *
 */
public class MainActivity extends SherlockFragmentActivity implements ITransactionsListFragment {
	
	// Konstansok
	private static final String TRANSACTIONS_LIST = "TransList";
	private static final String GET_TRANSACTIONS_DATAS = "GetTransDatas";
	private static final String USERNAME = "username";
	
	// Ha rögtön login után vagyunk, akkor true
	private boolean isAfterLogin = true;
	
	// Számlák
	private ArrayList<Account> accountsList;
	
	// Tranzakciók
	private ArrayList<TransactionListDatas> transactionsList;
	private GetTransactionDatas transDatas;
	
	// Kiválasztott tranzakció
	private TransactionListDatas transListDatas;
	private String transCurrency;
	
	// Sikeres átutalás után automatikusan frissül és betöltõdik a számla lista
	private BroadcastReceiver mSuccessTransferRefresh = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getAccountsListFragment();
		}
	};
	
	// Számlatörténet dialógus ablak után lista lekérése a tranzakciókkal
	private BroadcastReceiver mSuccessGetTransactions = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			transactionsList = intent.getParcelableArrayListExtra(TRANSACTIONS_LIST);
			transDatas = intent.getParcelableExtra(GET_TRANSACTIONS_DATAS);
			
			getTransactionsListFragment();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		// ActionBar beállítása
		ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setIcon(R.drawable.actionbar_logo);
		
		// Username lekérése
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			actionbar.setTitle(extras.getString(USERNAME));
		}
		
		// Számla lista betöltése
		getAccountsListFragment();
		isAfterLogin = false;
		
		// Feliratkozás a LocalBroadCast-ekre
		LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mSuccessTransferRefresh, new IntentFilter("Success-Transfer-Refresh"));
		LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mSuccessGetTransactions, new IntentFilter("Success-Get-Transactions"));
		
	}
	
	@Override
	protected void onDestroy() {
		// Leiratkozás a LocalBroadCast-ekrõl
		LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mSuccessTransferRefresh);
		LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mSuccessGetTransactions);
		super.onDestroy();
	}
	
	/**
	 * Számla lista betöltése
	 */
	public void getAccountsListFragment() {
		try {
			accountsList = new AccountsAsyncTask(MainActivity.this).execute().get();
			
			if(accountsList != null) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
				ft.replace(R.id.fragmentContainer, AccountsListFragment.newInstance(accountsList), AccountsListFragment.TAG);
				
				// Bejelentkezés után az elsõt nem adja hozzá a BackStack-hez, a többit már igen
				if(!isAfterLogin) {
					ft.addToBackStack(AccountsListFragment.TAG);
				}
				
				ft.commit();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tranzakció lista betöltése
	 */
	public void getTransactionsListFragment() {		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
		ft.replace(R.id.fragmentContainer, TransactionsListFragment.newInstance(transactionsList, transDatas), TransactionsListFragment.TAG);
		ft.addToBackStack(TransactionsListFragment.TAG);
		ft.commit();
	}
	
	/**
	 * Kiválasztott tranzakció részleteinek betöltése
	 * 
	 * @param tld -> Tranzakció részletek
	 * @param curr -> pénznem
	 */
	public void getTransactionDetailsFragment(TransactionListDatas tld, String curr) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
		ft.replace(R.id.fragmentContainer, TransactionDetailsFragment.newInstance(transListDatas, transCurrency), TransactionDetailsFragment.TAG);
		ft.addToBackStack(TransactionDetailsFragment.TAG);
		ft.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		// Ha nincs számla, akkor letiltjuk a felesleges menüpontokat
		if(AccountsListFragment.hasAccounts == false) {
			MenuItem refreshItem = menu.findItem(R.id.menuRefresh);
			MenuItem accountsItem = menu.findItem(R.id.menuAccounts);
			MenuItem transferItem = menu.findItem(R.id.menuTransfer);
			MenuItem transactionsItem = menu.findItem(R.id.menuTransactions);
			refreshItem.setVisible(false);
			accountsItem.setVisible(false);
			transferItem.setVisible(false);
			transactionsItem.setVisible(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Számlák menüpont
		if(item.getItemId() == R.id.menuAccounts) {
			getAccountsListFragment();
		}
		
		//Frissítés menüpont
		else if(item.getItemId() == R.id.menuRefresh) {
			FragmentManager fm = getSupportFragmentManager();
			
			// BackStack tetején lévõ Fragment
			String actFragmentTag;
			
			// Ha üres a BackStack
			if(fm.getBackStackEntryCount() == 0) {
				// Beállítás a számlákra
				actFragmentTag = AccountsListFragment.TAG;
			}
			else {
				// Beállítás a legfelül lévõre
				actFragmentTag = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
			}
			
			Log.d("Act. Fragment TAG", actFragmentTag);
			
			// Számlák frissítése
			if(actFragmentTag.equals(AccountsListFragment.TAG)) {
				getAccountsListFragment();
			}
			
			// Tranzakciók frissítése
			else if(actFragmentTag.equals(TransactionsListFragment.TAG)) {
				new GetTransactionsAsyncTask(MainActivity.this).execute(transDatas);
			}
		}
		
		// Home button
		else if(item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		
		// Kijelentkezés menüpont
		else if(item.getItemId() == R.id.menuLogout) {
			showLogoutAlert(MainActivity.this);
		}
		
		// Infó menüpont
		else if(item.getItemId() == R.id.menuInfo) {
			showInfoAlert(MainActivity.this);
		}
		
		// Átutalás menüpont
		else if(item.getItemId() == R.id.menuTransfer) {
			TransferFragment transferFragment = TransferFragment.newInstance(accountsList);
			FragmentManager fm = getSupportFragmentManager();
			Fragment fragment = fm.findFragmentByTag(AccountsListFragment.TAG);
			transferFragment.setTargetFragment(fragment, 0);
			transferFragment.show(fm, TransferFragment.TAG);
		}
		
		// Számlatörténet menüpont
		else if(item.getItemId() == R.id.menuTransactions) {
			GetTransactionsFragment getTransactionFragment = GetTransactionsFragment.newInstance(accountsList);
			FragmentManager fm = getSupportFragmentManager();
			Fragment fragment = fm.findFragmentByTag(AccountsListFragment.TAG);
			getTransactionFragment.setTargetFragment(fragment, 0);
			getTransactionFragment.show(fm, GetTransactionsFragment.TAG);
		}
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		
		// Ha van a BackStack-en Fragment, akkor levesszük
		if(fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		}
		
		// Ha nincs, akkor logout
		else {
			showLogoutAlert(MainActivity.this);	
		}
	}
	
	/**
	 * Kijelentkezés dialógus ablak
	 * 
	 * @param context
	 */
	public void showLogoutAlert(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.alert_logout_title);
		alert.setMessage(R.string.alert_logout_message);
		alert.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					dialog.dismiss();
					String res = new LogoutAsyncTask(context).execute().get();
					
					// Ha valami hiba van
					if(res.startsWith("Error") || res.equals(null)) {
						NetworkManager.showNetworkErrorAlert(context, res);
					}
					// Ha nincs, bezárjük az Activity-t és visszatérünk a login-hoz
					else {
						MainActivity.this.finish();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}).setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alert.show();
	}
	
	/**
	 * Infó dialógus ablak
	 * 
	 * @param context
	 */
	public void showInfoAlert(Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setMessage(R.string.alert_info_message);
		alert.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = alert.show();
		TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
		// Szöveg középre igazítása
		messageText.setGravity(Gravity.CENTER);
	}

	/**
	 * Tranzakció kiválasztásakor a detail Fragment betöltése
	 */
	@Override
	public void onTransactionSelected(TransactionListDatas tld, String curr) {
		transListDatas = tld;
		transCurrency = curr;
		getTransactionDetailsFragment(transListDatas, transCurrency);
	}
}
