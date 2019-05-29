/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.util.Date;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import javax.naming.event.NamingExceptionEvent;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");


			String pilotTrigger = "DROP SEQUENCE IF EXISTS pilot_number_seq CASCADE; " +
			"DROP TRIGGER IF EXISTS pilot_trigger on Pilot; " +
			"CREATE SEQUENCE pilot_number_seq START WITH 250; " +
			"CREATE OR REPLACE FUNCTION func_pilot() RETURNS trigger AS " +
			"$BODY$ BEGIN " + 
			"new.id := nextval('pilot_number_seq'); " +
			"RETURN new; " +
			"END; " + 
			"$BODY$ " +
			"LANGUAGE plpgsql VOLATILE; " +
			"CREATE TRIGGER pilot_trigger BEFORE INSERT ON Pilot FOR EACH ROW EXECUTE PROCEDURE func_pilot(); ";

			String planeTrigger = "DROP SEQUENCE IF EXISTS plane_number_seq CASCADE; " +
			"DROP TRIGGER IF EXISTS plane_trigger on Plane; " +
			"CREATE SEQUENCE plane_number_seq START WITH 67; " +
			"CREATE OR REPLACE FUNCTION func_plane() RETURNS trigger AS " +
			"$BODY$ BEGIN " + 
			"new.id := nextval('plane_number_seq'); " +
			"RETURN new; " +
			"END; " + 
			"$BODY$ " +
			"LANGUAGE plpgsql VOLATILE; " +
			"CREATE TRIGGER plane_trigger BEFORE INSERT ON Plane FOR EACH ROW EXECUTE PROCEDURE func_plane(); ";

			String flightTrigger = "DROP SEQUENCE IF EXISTS flight_number_seq CASCADE; " +
			"DROP TRIGGER IF EXISTS flight_trigger on Flight; " +
			"CREATE SEQUENCE flight_number_seq START WITH 2000; " +
			"CREATE OR REPLACE FUNCTION func_flight() RETURNS trigger AS " +
			"$BODY$ BEGIN " + 
			"new.fnum := nextval('flight_number_seq'); " +
			"RETURN new; " +
			"END; " + 
			"$BODY$ " +
			"LANGUAGE plpgsql VOLATILE; " +
			"CREATE TRIGGER flight_trigger BEFORE INSERT ON Flight FOR EACH ROW EXECUTE PROCEDURE func_flight(); ";


			esql.executeUpdate(pilotTrigger);
			esql.executeUpdate(planeTrigger);
			esql.executeUpdate(flightTrigger);
		
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddPlane(DBproject esql) {//1

		try{
			Scanner Scanner = new Scanner(System.in);
			

			System.out.println("make: ");
			String make = "'" + Scanner.next() + "'";

			System.out.println("model: ");
			String model = "'" + Scanner.next() + "'";
			System.out.println("Age: ");
			int age = Scanner.nextInt();
			System.out.println("Seats: ");
			int seats = Scanner.nextInt();
			String query = "INSERT INTO Plane(make, model, age, seats) VALUES(" + make  + ", " +  model  + ", " + age + ", " + seats + ")";
			System.out.println(query);
			esql.executeUpdate(query);
			
		}catch (Exception e){
			System.err.println(e.getMessage());
		}


	}
        
	public static void AddPilot(DBproject esql) {//2
	    Scanner input = new Scanner(System.in);

	    //System.out.println("Enter pilot ID");
	    //int pilotID = input.nextInt();
	    //String clear = input.nextLine(); // remove the \n
	    System.out.println("Enter pilot full name");
	    String pilotName = "'";
	    pilotName += input.nextLine();
	    pilotName += "'";
	    System.out.println("Enter pilot nationality");
	    String pilotNationality = "'";
	    pilotNationality += input.nextLine();
	    pilotNationality += "'";
	    
	    String query = "INSERT INTO Pilot(fullname, nationality) VALUES (" + pilotName + "," + pilotNationality + ");";
	    try {
		esql.executeUpdate(query);
	    }
	    catch(Exception e) {
		System.err.println(e.getMessage());
	    }

	    
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB


		/*
			Auto-generates primary key for Table 'Flight'.
			Adds Values into Flight table and FlightInfo. 

		*/
		
		try{
			Scanner Scanner = new Scanner(System.in);
			System.out.println("Enter Plane and Pilot ID: ");
			System.out.println("Pilot id: ");
			int pilotID = Scanner.nextInt();

			System.out.println("Plane id: ");
			int planeID = Scanner.nextInt();

			System.out.println("Enter Flight information: ");
			System.out.println("cost: ");
			int costOfFlight = Scanner.nextInt();

			System.out.println("number sold: ");
			int numberOfSold = Scanner.nextInt();

			System.out.println("number of stops: ");
			int numberOfStops = Scanner.nextInt();

			System.out.println("actual departure date: (MM-dd-yyyy) ");
			String actualDepartureDate = Scanner.next();

			System.out.println("actual arrival date: (MM-dd-yyyy) ");
			String actualArrivalDate = Scanner.next();

			Date departureDate = null;
			Date arrivalDate = null;

			try {
				departureDate = new SimpleDateFormat("MM-dd-yyyy").parse(actualDepartureDate);
				arrivalDate = new SimpleDateFormat("MM-dd-yyyy").parse(actualArrivalDate);
				// System.out.println(departureDate.getClass().getName());
			} catch (Exception e){
				System.err.println(e.getMessage());
			}


			System.out.println("airport arrival: ");
			String airportArrival = "'" + Scanner.next() + "'";

			System.out.println("airport departure: ");
			String airportDeparture = "'" + Scanner.next() + "'";
			
	
			String query = "INSERT INTO Flight(cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES(" + 
			costOfFlight  + ", " +  numberOfSold  + ", " + numberOfStops + ", " + "'" +
			departureDate + "'" + ", " + "'" + arrivalDate + "'" + ", " +
			airportArrival +  ", " + airportDeparture + "); ";
			
			
			System.out.println(query);
			esql.executeUpdate(query);
			

			String getId = "SELECT Flight.fnum FROM Flight WHERE Flight.actual_departure_date = " + 
			"'" + departureDate + "'" + " AND Flight.actual_arrival_date = " + "'" + arrivalDate + "'" + "; ";
			
			System.out.println(getId);
			
			List<List<String>> record = esql.executeQueryAndReturnResult(getId);
			String s = record.get(0).get(0);
			int id = Integer.parseInt(s);
			String query0 = "INSERT INTO FlightInfo VALUES (" + 
			id + ", " + id + ", " +
			pilotID + ", " + planeID + ");";
			System.out.println(id);
			esql.executeUpdate(query0);
			

			
		}catch (Exception e){
			System.err.println(e.getMessage());
		}
	}



	public static void AddTechnician(DBproject esql) {//4
	    Scanner input = new Scanner(System.in);
	    
	    System.out.println("Enter technician ID");
	    int techID = input.nextInt();
	    System.out.println("you enter: " + techID);
	    String deleteNewline = input.nextLine();
	    System.out.println("Enter technician full name");
	    String techName = "'" + input.nextLine() + "'";

	    String query = "INSERT INTO Technician VALUES (" + Integer.toString(techID) + "," 
		+ techName + ");";
	    try {
		esql.executeUpdate(query);
	    }
	    catch(Exception e) {
		System.err.println(e.getMessage());
	    }
	}

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
	}
	
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
	    Scanner input = new Scanner(System.in);

	    System.out.println("Enter flight number");
	    String flightNum = input.nextLine();
	    System.out.println("Enter status");
	    String status = "'" + input.nextLine() + "'";

	    String query = "SELECT COUNT(r.status) FROM Reservation r WHERE r.fid = ";
	    query += flightNum + "GROUP BY r.status HAVING r.status = " + status + ";";

	    try {
		System.out.println("Number passenger: " + esql.executeQueryAndReturnResult(query).get(0).get(0));
	    }
	    catch (Exception e) {
		System.err.println(e.getMessage());
	    }
	}
}
