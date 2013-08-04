package hu.atyin.indprobafeladat.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Számlatörténet objektum
 *
 */
public class TransactionListDatas implements Parcelable {
	private String sourceAccount;
	private String targetAccount;
	private int amount;
	private String date;
	private String description;
	
	public TransactionListDatas(String aSourceAccount, String aTargetAccount, int aAmount, String aDate, String aDescription) {
		sourceAccount = aSourceAccount;
		targetAccount = aTargetAccount;
		amount = aAmount;
		date = aDate;
		description = aDescription;
	}

	public String getSourceAccount() {
		return sourceAccount;
	}

	public String getTargetAccount() {
		return targetAccount;
	}

	public int getAmount() {
		return amount;
	}

	public String getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sourceAccount);
		dest.writeString(targetAccount);
		dest.writeInt(amount);
		dest.writeString(date);
		dest.writeString(description);
	}
}
