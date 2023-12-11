
import java.util.NoSuchElementException;
import java.util.Properties;
import java.security.PublicKey;
import java.sql.*;
import java.util.Scanner;
import java.math.BigDecimal;

public class ArborDB{
    
 public static String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=schema";
 public static Connection conn = null;


    public static void main(String[]args){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException err) {
            System.err.println("Unable to detect the JDBC .jar dependency. Check that the library is correctly loaded in and try again.");
            System.exit(-1);
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Arbor DB JDBC ");


        while (true) {
            displayMenu();
            String choice = scanner.nextLine();
            int c = Integer.parseInt(choice);
            switch (c) {
                    case 1:
                        try{
                            System.out.println("Username: ");
                            String user = scanner.nextLine();
                            System.out.println("Password: ");
                            String pass = scanner.nextLine();
                             conn = connect(user, pass);
                             break;
                        } catch (SQLException e ){
                            System.out.println("Error with connecting, please retry.");
                            System.out.println(e.getMessage());

                            break;
                        }
                       
                    case 2:
                    try{
                        
                        System.out.println("please enter a forest name, area, acid level, MBR_Xmin, MBR_XMax, MBR_YMin, MBR_YMax");
                        String[] inputArray = scanner.nextLine().split(",");
                        if(inputArray.length == 6)
                        {
                            System.out.println("Error with input.");
                            System.out.println(choice);
                            break;
                        }
                        addForest(conn, inputArray);
                        

                    } catch (SQLException e ){
                        System.out.println("Error with insert.");
                        System.out.println(e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                    }
                        // Perform function 2 (example: update)
                       
                        break;
                    // Add cases for other functions or operations
                    case 3:
                    try{
                        System.out.println("Please enter genus, epithet, ideal temperature, largest height, and raunkiaer life-form");
                        String[] inputArray = scanner.nextLine().split(",");
                        if(inputArray.length != 5){
                            System.out.println("Error with input.");
                            
                            break;
                        }
                        addTreeSpecies(conn, inputArray);

                        
                    } catch (SQLException e){
                        System.out.println("Error with inset.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }
                    case 4:
                    try{
                     System.out.println("Please enter forest_no, genus, epithet");
                        String[] inputArray = scanner.nextLine().split(",");
                        if(inputArray.length != 3){
                            System.out.println("Error with input.");
                            break;
                        }
                        addSpeciesToForest( conn, inputArray);
                        break;

                        
                    } catch (SQLException e){
                        System.out.println("Error with inset.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }
                    case 5:
                    try{
                        System.out.println("Please enter SSN, first_name, last_name, middle_inital, rank, state abbreviation");
                        String[] inputArray = scanner.nextLine().split(",");
                        if(inputArray.length != 6){
                            System.out.println("Error with input.");
                            break;
                        }
                        newWorker( conn, inputArray);
                        break;

                        
                    } catch (SQLException e){
                        System.out.println("Error with inset.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }
                    case 6:
                        employWorkerToState(scanner);
                        break;
                    case 7:
                        placeSensor(scanner);
                        break;
                    case 8 :
                        generateReport(scanner);
                        break;
                    case 9:
                    try{
                        System.out.println("Please enter genus, epithet, forest_no");
                        String[] inputArray = scanner.nextLine().split(",");
                        if(inputArray.length != 3){
                            System.out.println("Error with input.");
                            break;
                        }
                        removeSpeciesFromForest( conn, inputArray);
                        break;

                        
                    } catch (SQLException e){
                        System.out.println("Error with removal.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }

                    case 10:
                    try{
                        System.out.println("Please enter SSN to delete");
                        String[] inputArray = scanner.nextLine().split(",");
                        if(inputArray.length != 1){
                            System.out.println("Error with input.");
                            break;
                        }
                        deletWorker( conn, inputArray);
                        break;
                        
                    } catch (SQLException e){
                        System.out.println("Error with removal.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }

                    case 11:
                    moveSensor(scanner);
                    break;
                    case 12:
                    removeWorkerFromState(scanner);
                    break;
                    case 13:
                    removeSensor(scanner);
                    break;
                    case 14:
                    try{
                        System.out.println("Please enter forest_no to view sensors");
                        int input = Integer.parseInt(scanner.nextLine());
                        
                        listSensors(conn, input);
                        break;

                        
                    } catch (SQLException e){
                        System.out.println("Error with insert.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }
                    case 15:
                     try{
                        System.out.println("Please enter work's ssn (maintainer_id) to view sensors");
                        String input = scanner.nextLine();
                        
                        listMaintainedSensors(conn, input);
                        break;

                        
                    } catch (SQLException e){
                        System.out.println("Error with inset.");
                        System.err.println("Message = " + e.getMessage());
                        System.err.println("SQLState = " + e.getSQLState());
                        System.err.println("SQL Code = " + e.getErrorCode());
                        break;
                    }
                    case 16:
                    locateTreeSpecies(scanner);
                    break;
                    case 17:
                    rankForestSensors(scanner);
                    break;
                    case 18:
                    habitableEnvironment(scanner);
                    break;
                    case 19:
                    topSensors(scanner);
                    break;
                    case 20:
                    threeDegrees(scanner);
                    break;
                    

                    
                    
                    
                    case 0:
                        // Exit the application
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                        break;
             }

        }
    }



    public static Connection connect(String user, String pass) throws SQLException
    {
        if( user == null || pass == null) {
            return null;
        }
        Connection ret = DriverManager.getConnection(URL, user, pass);
        
        return ret;

    }
    public static  void addForest(Connection conn, String[]inputArray) throws SQLException{
        if(conn == null){
            System.out.println("issue with connection to DB, going to connection, please reconnect.");
            return;
        }
        CallableStatement st =  conn.prepareCall("call addForest(?,?,?,?,?,?,?)");
        st.setString(1, inputArray[0]);
        st.setInt(2, Integer.parseInt(inputArray[1]));
        st.setBigDecimal(3, new BigDecimal(inputArray[2]));
        st.setBigDecimal(4, new BigDecimal(inputArray[3]));
        st.setBigDecimal(5, new BigDecimal(inputArray[4]));
        st.setBigDecimal(6, new BigDecimal(inputArray[5]));
        st.setBigDecimal(7, new BigDecimal(inputArray[6]));
        // Execute the stored procedure
         st.execute();

        // Retrieve results using getResultSet()
        ResultSet rs = st.getResultSet();
        if (rs != null && rs.next()) {
            String name = rs.getString("name");
            System.out.println("Successfully inserted forest " + name);
        }

    }

    public static void addTreeSpecies(Connection conn, String[]inputArray) throws SQLException{
        if(conn == null){
            System.out.println("issue with connection to DB, going to connection, please reconnect.");
            return;
        }

        CallableStatement st = conn.prepareCall("call addTreeSpecies(?,?,?,?,?)");
        st.setString(1, inputArray[0]);
        st.setString(2, inputArray[1]);
        st.setBigDecimal(3, new BigDecimal(inputArray[2]));
        st.setBigDecimal(4, new BigDecimal(inputArray[3]));
        st.setString(5, inputArray[4]);
        st.execute();

        

    }

    public static void addSpeciesToForest(Connection conn,String[]inputArray) throws SQLException{
         if(conn == null){
            System.out.println("issue with connection to DB, going to connection, please reconnect.");
            return;
        }

        CallableStatement st = conn.prepareCall("call addSpeciesToFores(?,?,?)");
        st.setInt(1, Integer.parseInt(inputArray[0]));
        st.setString(2, inputArray[1]);
        st.setString(3, inputArray[2]); 
        st.execute();

       
    }
    public static void newWorker(Connection conn, String[] inputArray) throws SQLException{
         if(conn == null){
            System.out.println("issue with connection to DB, going to connection, please reconnect.");
            return;
        }

        CallableStatement st = conn.prepareCall("call newWorker(?,?,?,?,?,?)");
        st.setString(1, inputArray[0]);
        st.setString(2, inputArray[1]);
        st.setString(3, inputArray[2]); 
        st.setString(4, inputArray[3]); 
        st.setString(5, inputArray[4]); 
        st.setString(6, inputArray[5]); 
        st.execute();
        /*
        ResultSet rs = st.executeQuery("SELECT * FROM WORKER WHERE ssn = "+ inputArray[1]);
        Integer ssn = rs.getInt(1);
        String name = rs.getString(2);
        System.out.println("inserted " + ssn + ","+ name +", succesfully.");
        rs.close();
        */

    }

    public static void removeSpeciesFromForest(Connection conn, String[]inputArray) throws SQLException{
         if(conn == null){
            System.out.println("issue with connection to DB, going to connection, please reconnect.");
            return;
        }

        PreparedStatement st = conn.prepareCall("call removeSpeciesFromForest(?,?,?)");
        st.setString(1, inputArray[0]);
        st.setString(2, inputArray[1]);
        st.setInt(3, Integer.parseInt(inputArray[2])); 
        
        st.execute();
        /*not sure how we should check to make sure its removed. */
        
    }

    public static void deletWorker(Connection conn, String []inputArray) throws SQLException{
        if(conn == null)
        {
            System.out.println("Error with connection, exiting.");
            return;
        }
        PreparedStatement st = conn.prepareCall("call deleteWorker(?)");
        st.setString(1, inputArray[0]);
        st.execute();

        /*Process deleteion conffimation */


    }
    //issue with sensors listing 

    public static void listSensors(Connection conn, int input) throws SQLException{
        if(conn== null){
            System.out.println("Error with connection, exiting.");
            return;
        }
        PreparedStatement st = conn.prepareStatement("SELECT * FROM listSensors(?)");
       
        st.setInt(1, input);
   

        ResultSet rs = st.executeQuery(); 
        System.out.println("Printing Sensors from forest (sensor_id, maintainer_id) "+ input);
        while(rs.next()){
            int sensorId = rs.getInt(1);
            String maintainerId = rs.getString(7);
            System.out.println(sensorId + ","+ maintainerId);
        }

    }

    public static void employWorkerToState(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try
        {
            CallableStatement call = conn.prepareCall("call employWorkerToState(?, ?)");
            System.out.print("Enter the abbreviation of state: ");
            String abb = scanner.nextLine();
            if (abb.length()!=2){
                System.out.print("Please entre a vaild abbreviation(2 characters)");
                return;
            }
            System.out.println("Enter the SSN of the workder: ");
            String ssn = scanner.nextLine();
            if(ssn == null){
                System.out.print("Please entre a vaild ssn (9 characters)");
                return;
            }
            call.setString(1, abb);
            call.setString(2,ssn);
            
            call.execute();

            System.out.println("Worker employed successfully.");
        }
        catch(SQLException e){
            System.out.println("Error with insert.");
            System.err.println("Message = " + e.getMessage());
            System.err.println("SQLState = " + e.getSQLState());
            System.err.println("SQL Code = " + e.getErrorCode());

            
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }
        
        
    }
    //case 7
    public static void placeSensor(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try
        {
            CallableStatement call = conn.prepareCall("call placeSensor(?, ?, ?, ?)");
            System.out.println("Enter latest energy of the sensor: ");
            int energy = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter its X location: ");
            float Location_x = Float.parseFloat(scanner.nextLine());
            System.out.println("Enter its Y location: ");
            float Location_y = Float.parseFloat(scanner.nextLine());
            System.out.println("Please enter a workers ssn.");
            String ssn = scanner.nextLine();
            

            call.setInt(1,energy);
            call.setFloat(2, Location_x);
            call.setFloat(3, Location_y);
            call.setString(4, ssn);
            call.execute();

            System.out.println("Sensor added successfully.");
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }

    }
    //case 8 generateReport
    public static void generateReport(Scanner scanner) throws NumberFormatException
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try{
            PreparedStatement st = conn.prepareStatement("SELECT sensor_id FROM SENSOR ORDER BY sensor_id");
            ResultSet rs = st.executeQuery();

            if(!rs.next()){
                System.out.println("No Sensors are currently deployed");
                return;
            }
            System.out.println("Sensor List: ");
            while(rs.next())
            {
                System.out.println("Sensor ID:" + rs.getInt("sensor_id"));
            }
            System.out.println("Enter a sensor id or enter -1 to exit: ");
            int sensorID = Integer.parseInt(scanner.nextLine());
            if(sensorID ==-1)
            {
                return;
            }

            CallableStatement call = conn.prepareCall("call generateReport(?, ?, ?)");

            System.out.println("Enter the report time: ");
            Timestamp reportTime = Timestamp.valueOf(scanner.nextLine());
            System.out.println("Enter the record temperature");
            float recordTemp = Float.parseFloat(scanner.nextLine());

            call.setInt(1, sensorID);
            call.setTimestamp(2, reportTime);
            call.setFloat(3, recordTemp);
            call.execute();
            
            System.out.println("Report generated successfully.");
        }
        catch(IllegalArgumentException e){
            System.out.println("Invalid time format.");
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        
    }
    //case 11 moveSensor
    public static void moveSensor(Scanner scanner) throws NumberFormatException
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try{
            PreparedStatement st = conn.prepareStatement("SELECT sensor_id FROM SENSOR ORDER BY sensor_id");
            ResultSet rs = st.executeQuery();

            if(!rs.next()){
                System.out.println("No Sensors are currently deployed");
                return;
            }

            System.out.println("Enter the sensor id you are trying to move or -1 return to meau: ");
            int sensorID = Integer.parseInt(scanner.nextLine());
            if(sensorID ==-1)
            {
                return;
            }

            CallableStatement call = conn.prepareCall("call moveSensor(?, ?, ?)");
            System.out.println("Enter the new X location");
            float nex_X = Float.parseFloat(scanner.nextLine());
            System.out.println("Enter the new Y location");
            float nex_Y = Float.parseFloat(scanner.nextLine());

            call.setInt(1, sensorID);
            call.setFloat(2, nex_X);
            call.setFloat(3, nex_Y);

            call.execute();

            System.out.println("Sensor moved successfully.");
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }
    }
    //case 12 removeWorkerFromState
    public static void removeWorkerFromState(Scanner scanner) throws NumberFormatException
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try{
            CallableStatement call = conn.prepareCall("call removeWorkerFromState(?, ?)");
            System.out.println("Enter the ssn of worker are removed");
            String ssn = scanner.nextLine();
            System.out.println("Enter the state abbreviation");
            String abb = scanner.nextLine();
            if (abb.length()!=2){
                System.out.print("Please entre a vaild abbreviation(2 characters)");
                return;
            }

            call.setString(1, ssn);
            call.setString(2, abb);
            

            call.execute();

            System.out.println("worker is moved successfully.");
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }
    }
    //case 13 removeSensor
     public static void removeSensor(Scanner scanner) throws NumberFormatException
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try{
            System.out.println("Would you like to remove all sensors or selected sensors from ArborDB? (all or selected)");
            String answer = scanner.nextLine().toLowerCase();
            CallableStatement call = conn.prepareCall("call removeSensor(?)");

            if(answer.equals("all"))
            {
                System.out.println("Are you removing all sensor? (yes or no)");
                String confirmation = scanner.nextLine().toLowerCase();

                if(confirmation.equals("yes")){
                PreparedStatement st = conn.prepareStatement("SELECT sensor_id FROM SENSOR ORDER BY sensor_id");
                ResultSet rs = st.executeQuery();
               
                while(rs.next()){
                    int sensor_id = rs.getInt("sensor_id");
                    call.setInt(1, sensor_id);
                    call.execute();
                }
                
                System.out.println("All sensors removed successfully. ");
                return;
            }

            else if(confirmation.equals("no"))
            {
                System.out.println("No Sensors were Removed");
                return;
            }
            else{
                System.out.println("Invalid choice");
                return;
            }
            }
            else if (answer.equals("selected")){
                System.out.println("Enter a sensor id or enter -1 to exit: ");
                int sensorID = Integer.parseInt(scanner.nextLine());
                if(sensorID ==-1)
            {
                return;
            }
             call.setInt(1, sensorID);
             call.execute();

            System.out.println("Sensor and its reports removed successfully.");

            }
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }
    }
    //case 15 listMaintainedSensors
     public static void listMaintainedSensors(Connection conn, String input) throws SQLException{
        if(conn== null){
            System.out.println("Error with connection, exiting.");
            return;
        }
        PreparedStatement st = conn.prepareStatement("SELECT * FROM listMaintainedSensors(?)");
        st.setString(1,input);
        ResultSet rs = st.executeQuery();
        System.out.println("Printing Sensors from worker (sensor_id, maintainer_id) "+ input);
        while(rs.next()){
            int sensorId = rs.getInt(1);
            String maintainerId = rs.getString(7);
            System.out.println(sensorId + ","+ maintainerId);
        }

    }
    //case 16 locateTreeSpecies
     public static void locateTreeSpecies(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try
        {
            PreparedStatement call = conn.prepareStatement("SELECT * FROM locateTreeSpecies(?,?)");

            System.out.println("Enter pattern alpha: ");
            String alpha = scanner.nextLine();
            System.out.println("Enter pattern beta: ");
            String beta = scanner.nextLine();
          

            call.setString(1,alpha);
            call.setString(2, beta);
           
        

            ResultSet rs = call.executeQuery();

            //display not sure.
            while(rs.next()){

            int forest_id = rs.getInt("forest_id");
            
            System.out.println(forest_id );
            rs.next();
        }
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }

    }
    //case 17 rankForestSensors
    public static void rankForestSensors(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try{
            PreparedStatement st = conn.prepareStatement("SELECT * FROM rankForestSensors()");
            ResultSet rs = st.executeQuery();
    

            if(!rs.next()){
                System.out.println("No Forests to Rank");
                return;
            }
            System.out.println("Printing Ranked Sensors");
            if(!rs.isBeforeFirst()){
                System.out.println("no Data");
            }

            while(rs.next()){
                int forest_no = rs.getInt(1);
                int rank = rs.getInt(2);
                System.out.println("forest_no: "+forest_no+", rank: "+rank);
            }
            return;
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }
    }
    //case 18 habitableEnvironment
      public static void habitableEnvironment(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try
        {
            PreparedStatement call = conn.prepareStatement("SELECT * FROM habitableenvironment(?, ?, ?)");

            
            System.out.println("Enter genus: ");
            String genus = scanner.nextLine();
            System.out.println("Enter epithet: ");
            String epithet = scanner.nextLine();
            System.out.println("Enter  integer value k: ");
            int k = Integer.parseInt(scanner.nextLine());
            
        

            call.setString(1,genus);
            call.setString(2, epithet);
            call.setInt(3, k);
            ResultSet rs = call.executeQuery();
            
             if (!rs.isBeforeFirst() ) {    
                System.out.println("No data"); 
            } 
            if(!rs.next()){
                System.out.println("No habitable environments were found");
                return;
            }
            while(rs.next())
            {
                int forest_no = rs.getInt("forest_no");
                System.out.println("Forest " + forest_no + " is habitable for "+ genus +","+ epithet);
            }
            return;

        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }

    }
    //case 19 topSensors
    public static void topSensors(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try
        {
            CallableStatement call = conn.prepareCall("SELECT * FROM topSensors(?, ?)");
          
            System.out.println("Enter number of sensors wished to be seen (k) ");
            int k = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter how many months you want to look back (x): ");
            int month = Integer.parseInt(scanner.nextLine());
            
            call.setInt(1,k);
            call.setInt(2, month);
            
            ResultSet rs = call.executeQuery();

            if (!rs.isBeforeFirst() ) {    
                System.out.println("No data"); 
            } 
            int i =1;
            while(rs.next()){
                int sensor_id = rs.getInt("sensor_id");
                System.out.println("top "+i+" sensor is "+sensor_id);
                i++;
            }
            return;
            
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }

    }
    //case 20 threeDegrees
     public static void threeDegrees(Scanner scanner)
    {
        if(conn == null){
            System.out.println("Please connect first.");
            return;
        }
        try
        {
            CallableStatement call = conn.prepareCall("{call threeDegrees(?, ?)}");
            System.out.println("Enter first forest id: ");
            int s_forest = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter second forest id: ");
            int e_forest = Integer.parseInt(scanner.nextLine());
            
            call.setInt(1,s_forest);
            call.setInt(2,e_forest);
            
            ResultSet rs = call.executeQuery();

            if(rs.next()){
                String result_path = rs.getString(1);
                System.out.println("path found: "+ result_path);
                return;
            }
            else{
                System.out.println("No path was found");
            }
            
            
        }
        catch (SQLException e )
        {
            System.out.println("SQL Error");

            while (e != null) {
                System.err.println("Message = " + e.getMessage());
                System.err.println("SQLState = " + e.getSQLState());
                System.err.println("SQL Code = " + e.getErrorCode());
                e = e.getNextException();
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Input Error: Please ensure numeric fields are entered correctly.");
        }

    }
    




    static void displayMenu() {
        System.out.println("ArborDB Menu");
        System.out.println("1. Connect");
        System.out.println("2. addForest");
        System.out.println("3.addTreeSpecies");
        System.out.println("4. addSpeciesToForest");
        System.out.println("5. newWorker");
        System.out.println("6. employWorkerToState");
        System.out.println("7. placeSensor");
        System.out.println("8. generateReport");
        System.out.println("9. removeSpeciesFromForest");
        System.out.println("10. deleteWorker");
        System.out.println("11. moveSensor");
        System.out.println("12. removeWorkerFromState");
        System.out.println("13. removeSensor");
        System.out.println("14. listSensors");
        System.out.println("15. listMaintainedSensors");
        System.out.println("16. locateTreeSpecies");
        System.out.println("17. rankForestSensors");
        System.out.println("18. HabitableEnviornment");
        System.out.println("19. topSensors");
        System.out.println("20. threeDegrees");
        System.out.println("21. Exit");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    

}


