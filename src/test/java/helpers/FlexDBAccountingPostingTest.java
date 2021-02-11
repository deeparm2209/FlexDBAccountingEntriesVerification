package helpers;

public class FlexDBAccountingPostingTest {
	
	public static void main(String[] args) throws Exception {
		
	    ValidateDebtorTransactionInFlex validateDebtorTransactionInFlex = new ValidateDebtorTransactionInFlex();
	    ValidateCreditorTransactionInFlex validateCreditorTransactionInFlex = new ValidateCreditorTransactionInFlex();
	    ValidateProcessingSuspenseAccount validateProcessingSuspenseAccount = new ValidateProcessingSuspenseAccount();
	    
		String strCreditorAccountNumber = "2010155994";
		String strCurrentEndToEndId = "KESAP001-ENDTOENDID01-20210126801";
		String CreditorControlSum = "8.1";
		String strDebtorAccountNumber = "2010112381";

		System.out.println("*******************Debtor Transaction Records *******************");
		validateDebtorTransactionInFlex.validateDebtorTransactionInFlexSingleTxn(strDebtorAccountNumber, 
				strCurrentEndToEndId, CreditorControlSum);

		System.out.println("*******************Creditor Transaction Records *******************");
		validateCreditorTransactionInFlex.validateCreditorTransactionInFlexSingleTxn(strCreditorAccountNumber, 
				strCurrentEndToEndId, CreditorControlSum);
		
		String strProcessingSuspenseAccount = "02730235111916";
		String strTestTransactionCategory ="On_Us";
		String strDebtorWaiveChargesOption = "";

		System.out.println("*******************Processing Suspense Account Credit Leg of Transaction *******************");
		validateProcessingSuspenseAccount.validateCreditorProcessingSuspenseAccount(strProcessingSuspenseAccount,
				strCreditorAccountNumber,CreditorControlSum,strCurrentEndToEndId,
				strTestTransactionCategory,strDebtorWaiveChargesOption);

		System.out.println("*******************Processing Suspense Account Debit Leg of Transaction *******************");
		validateProcessingSuspenseAccount.validateDebtorProcessingSuspenseAccount(strProcessingSuspenseAccount,
				strDebtorAccountNumber,CreditorControlSum,strCurrentEndToEndId,
				strTestTransactionCategory,strDebtorWaiveChargesOption);
	}

}
