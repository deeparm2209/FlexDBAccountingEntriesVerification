package helpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ValidateCreditorTransactionInFlex
{

	DatabaseUtil DatabaseUtil = new DatabaseUtil();
	public String strQueryGetCreditorTxnRecord = null;
	public static String strOracleRecordColumnName_1 = "DAT_TXN";// Column name in the DB
	public static String strOracleRecordColumnName_2 = "DAT_VALUE";// Column name in the DB
	public static String strOracleRecordColumnName_3 = "TXT_TXN_DESC";// Column name in the DB
	public static String strOracleRecordColumnName_4 = "TXT_TXN_USER_DESC";// Column name in the DB
	public static String strOracleRecordColumnName_5 = "REF_CHQ_NO";// Column name in the DB
	public static String strOracleRecordColumnName_6 = "COD_DRCR";// Column name in the DB
	public static String strOracleRecordColumnName_7 = "COD_CC_BRN_TXN";// Column name in the DB
	public static String strOracleRecordColumnName_8 = "AMT_TXN";// Column name in the DB
	public static String strOracleRecordColumnName_9 = "COD_TXN_CCY";// Column name in the DB
	public static String strOracleRecordColumnName_10 = "REF_TXN_NO";// Column name in the DB
	public static String strOracleRecordColumnName_11 = "REF_USR_NO";// Column name in the DB
	public List<String> strCreditorTxnRecord = null;

	public void validateCreditorTransactionInFlexSingleTxn(String strCreditorAccountNumber, String strCurrentEndToEndId,
			String CreditorControlSum) throws IOException {

		/** Initialize config reader to read configs from Settings */
		String dbDriver = "oracle.jdbc.OracleDriver";

		// *****************************Get,Validate and store the PAIN001
		// XML**************************************************************
		try {
			int i = 0;
			int intDatabaseWaitTimeIndex = 2;

			// Retry the SQL Query execution if there is no payload in the DB on the first
			// attempt
			try {
				do {
					/**
					 * =================Determine which database to use
					 * ======================================
					 */
					if (dbDriver.contains("postgresql")) {
						System.out.println("Using Postgresql database");

						// Retrieve the transaction record from the Database using the Creditor account no and EndToEndID
						strQueryGetCreditorTxnRecord = "";

						// Store the record in a string
						strCreditorTxnRecord = DatabaseUtil.databaseHandlerForFlexDB(strQueryGetCreditorTxnRecord,
								strOracleRecordColumnName_1, strOracleRecordColumnName_2, strOracleRecordColumnName_3,
								strOracleRecordColumnName_4, strOracleRecordColumnName_5, strOracleRecordColumnName_6,
								strOracleRecordColumnName_7, strOracleRecordColumnName_8, strOracleRecordColumnName_9,
								strOracleRecordColumnName_10, strOracleRecordColumnName_11);
					} else if (dbDriver.contains("mysql") || dbDriver.contains("oracle")) {
						System.out.println("Using oracle database");

						// Retrieve the transaction record from the Database using the Creditor account number and EndToEndID
						/// **SQL query For Flex DB*/
						strQueryGetCreditorTxnRecord = "SELECT DAT_TXN,DAT_VALUE,TXT_TXN_DESC,TXT_TXN_USER_DESC,REF_CHQ_NO,COD_DRCR,COD_CC_BRN_TXN,AMT_TXN,COD_TXN_CCY,REF_TXN_NO,REF_USR_NO "
								+ "FROM Ch_nobook " + "WHERE Cod_acct_no = '" + strCreditorAccountNumber + "' "
								+ "AND REF_USR_NO = '" + strCurrentEndToEndId + "' "
								+ "AND REF_USR_NO IS NOT NULL ORDER BY 1 DESC";

						// Store the record in a string
						strCreditorTxnRecord = DatabaseUtil.databaseHandlerForFlexDB(strQueryGetCreditorTxnRecord,
								strOracleRecordColumnName_1, strOracleRecordColumnName_2, strOracleRecordColumnName_3,
								strOracleRecordColumnName_4, strOracleRecordColumnName_5, strOracleRecordColumnName_6,
								strOracleRecordColumnName_7, strOracleRecordColumnName_8, strOracleRecordColumnName_9,
								strOracleRecordColumnName_10, strOracleRecordColumnName_11);
					}

					// Save the record to a file
					if (strCreditorTxnRecord != null) {
						String string = String.valueOf(strCreditorTxnRecord);

						String[] splited = string.split("\\|"); // Use "|" as the separator to split the string
						String strValidationStatus = null; // To set the status to Pass/Fail

						// Store split records in array
						String strTxn_Date = splited[0];
						String strValue_Data = splited[1];
						String strDescription = splited[2];
						String strDescription1 = splited[3];
						String strUser_Description = splited[4];
						String strCheque_No = splited[5];
						String strDr_Cr = splited[6];
						String strOrig_Brn = splited[7];
						String strAmount = splited[8].replaceAll("[^a-zA-Z0-9\\\\._-]", "");// Remove the white spaces
						String strTxnCcy = splited[9];
						String strRef_Txn_No = splited[10];
						String strEndToEndId = splited[11];

						double dbAmount = Double.parseDouble(strAmount);
						double dbCreditorControlSum = Double.parseDouble(CreditorControlSum);

						// Perform validations
						if (dbAmount == dbCreditorControlSum) {
							strValidationStatus = "PASS";
							String strSpace = "      ";

							// Print out the results
							/** INFO */
							System.out.println("Status=PASS: Creditor Transaction Record from Flex DB below");
							System.out.println("===========================================================");
							System.out.println();

							String[][] data = {
									// Headings
									{ "DAT_TXN", "               DAT_VALUE", "           COD_ACCT_NO", "TXT_TXN_DESC",
											"TXT_TXN_USER_DESC", "REF_CHQ_NO", "COD_DRCR", "COD_CC_BRN_TXN", "AMT_TXN",
											"COD_TXN_CCY", "REF_TXN_NO", "								REF_USR_NO", "						Status" },
									{ "===================", "   ===================", " ===========", "============",
											"=================", "==========", "========", "==============", "=======",
											"===========", "==========", "								========================", "	 	======" } };

							// Loop through all rows
							for (String[] row : data)
								System.out.println(Arrays.toString(row));

							System.out.println(strTxn_Date + ",	" + strValue_Data + ", " + strCreditorAccountNumber
									+ ",   " + strDescription + ",   	   " + strUser_Description + ",  	 	"
									+ strCheque_No + ",  		" + strDr_Cr + ",  		" + strOrig_Brn + ",       "
									+ strAmount + ", 		" + strTxnCcy + ",  " + strRef_Txn_No + ", "
									+ strCurrentEndToEndId + ", " + strValidationStatus);
							System.out.println();

						} else {
							strValidationStatus = "FAIL";

							// Print out the results
							/** INFO */
							System.out.println("Status=FAILED: Creditor Transaction Amount is not equal to expected,see transaction from Flex DB below");
							System.out.println("===========================================================");
							System.out.println();
							
							String[][] data = {
									// Headings
									{ "DAT_TXN", "               DAT_VALUE", "           COD_ACCT_NO", "TXT_TXN_DESC",
											"TXT_TXN_USER_DESC", "REF_CHQ_NO", "COD_DRCR", "COD_CC_BRN_TXN", "AMT_TXN",
											"COD_TXN_CCY", "REF_TXN_NO", "REF_USR_NO", "Status" },
									{ "===================", "   ===================", " ===========", "============",
											"=================", "==========", "========", "==============", "=======",
											"===========", "==========", "==========", "======" } };

							// Loop through all rows
							for (String[] row : data)
								System.out.println(Arrays.toString(row));

							System.out.println(strTxn_Date + ",	" + strValue_Data + ", " + strCreditorAccountNumber
									+ ",   " + strDescription + ",   	 " + strUser_Description + ",  	 	"
									+ strCheque_No + ",  " + strDr_Cr + ",  		" + strOrig_Brn + ",     "
									+ strAmount + ", 	" + strTxnCcy + ",  " + strRef_Txn_No + ", "
									+ strCurrentEndToEndId + ", " + strValidationStatus);
							System.out.println();

						}

						break;

					} else {
						System.out.println(
								"Warning:: No Records were Found In The Database on the 1st attempt ,the script will attempt to retry in 20 seconds");
						Thread.sleep(20000);
						i++;
					}
				} while (i <= intDatabaseWaitTimeIndex);

				/** Stop the test if there are no records in the DB after the allocated time */
				if (strCreditorTxnRecord == null & i > intDatabaseWaitTimeIndex) {
					System.out.println("No Records were Found In The Database ,The test has been stopped");
				}

			} catch (Exception e) {
				System.out.println("Status=FATAL: No Records were Found In The Database.File not created");
			}
		} catch (Exception e) {
			System.out.println("Dabase data retrieval failed :: " + e.getMessage());
		}
	}
}
