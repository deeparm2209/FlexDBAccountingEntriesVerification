package helpers;

import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import static org.testng.Assert.fail;


public class DatabaseUtil
{
  
    public List<String> databaseHandlerForFlexDB(String strSQLquery,String strColumnName_1,String strColumnName_2,String strColumnName_3,
                                                        String strColumnName_4,String strColumnName_5, String strColumnName_6,String strColumnName_7,
                                                        String strColumnName_8,String strColumnName_9, String strColumnName_10,String strColumnName_11) throws SQLException {
        /**DATA BASE PROCESS FOR AUDIT DB*/

        //TODO : Make sure database communication works with the below settings
        String strSelectedFieldsInRecords = null;

        //Connection URL Syntax: "jdbc:mysql://ipaddress:portnumber/db_name"
        String dbUrl = "jdbc:oracle:thin:@//sadu002-uat.corp.dsarena.com:3521/TPKEFCRU.corp.dsarena.com";

        //Define what type of driver is needed(get from property file)
//        String dbDriver = "oracle.jdbc.OracleDriver";

        //Database Username
        String username ="MAKOLA_USER";

        //Database Password
        String password ="makola_1";

        //Create an array list to store the multiple records that get returned from the database
        List<String> results =new ArrayList<String>();


        //Create Connection to DB
        //System.out.println(getConfig().getDatabaseUrl2());
        Connection conn = DriverManager.getConnection(dbUrl, username, password);
        System.out.println("INFORMATION:: Database Connected successfully");

        //Alter the table to set the variables
        conn.createStatement().execute("alter session set current_schema = kefcrh");

        //Query to Execute
        String sqlQuery = strSQLquery;


        //Create Statement Object
        Statement stmt = conn.createStatement();

        //Execute the SQL Query. Store results in ResultSet
        ResultSet resultSet = stmt.executeQuery(sqlQuery);


        try
        {
            //Iterate the result set and return all the records(Multiple records returned)
            while (resultSet.next())
            {
                //Return the records per Column Name
                strSelectedFieldsInRecords =
                        (resultSet.getString(strColumnName_1) + " | " + resultSet.getString(strColumnName_2) + " | " +
                                resultSet.getString(strColumnName_3) + " | " + resultSet.getString(strColumnName_4) + " | " +
                                resultSet.getString(strColumnName_5) + " | " + resultSet.getString(strColumnName_6) + " | " +
                                resultSet.getString(strColumnName_7) + " | " + resultSet.getString(strColumnName_8) + " | " +
                                resultSet.getString(strColumnName_9) + " | " + resultSet.getString(strColumnName_10) + " | " +
                                resultSet.getString(strColumnName_11));

                //Store the records returned from the DB in the list row per row.
                results.add(strSelectedFieldsInRecords);
            }

            System.out.println("Number of records found in the database is  :"+results.size());
            System.out.println();


        }
        catch (Exception e)
        {
            System.out.println("Unable to make connection with DB" +e);
            e.printStackTrace();
            //ExtentTestManager.getTest().log(Status.FAIL, "INFORMATION:: Unable to make connection with DB" +e);
            fail("This test has been stopped!");
        }
        finally
        {
            //closing DB Connection
            conn.close();
            resultSet.close();
        }
        return results;
    }

    //*************Flex Database Handler return multiple records********************************************************


    //*************Microsoft Database Handler **************************************************************************
    //Open DB connection
    public static Connection Open(String connectionString)
    {
        //Import microsoft class
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            return DriverManager.getConnection(connectionString);
        }
        catch (Exception e)
        {

        }
        return null;
    }

    //Close DB connection
    public static  void Close()
    {
        //
    }

    //Execute DB query
    public static void ExecuteQuery(String query, Connection connection)
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
        }
        catch (Exception e)
        {

        }
    }

    public static void ExecuteStoredProc(String procedureName, Hashtable parameters, Connection connection)
    {
        try {

            int paramterLength = parameters.size();
            String paraAppender = null;
            StringBuilder builder = new StringBuilder();
            // Build the paramters list to be passed in the stored proc
            for (int i = 0; i < parameters.size(); i++) {
                builder.append("?,");
            }

            paraAppender = builder.toString();
            paraAppender = paraAppender.substring(0,
                    paraAppender.length() - 1);

            CallableStatement stmt = connection.prepareCall("{Call "
                    + procedureName + "(" + paraAppender + ")}");

            // Creates Enumeration for getting the keys for the parameters
            Enumeration params = parameters.keys();

            // Iterate in all the Elements till there is no keys
            while (params.hasMoreElements()) {
                // Get the Key from the parameters
                String paramsName = (String) params.nextElement();
                // Set Paramters name and Value
                stmt.setString(paramsName, parameters.get(paramsName)
                        .toString());
            }

            // Execute Query
            stmt.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //******************************************************************************************************************
}
