package hu.atyin.indprobafeladat.network;

import android.content.Context;

public interface HttpRequestValues {
	public static final String POST_LOGIN_URL = "http://m.indgroup.eu/ind.example.bank/user/authenticate";
	public static final String GET_LOGOUT_URL = "http://m.indgroup.eu/ind.example.bank/user/logout";
	public static final String GET_ACCOUNTS_URL = "http://m.indgroup.eu/ind.example.bank/bank/accounts";
	public static final String GET_TRANSACTIONS_URL = "http://m.indgroup.eu/ind.example.bank/bank/transactions";
	public static final String POST_TRANSFER_URL = "http://m.indgroup.eu/ind.example.bank/bank/doTransfer";
	
	public String getJSONLoginDatas(Context context, final String loginId, final String password);
	public String logout(Context context);
	public String getJSONAccounts(Context context);
	public String doTransfer(Context context, String sourceAccount, String targetAccount, int amount, String description);
	public String getTransactions(Context context, String dateFrom, String dateTo, String accountNr);
}
