package hu.atyin.indprobafeladat.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Számla objektum
 *
 */
public class Account implements Parcelable {
	private long userId;
	private String accountNr;
	private long amount;
	private String currency;
	
	public Account(long aUserId, String aAccountNr, long aAmount, String aCurrency) {
		userId = aUserId;
		accountNr = aAccountNr;
		amount = aAmount;
		currency = aCurrency;
	}
	
	public long getUserId() {
		return userId;
	}

	public String getAccountNr() {
		return accountNr;
	}

	public long getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(userId);
		dest.writeString(accountNr);
		dest.writeLong(amount);
		dest.writeString(currency);
		
	}
}
