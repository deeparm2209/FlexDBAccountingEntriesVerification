package helpers;

import java.util.Arrays;
import java.util.List;

public class ValidateProcessingSuspenseAccount {

	public String strQueryGetCreditorTxnRecord = null;
	public String strQueryGetDebtorTxnRecord = null;
	public List<String> strCreditorTxnRecord = null;
	public List<String> strDebitedTxnRecord = null;

	public static String strWorkflowReferenceUniqueTxt = "";
	public static String strOracleRecordColumnName_1 = "COD_ACCT_NO";// Column name in the DB
	public static String strOracleRecordColumnName_2 = "COD_CC_BRN";// Column name in the DB
	public static String strOracleRecordColumnName_3 = "COD_ACC_CCY";// Column name in the DB
	public static String strOracleRecordColumnName_4 = "FLG_DRCR";// Column name in the DB
	public static String strOracleRecordColumnName_5 = "AMT_TXN_TCY";// Column name in the DB
	public static String strOracleRecordColumnName_6 = "AMT_TXN_LCY";// Column name in the DB
	public static String strOracleRecordColumnName_7 = "COD_FROM_ACCT_NO";// Column name in the DB
	public static String strOracleRecordColumnName_8 = "REF_TXN_NO";// Column name in the DB
	public static String strOracleRecordColumnName_9 = "TXN_NRRTV";// Column name in the DB
	public static String strOracleRecordColumnName_10 = "DAT_TXN_STR";// Column name in the DB
	public static String strOracleRecordColumnName_11 = "DAT_VALUE_STR";// Column name in the DB

	// Flex Validations variables
	public String strFlexTestValidations = null;// To set the status to Pass/Fail
	String strSuspenseCreditAccountActualFeesDescription = "N/A";
	String strSuspenseDebitAccountActualFeesDescription = "N/A";
	String strDebitAccountActualFeesDescription = "N/A";
	String strPNLCreditAccountActualFeesDescription = "N/A";
	String strDifferentChargesDebitAccountActualFeesDescription = "N/A";
	String doubleExpectedFeesAmountApplied = "N/A";
	String doubleSuspenseCreditAccountActualFeesAmount = "N/A";
	String doubleSuspenseDebitAccountActualFeesAmount = "N/A";
	String doubleClearingAccountCreditAccountActualAmountTotal = "N/A";
	String doublePNLCreditAccountFeesAmount = "N/A";
	String doubleDifferentChargesDebitAccountFeesAmount = "N/A";
	String strSuspenseCreditAccountFeesAmountAssertion = "N/A";
	String strSuspenseDebitAccountFeesAmountAssertion = "N/A";
	String strClearingAccountCreditAccountTransactionsAmountAssertion = "N/A";
	String strPNLCreditAccountFeesAmountAssertion = "N/A";
	String strDifferentChargesDebitAccountFeesAmountAssertion = "N/A";

	// Credited suspense account variables
	public double dbCrAmountTCY;
	public double dbCrAmountTCYActualTotal;
	public double dbCrAmountLCY;
	public double dbCrAmountLCYActualTotal;
	public double dbCrCreditorControlSum;
	String strSuspenseAccountCreditedAmountValidation;
	String strSuspenseAccountCreditTransactionFeesValidations;

	// Debited suspense account variables
	public double dbDbAmountTCY;
	public double dbDbAmountTCYActualTotal;
	public double dbDbAmountLCY;
	public double dbDbAmountLCYActualTotal;
	String strSuspenseAccountDebtorAmountValidation;
	String strSuspenseAccountDebitATransactionFeesValidations;

	// Clearing Account variables

	// PNL Account variables
	String strPNLCreditAccountTransactionFeesValidations;// TODO

	// Different charges Account variables
	String strDifferentChargesDebitAccountTransactionFeesValidations;// TODO

	DatabaseUtil DatabaseUtil = new DatabaseUtil();

	/** Check the transactions suspense account for credits*/
	public void validateCreditorProcessingSuspenseAccount(String ProcessingSuspenseAccount,
			String strCreditorAccountNumber, String CreditorControlSum, String strCurrentEndToEndId,
			String TestTransactionCategory, String DebtorWaiveChargesOption) throws Exception {

		String dbDriver = "oracle.jdbc.OracleDriver";

		// Check the that the IAT transaction does'nt check the suspense account.
		if (TestTransactionCategory.equalsIgnoreCase("IAT")) {
			System.out.println("This is an IAT transaction,therefore the Suspense Account will not be checked");
		} else {
			// **************************GET THE AMOUNT CREDITED TO THE SUSPENSE
			// ACCOUNT*********************************
			// **********************************************************************************************************
			try {
				int i = 0;
				int intDatabaseWaitTimeInde = 2;

				// Retry the SQL Query execution if there is no payload in the DB on the first
				// attempt
				try {

					do {

						/**
						 * =================Determine which database to use ==========================================
						 */
						if (dbDriver.contains("postgresql")) {
							System.out.println("Using Postgresql database");

							// Retrieve the transaction record from the Database using the debtor account
							// number and EndToEndID
							strQueryGetCreditorTxnRecord = "";

							// Store the record in a string
							strCreditorTxnRecord = DatabaseUtil.databaseHandlerForFlexDB(strQueryGetCreditorTxnRecord,
									strOracleRecordColumnName_1, strOracleRecordColumnName_2,
									strOracleRecordColumnName_3, strOracleRecordColumnName_4,
									strOracleRecordColumnName_5, strOracleRecordColumnName_6,
									strOracleRecordColumnName_7, strOracleRecordColumnName_8,
									strOracleRecordColumnName_9, strOracleRecordColumnName_10,
									strOracleRecordColumnName_11);
						} else if (dbDriver.contains("mysql") || dbDriver.contains("oracle")) {
							System.out.println("Using oracle database");
							// Retrieve the transaction record from the Database using the debtor account
							// number and EndToEndID
							/// **SQL query For Flex DB*/
							strQueryGetCreditorTxnRecord = "SELECT COD_ACCT_NO,COD_CC_BRN,COD_ACC_CCY,FLG_DRCR,AMT_TXN_TCY,AMT_TXN_LCY,COD_FROM_ACCT_NO,REF_TXN_NO,TXN_NRRTV,DAT_TXN_STR,DAT_VALUE_STR "
									+ " FROM XF_OL_ST_TXNLOG_CURRENT WHERE " 
									+ " REF_USR_NO = '" + strCurrentEndToEndId + "'" 
									+ " AND FLG_DRCR ='C' " 
									+ " ORDER BY COD_FROM_ACCT_NO ASC";

							// Store the record in a string
							strCreditorTxnRecord = DatabaseUtil.databaseHandlerForFlexDB(strQueryGetCreditorTxnRecord,
									strOracleRecordColumnName_1, strOracleRecordColumnName_2,
									strOracleRecordColumnName_3, strOracleRecordColumnName_4,
									strOracleRecordColumnName_5, strOracleRecordColumnName_6,
									strOracleRecordColumnName_7, strOracleRecordColumnName_8,
									strOracleRecordColumnName_9, strOracleRecordColumnName_10,
									strOracleRecordColumnName_11);
						}
						
						//Save the record in an array
                        if (strCreditorTxnRecord != null)
                        {
                        	/**Iterate through the number of transaction found to get all the amounts and add them up*/
                            for (int j = 0; j < strCreditorTxnRecord.size(); j++)
                            {
                                String strDBresults = String.valueOf(strCreditorTxnRecord.get(j));
                                String[] splited = strDBresults.split("\\|"); //Use | as the separator to split the string 

                                //Store split records in array
                                String strCCrod_Acct_No = splited[0];
                                String strCrCod_CC_Brn = splited[1];
                                String strCrCod_acc_ccy = splited[2];
                                String strCrFlg_Drcr = splited[3];
                                String strCrAmt_Txn_tcy = splited[4].replaceAll("[^a-zA-Z0-9\\\\._-]", "");
                                String strCrAmt_Txn_lcy = splited[5].replaceAll("[^a-zA-Z0-9\\\\._-]", "");
                                String strCod_From_Acct_No = splited[6];                       
                                String strCrDat_Txn = splited[7];
                                String strCrTxn_Nrrtv = splited[8];
                                String strCrDat_Txn_Str = splited[9];
                                String strCrDat_Value = splited[10];

                                dbCrAmountTCY = Double.parseDouble(strCrAmt_Txn_tcy);
                                dbCrAmountLCY = Double.parseDouble(strCrAmt_Txn_lcy);

                                //Add the amount in each iteration to get the total sums
                                dbCrAmountTCYActualTotal += dbCrAmountTCY;
                                dbCrAmountLCYActualTotal += dbCrAmountLCY;
                                
                                if (dbCrAmountTCYActualTotal == dbCrAmountLCYActualTotal) {
        							
        							// Print out the results
        							/** INFO */
        							System.out.println("Status=PASS: Processing Suspense Account Credit Transaction Record from Flex DB below");
        							System.out.println("===========================================================");
        							System.out.println();

        							String[][] data = {
        									// Headings
        									{ "COD_ACCT_NO", "         COD_CC_BRN", "   COD_ACC_CCY", "  FLG_DRCR",
        											"	AMT_TXN_TCY", "	AMT_TXN_LCY", " COD_FROM_ACCT_NO", 
        											"  DAT_TXN_STR	",  "  	 REF_USR_NO",
        											"			      REF_TXN_NO", "   					TXN_NRRTV" },
        									{ "=================", " ===============", " ===========", "==========",
        											"============", "	============", " =================", 
        											" ===================", "  ==================================", 
        											"=======================", "				================================================================" } };

        							// Loop through all rows
        							for (String[] row : data)
        								System.out.println(Arrays.toString(row));

        							System.out.println(strCCrod_Acct_No + ",	" + strCrCod_CC_Brn + ", 		" + strCrCod_acc_ccy
        									+ ",   	" + strCrFlg_Drcr + ",   	 " + strCrAmt_Txn_tcy + ",  	 	"
        									+ strCrAmt_Txn_lcy + ", 	 	" + strCod_From_Acct_No + 	"," + strCrDat_Txn_Str 
        									+  ", "	+ strCurrentEndToEndId + ", " 
        									+  strCrDat_Txn + ",   " + strCrTxn_Nrrtv);
        							System.out.println();
                                }
                                else {
                                	// Print out the results
        							/** INFO */
        							System.out.println("Status=FAILED: Creditor Transaction Amount is not equal to expected,see transaction from Flex DB below");
        							System.out.println("===========================================================");
        							System.out.println();
        							
        							String[][] data = {
        									// Headings
        									{ "COD_ACCT_NO", "         COD_CC_BRN", "   COD_ACC_CCY", "  FLG_DRCR",
        											"	AMT_TXN_TCY", "	AMT_TXN_LCY", " COD_FROM_ACCT_NO", 
        											"  DAT_TXN_STR	",  "  	 REF_USR_NO",
        											"			      REF_TXN_NO", "   					TXN_NRRTV" },
        									{ "=================", " ===============", " ===========", "==========",
        											"============", "	============", " =================", 
        											" ===================", "  ==================================", 
        											"=======================", "				================================================================" } };

        							// Loop through all rows
        							for (String[] row : data)
        								System.out.println(Arrays.toString(row));

        							System.out.println(strCCrod_Acct_No + ",	" + strCrCod_CC_Brn + ", 		" + strCrCod_acc_ccy
        									+ ",   	" + strCrFlg_Drcr + ",   	 " + strCrAmt_Txn_tcy + ",  	 	"
        									+ strCrAmt_Txn_lcy + ", 	 	" + strCod_From_Acct_No + 	"," + strCrDat_Txn_Str 
        									+  ", "	+ strCurrentEndToEndId + ", " 
        									+  strCrDat_Txn + ",   " + strCrTxn_Nrrtv);
        							System.out.println();
                                }
                            }
                        	
                        	//Exist the main DO WHILE LOOP
                            break;
                        }
                        else{
                    		System.out.println("Warning:: No Records Where Found In The Database on the 1st attempt ,the script will attempt to retry in 20 seconds");
                            Thread.sleep(20000);
                            i++;
                        }
					} while (i <= intDatabaseWaitTimeInde);
				} catch (Exception e) 
				{
					System.out.println(
							"Checking available balance on the suspense account failed due to :: " + e.getMessage());
				}
			} catch (Exception e) 
			{
				System.out.println("Database data retrieval failed :: " + e.getMessage());
			}

		}

	}
	
	/** Check the transactions suspense account for Debits*/
	public void validateDebtorProcessingSuspenseAccount(String ProcessingSuspenseAccount,
			String strDreditorAccountNumber, String CreditorControlSum, String strCurrentEndToEndId,
			String TestTransactionCategory, String DebtorWaiveChargesOption) throws Exception {

		String dbDriver = "oracle.jdbc.OracleDriver";

		// Check the that the IAT transaction doesn't check the suspense account.
		if (TestTransactionCategory.equalsIgnoreCase("IAT")) {
			System.out.println("This is an IAT transaction,therefore the Suspense Account will not be checked");
		} else {
			// **************************GET THE AMOUNT CREDITED TO THE SUSPENSE
			// ACCOUNT*********************************
			// **********************************************************************************************************
			try {
				int i = 0;
				int intDatabaseWaitTimeInde = 2;

				// Retry the SQL Query execution if there is no payload in the DB on the first
				// attempt
				try {

					do {

						/**
						 * =================Determine which database to use
						 * ==========================================
						 */
						if (dbDriver.contains("postgresql")) {
							System.out.println("Using Postgresql database");

							// Retrieve the transaction record from the Database using the debtor account
							// number and EndToEndID
							strQueryGetCreditorTxnRecord = "";

							// Store the record in a string
							strDebitedTxnRecord = DatabaseUtil.databaseHandlerForFlexDB(strQueryGetCreditorTxnRecord,
									strOracleRecordColumnName_1, strOracleRecordColumnName_2,
									strOracleRecordColumnName_3, strOracleRecordColumnName_4,
									strOracleRecordColumnName_5, strOracleRecordColumnName_6,
									strOracleRecordColumnName_7, strOracleRecordColumnName_8,
									strOracleRecordColumnName_9, strOracleRecordColumnName_10,
									strOracleRecordColumnName_11);
						} else if (dbDriver.contains("mysql") || dbDriver.contains("oracle")) {
							System.out.println("Using oracle database");
							// Retrieve the transaction record from the Database using the debtor account number and EndToEndID
							/// **SQL query For Flex DB*/
							strQueryGetDebtorTxnRecord = "SELECT COD_ACCT_NO,COD_CC_BRN,COD_ACC_CCY,FLG_DRCR,AMT_TXN_TCY,AMT_TXN_LCY,COD_FROM_ACCT_NO,REF_TXN_NO,TXN_NRRTV,DAT_TXN_STR,DAT_VALUE_STR "
									+ " FROM XF_OL_ST_TXNLOG_CURRENT WHERE " 
									+ " REF_USR_NO = '" + strCurrentEndToEndId + "'" 
									+ " AND FLG_DRCR ='D' " 
									+ " ORDER BY COD_FROM_ACCT_NO ASC";

							// Store the record in a string
							strDebitedTxnRecord = DatabaseUtil.databaseHandlerForFlexDB(strQueryGetDebtorTxnRecord,
									strOracleRecordColumnName_1, strOracleRecordColumnName_2,
									strOracleRecordColumnName_3, strOracleRecordColumnName_4,
									strOracleRecordColumnName_5, strOracleRecordColumnName_6,
									strOracleRecordColumnName_7, strOracleRecordColumnName_8,
									strOracleRecordColumnName_9, strOracleRecordColumnName_10,
									strOracleRecordColumnName_11);
						}
						
						//Save the record in an array
                        if (strDebitedTxnRecord != null)
                        {
                        	/**Iterate through the number of transaction found to get all the amounts and add them up*/
                            for (int j = 0; j < strDebitedTxnRecord.size(); j++)
                            {
                                String strDBresults = String.valueOf(strDebitedTxnRecord.get(j));
                                String[] splited = strDBresults.split("\\|"); //Use | as the separator to split the string 

                                //Store split records in array
                                String strCCrod_Acct_No = splited[0];
                                String strDrCod_CC_Brn = splited[1];
                                String strDrCod_acc_ccy = splited[2];
                                String strDrFlg_Drcr = splited[3];
                                String strDrAmt_Txn_tcy = splited[4].replaceAll("[^a-zA-Z0-9\\\\._-]", "");
                                String strDrAmt_Txn_lcy = splited[5].replaceAll("[^a-zA-Z0-9\\\\._-]", "");
                                String strCod_From_Acct_No = splited[6];                       
                                String strDrDat_Txn = splited[7];
                                String strDrTxn_Nrrtv = splited[8];
                                String strDrDat_Txn_Str = splited[9];
                                String strDrDat_Value = splited[10];

                                dbCrAmountTCY = Double.parseDouble(strDrAmt_Txn_tcy);
                                dbCrAmountLCY = Double.parseDouble(strDrAmt_Txn_lcy);

                                //Add the amount in each iteration to get the total sums
                                dbCrAmountTCYActualTotal += dbCrAmountTCY;
                                dbCrAmountLCYActualTotal += dbCrAmountLCY;
                                
                                if (dbCrAmountTCYActualTotal == dbCrAmountLCYActualTotal) {
        							
        							// Print out the results
        							/** INFO */
        							System.out.println("Status=PASS: Processing Suspense Account Debit Transaction Record from Flex DB below");
        							System.out.println("===========================================================");
        							System.out.println();

        							String[][] data = {
        									// Headings
        									{ "COD_ACCT_NO", "         COD_CC_BRN", "   COD_ACC_CCY", "  FLG_DRCR",
        											"	AMT_TXN_TCY", "	AMT_TXN_LCY", " COD_FROM_ACCT_NO", 
        											"  DAT_TXN_STR	",  "  	 REF_USR_NO",
        											"			      REF_TXN_NO", "   					TXN_NRRTV" },
        									{ "=================", " ===============", " ===========", "==========",
        											"============", "	============", " =================", 
        											" ===================", "  ==================================", 
        											"=======================", "				================================================================" } };

        							// Loop through all rows
        							for (String[] row : data)
        								System.out.println(Arrays.toString(row));

        							System.out.println(strCCrod_Acct_No + ",	" + strDrCod_CC_Brn + ", 		" + strDrCod_acc_ccy
        									+ ",   	" + strDrFlg_Drcr + ",   	 " + strDrAmt_Txn_tcy + ",  	 	"
        									+ strDrAmt_Txn_lcy + ", 	 	" + strCod_From_Acct_No + 	"," + strDrDat_Txn_Str 
        									+  ", "	+ strCurrentEndToEndId + ", " 
        									+  strDrDat_Txn + ",   " + strDrTxn_Nrrtv);
        							System.out.println();
                                }
                                else {
                                	// Print out the results
        							/** INFO */
        							System.out.println("Status=FAILED: Debtor Transaction Amount is not equal to expected,see transaction from Flex DB below");
        							System.out.println("===========================================================");
        							System.out.println();
        							
        							String[][] data = {
        									// Headings
        									{ "COD_ACCT_NO", "         COD_CC_BRN", "   COD_ACC_CCY", "  FLG_DRCR",
        											"	AMT_TXN_TCY", "	AMT_TXN_LCY", " COD_FROM_ACCT_NO", 
        											"  DAT_TXN_STR	",  "  	 REF_USR_NO",
        											"			      REF_TXN_NO", "   					TXN_NRRTV" },
        									{ "=================", " ===============", " ===========", "==========",
        											"============", "	============", " =================", 
        											" ===================", "  ==================================", 
        											"=======================", "				================================================================" } };

        							// Loop through all rows
        							for (String[] row : data)
        								System.out.println(Arrays.toString(row));

        							System.out.println(strCCrod_Acct_No + ",	" + strDrCod_CC_Brn + ", 		" + strDrCod_acc_ccy
        									+ ",   	" + strDrFlg_Drcr + ",   	 " + strDrAmt_Txn_tcy + ",  	 	"
        									+ strDrAmt_Txn_lcy + ", 	 	" + strCod_From_Acct_No + 	"," + strDrDat_Txn_Str 
        									+  ", "	+ strCurrentEndToEndId + ", " 
        									+  strDrDat_Txn + ",   " + strDrTxn_Nrrtv);
        							System.out.println();
                                }
                            }
                        	
                        	//Exist the main DO WHILE LOOP
                            break;
                        }
                        else{
                    		System.out.println("Warning:: No Records Where Found In The Database on the 1st attempt ,the script will attempt to retry in 20 seconds");
                            Thread.sleep(20000);
                            i++;
                        }
					} while (i <= intDatabaseWaitTimeInde);
				} catch (Exception e) 
				{
					System.out.println("Checking available balance on the suspense account failed due to :: " + e.getMessage());
				}
			} catch (Exception e) 
			{
				System.out.println("Database data retrieval failed :: " + e.getMessage());
			}

		}

	}

}