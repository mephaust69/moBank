package hu.atyin.indprobafeladat.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Számlatörténet beállítások objektum
 *
 */
public class GetTransactionDatas implements Parcelable {
	
	private String account;
	private String currency;
	private String dateFrom;
	private String dateTo;
	
	public GetTransactionDatas(String aAccount, String aCurrency, String aDateFrom, String aDateTo) {
		account = aAccount;
		currency = aCurrency;
		dateFrom = aDateFrom;
		dateTo = aDateTo;
	}

	public String getAccount() {
		return account;
	}
	
	public String getCurrency() {
		return currency;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(account);
		dest.writeString(currency);
		dest.writeString(dateFrom);
		dest.writeString(dateTo);		
	}
}
