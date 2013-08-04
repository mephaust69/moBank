package hu.atyin.indprobafeladat.pojos;

/**
 * 
 * Átutalás objektum
 *
 */
public class TransferDatas {
	private String sourceAccount;
	private String targetAccount;
	private int amount;
	private String description;
	
	public TransferDatas(String aSourceAccount, String aTargetAccount, int aAmount, String aDescription) {
		sourceAccount = aSourceAccount;
		targetAccount = aTargetAccount;
		amount = aAmount;
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

	public String getDescription() {
		return description;
	}
	
}
