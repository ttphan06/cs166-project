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
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;


//import jdk.jfr.Experimental;

//import sun.jvm.hotspot.tools.StackTrace;
import java.awt.*;  
import java.awt.event.*;
import javax.naming.event.NamingExceptionEvent;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject {
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

			// HERE
			JFrame f = new JFrame("Airline Application"); 
			String[] test = {"1. Add Plane", "2. Add Pilot", "3. Add Flight", "4. Add Technician", "5. Book Flight",
			 "6. List number of available seats for a given flight",
			  "7. List total number of repairs per plane in descending order", "8. List total number of repairs per year in ascending order",
			   "9. Find total number of passengers with a given status"};
			
			JComboBox cb = new JComboBox();
				
		
			for (int i =0; i < test.length; i++){
				cb.addItem(test[i]);
			}
	 		
			      
			JLabel label1 = new JLabel("MAIN MENU", SwingConstants.CENTER);
			label1.setFont(new Font("sans-serif", Font.BOLD, 18));
			label1.setBounds(30,10,140,40);
			f.add(label1);
			
			f.getContentPane().setLayout(null);
			cb.setBounds(30, 55, 150, 30);
			final DBproject esql2 = esql;
			

			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					//
					// Get the source of the component, which is our combo
					// box.
					//
					JComboBox cb = (JComboBox) event.getSource();
	
					Object selected = cb.getSelectedItem();
					if(selected.toString().equals("1. Add Plane")){
						AddPlane(esql2);
					}
				
					else if(selected.toString().equals("2. Add Pilot")){
						AddPilot(esql2); 

					}
					else if(selected.toString().equals("3. Add Flight")){
						AddFlight(esql2); 

					}
					else if(selected.toString().equals("4. Add Technician")){
						AddTechnician(esql2); 

					}
					else if(selected.toString().equals("5. Book Flight")){
						BookFlight(esql2); 

					}
					else if(selected.toString().equals("6. List number of available seats for a given flight")){
						ListNumberOfAvailableSeats(esql2); 

					}
					else if(selected.toString().equals("7. List total number of repairs per plane in descending order")){
						ListsTotalNumberOfRepairsPerPlane(esql2); 
					}
					else if(selected.toString().equals("8. List total number of repairs per year in ascending order")){
						ListTotalNumberOfRepairsPerYear(esql2); 
	
					}
					else if(selected.toString().equals("9. Find total number of passengers with a given status")){
						FindPassengersCountWithStatus(esql2); 
					}
				}
			});


			JButton exitButton = new JButton("Exit");
			exitButton.setOpaque(true);
			exitButton.setForeground(Color.red);
			
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.print("Disconnecting from database...");
					esql2.cleanup ();
					System.out.println("Done\n\nBye !");
					f.dispose();
				}
			});
			exitButton.setBounds(70, 110, 60, 50);


			f.add(cb);
			f.add(exitButton);
			
			f.setVisible(true);   
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(200,200);
			f.setVisible(true);
			f.setResizable(false);
			f.setLocationRelativeTo(null);
			
			
			
		}catch(Exception e){
			System.err.println (e.getMessage ());
		
		}/*finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}*/
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
		
		String make, model, query;
		int age, seats;

		JTextField f1 = new JTextField();
		JTextField f2 = new JTextField();
		JTextField f3 = new JTextField();
		JTextField f4 = new JTextField();
		

		Object[] fields = {" ", "Make", f1, "Model", f2, "Age", f3, "Seats", f4};
	
		JOptionPane.showConfirmDialog(null, fields, "About Plane ..", JOptionPane.OK_CANCEL_OPTION);

		make = "'" + f1.getText() + "'";
		model = "'" + f2.getText() + "'";
		age = Integer.parseInt(f3.getText().toString());
		seats = Integer.parseInt(f4.getText().toString());
		
		query = "INSERT INTO Plane(make, model, age, seats) VALUES(" + 
			make  + ", " +  model  + ", " + age + ", " + seats + ")";

		try {
			esql.executeUpdate(query);
			JOptionPane.showMessageDialog(null, 
				"Plane Added !", "Message",
				JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
		System.err.println(e.getMessage());
	    }


	}
        
	public static void AddPilot(DBproject esql) {//2
	  
		JTextField f1 = new JTextField();
		JTextField f2 = new JTextField();

		

		Object[] fields = {" ", "Name", f1, "Nationality", f2};
	
		JOptionPane.showConfirmDialog(null, fields, "About Pilot ..", JOptionPane.OK_CANCEL_OPTION);

		String pilotName = "'" + f1.getText() + "'";
		String pilotNationality = "'" + f2.getText() + "'";

		String query = "INSERT INTO Pilot(fullname, nationality) VALUES (" + pilotName + "," + pilotNationality + ");";

		try {
			esql.executeUpdate(query);
			JOptionPane.showMessageDialog(null, 
				"Pilot Added", "Message",
				JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
		System.err.println(e.getMessage());
	    }

	    
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB

		Date departureDate = null, arrivalDate = null;
		int pilotId, planeId, costOfFlight, numberOfSold, numberOfStops;
		String actualDepartureDate, actualArrivalDate, airportArrival, airportDeparture, getId, query;
		


		JTextField f1 = new JTextField();
		JTextField f2 = new JTextField();
		JTextField f3 = new JTextField();
		JTextField f4 = new JTextField();
		JTextField f5 = new JTextField();
		JTextField f6 = new JTextField();
		JTextField f7 = new JTextField();
		JTextField f8 = new JTextField();
		

		Object[] fields = {" ", "Cost", f1, "Number Sold", f2, "Number of Stops", f3, "Actual Departure Date (MM-dd-yyyy)", f4, 
		"Actual Arrival Date (MM-dd-yyyy)", f5, "Airport Arrival", f6, "Airport Departure", f7};
	
		
		try{
			while(true){
				try {
					JOptionPane.showConfirmDialog(null, fields, "About Flight ..", JOptionPane.OK_CANCEL_OPTION);
					actualDepartureDate = f4.getText().toString();
					actualArrivalDate = f5.getText().toString();
		
					departureDate = new SimpleDateFormat("MM-dd-yyyy").parse(actualDepartureDate);
					arrivalDate = new SimpleDateFormat("MM-dd-yyyy").parse(actualArrivalDate);
					break;
					// System.out.println(departureDate.getClass().getName());
				} catch (Exception e){
					
					JOptionPane.showMessageDialog(null, "date error");
					
				} 
		
			}

			costOfFlight = Integer.parseInt(f1.getText().toString());
			numberOfSold = Integer.parseInt(f2.getText().toString());
			numberOfStops = Integer.parseInt(f3.getText().toString());
		
			airportArrival = "'" + f6.getText() + "'";
			airportDeparture = "'" + f7.getText() + "'";
			
			query = "INSERT INTO Flight(cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES(" + 
				costOfFlight  + ", " +  numberOfSold  + ", " + numberOfStops + ", " + "'" +
				departureDate + "'" + ", " + "'" + arrivalDate + "'" + ", " +
				airportArrival +  ", " + airportDeparture + "); ";
			
			


			try {
				esql.executeUpdate(query);
				JOptionPane.showMessageDialog(null, 
					"Plane Added", "Message",
					JOptionPane.INFORMATION_MESSAGE);
			}
			catch (Exception e) {
			System.err.println(e.getMessage());
			}

			JTextField ff1 = new JTextField();
			JTextField ff2 = new JTextField();
		
			Object[] fields1 = {" ", "Pilot ID", ff1, "Plane ID", ff2};
		
			JOptionPane.showConfirmDialog(null, fields1, " .. ", JOptionPane.OK_CANCEL_OPTION);

			pilotId = Integer.parseInt(ff1.getText().toString());
			planeId = Integer.parseInt(ff2.getText().toString());

			getId = "SELECT Flight.fnum FROM Flight WHERE Flight.actual_departure_date = " + 
			"'" + departureDate + "'" + 
			" AND Flight.actual_arrival_date = " + 
			"'" + arrivalDate + "'" + "; ";
			
			try{
			
				List<List<String>> record = esql.executeQueryAndReturnResult(getId);
				String s = record.get(0).get(0);
				int id = Integer.parseInt(s);

				String query0 = "INSERT INTO FlightInfo VALUES (" + 
				id + ", " + id + ", " +
				pilotId + ", " + planeId + ");";

				esql.executeUpdate(query0);
			
			}catch (Exception e){
				JOptionPane.showMessageDialog(null, "error");
			}
		
	}catch(Exception e){
		JOptionPane.showMessageDialog(null, "canceled");
	}
}
	



	public static int AddCustomer(DBproject esql){ //Add Customer

		
		String fname, lname, address, phone, zipcode, query, query2, birthDate;
		char gtype;
		Date dob = null;
		int cid = 0;

	
		try {

			System.out.println("First name: ");
			fname = "'" + in.readLine() + "'";

			System.out.println("Last name: ");
			lname = "'" + in.readLine() + "'";

			System.out.println("Gender: (M, F) ");
			gtype = in.readLine().charAt(0);

	
			do{
				try {
					System.out.println("Date of birth: (MM-dd-yyyy)");
					birthDate = in.readLine();
					dob = new SimpleDateFormat("MM-dd-yyyy").parse(birthDate);	
					break;
				} catch (Exception e){
					System.err.println(e.getMessage());
					System.out.println("Enter correct date ");
				}
			} while(true);


			System.out.println("Address: ");
			address = "'" + in.readLine() + "'";
			
			System.out.println("phone: ");
			phone = "'" + in.readLine() + "'";

			System.out.println("zipcode: ");
			zipcode = "'" + in.readLine() + "'";

			query = "INSERT INTO Customer(fname, lname, gtype, dob, address, phone, zipcode) VALUES(" +
			fname + ", " + lname + ", " + "'" + gtype + "', " + "'" + dob + "', " + address + ", " +
			phone + ", " + zipcode + ");";  
			
			esql.executeUpdate(query);

			query2 = "SELECT Customer.id FROM Customer WHERE fname = " + fname + " AND lname = " + lname + 
			"AND dob = '" + dob + "';";


			List<List<String>> customerId = esql.executeQueryAndReturnResult(query2);
			
			String s = customerId.get(0).get(0);
			cid = Integer.parseInt(s);

		} catch(Exception e){
			System.err.println(e.getMessage());
		} finally {
			System.out.println("*Customer created*"); 
			System.out.println("Book a flight ");
			System.out.println("------------------");
		}

		return cid;

	}



	public static void AddTechnician(DBproject esql) {//4
	    
	
		JTextField f1 = new JTextField();
	

		Object[] fields = {" ", "Name", f1};
	
		JOptionPane.showConfirmDialog(null, fields, "About Technician ..", JOptionPane.OK_CANCEL_OPTION);

		String techName = "'" + f1.getText().toString() + "'";
		
		String query = "INSERT INTO Technician(full_name) VALUES (" 
		+ techName + ");";

		try {
			esql.executeUpdate(query);
			JOptionPane.showMessageDialog(null, 
				"Technician Added", "Message",
				JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
		System.err.println(e.getMessage());
	    }



	}

	public static void BookFlight(DBproject esql) {//5
		
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB

		
		int fnum, customerId = 0;
		String query; 
		char status;
		
		/*
		System.out.println("OPTIONS : ");
		System.out.println(" --------- ");
		System.out.println("1. Enter Customer ID ");
		System.out.println("2. Create New Customer ");
		*/
		JTextField f1 = new JTextField();
		JButton yesButton = new JButton("Yes ");
		JButton noButton = new JButton("No ");
		Object[] fields = {" ", "Create new customer ? ", yesButton, noButton};
	
	

		final DBproject esql2 = esql;
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddCustomer(esql2);
			}
		});
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.getRootFrame().dispose();   
				int fnum, customerId = 0;
				String query; 
				char status;

				JTextField f1 = new JTextField();
				JTextField f2 = new JTextField();
				JTextField f3 = new JTextField();
				JTextField f4 = new JTextField();
				
				try{
				
					Object[] fields = {" ", "Flight #", f1, "Customer ID", f2};
				
					JOptionPane.showConfirmDialog(null, fields, "About Plane ..", JOptionPane.OK_CANCEL_OPTION);

					fnum = Integer.parseInt(f1.getText().toString());
					customerId = Integer.parseInt(f2.getText().toString());
					
					query = "SELECT p.seats - f.num_sold FROM ((FlightInfo i INNER JOIN Flight f ON i.flight_id = f.fnum) " +  
						"INNER JOIN Plane p ON p.id = i.plane_id) WHERE f.fnum = " + fnum + ";";
						List<List<String>> availableSeats = esql.executeQueryAndReturnResult(query);
						String s = availableSeats.get(0).get(0);
						int available = Integer.parseInt(s);
			
						if (available > 0){
							status = 'C';
							String query3 = "UPDATE Flight SET num_sold = num_sold + 1 WHERE fnum = " + fnum + "; ";
							esql.executeUpdate(query3);
						} else{
							status = 'W';
						}

						String query2 = "INSERT into Reservation(cid, fid, status) VALUES(" +
						customerId + ", " + fnum + ", " + "'" + status + "');";
					
						
						esql.executeUpdate(query2);
						JOptionPane.showMessageDialog(null, 
							"Booked Flight", "Message",
							JOptionPane.INFORMATION_MESSAGE);

			}
		catch(Exception ex){
			System.err.println(ex.getMessage());
		}
		}
	});

		JOptionPane.showConfirmDialog(null, fields, "Book Flight", JOptionPane.OK_CANCEL_OPTION);
		
		
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
	    String fNumber = JOptionPane.showInputDialog("Enter flight number");
	    System.out.println("Enter departure date");
	    String dDate = "'" + JOptionPane.showInputDialog("Enter departure date (YYYY-MM-DD)") + "'";
	    String query = "SELECT p.seats - f.num_sold "
		+ "FROM ((flightinfo i INNER JOIN flight f ON i.flight_id = f.fnum) "
		+ "INNER JOIN plane p ON p.id = i.plane_id) "
		+ "WHERE f.fnum = " + fNumber + " AND f.actual_departure_date = " + dDate +";";

	    try {
			JOptionPane.showMessageDialog(null, "Available seats: " 
				+ esql.executeQueryAndReturnResult(query).get(0).get(0),
				"Number of available seats", JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
			System.err.println(e.getMessage());
	    }
	}


	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
		String query = "SELECT r.plane_id, count(r.plane_id)" + 
		"from Repairs r " +
		"group by r.plane_id " + 
		"order by count(r.plane_id) desc; ";

	    try {
			List<List<String>> res = esql.executeQueryAndReturnResult(query);
			String m = "Plane\tnumber of repairs\n";
			for (int i = 0; i < res.size(); ++i) {
				for (int j = 0; j < res.get(i).size(); ++j) {
					m += res.get(i).get(j) + "\t";
				}
				m += "\n";
			}
			JTextArea textArea = new JTextArea(m);
			JScrollPane scrollPane = new JScrollPane(textArea);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			scrollPane.setPreferredSize(new Dimension(300, 400));
			JOptionPane.showMessageDialog (null, scrollPane, "Repair List", JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
	    }
	}


	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order

		String query = "SELECT EXTRACT(YEAR FROM r.repair_date), COUNT(EXTRACT(YEAR FROM r.repair_date)) "
		+ "FROM Repairs r "
		+ "GROUP BY EXTRACT(YEAR FROM r.repair_date) "
		+ "ORDER BY COUNT(EXTRACT(YEAR FROM r.repair_date));";


	    try {
			List<List<String>> res = esql.executeQueryAndReturnResult(query);
			String m = "Year\tRepair\n";
			for (int i = 0; i < res.size(); ++i) {
				for (int j = 0; j < res.get(i).size(); ++j) {
					m += res.get(i).get(j) + "\t";
				}
				m += "\n";
			}
			JTextArea textArea = new JTextArea(m);
			JScrollPane scrollPane = new JScrollPane(textArea);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			scrollPane.setPreferredSize(new Dimension(300, 400));			
			JOptionPane.showMessageDialog (null, scrollPane, "Number of Repair per Year", JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
	    String flightNum = JOptionPane.showInputDialog("Enter flight number");
	    String status = "'" + JOptionPane.showInputDialog("Enter status") + "'";

	    String query = "SELECT COUNT(r.status) FROM Reservation r WHERE r.fid = ";
	    query += flightNum + "GROUP BY r.status HAVING r.status = " + status + ";";
		
	    try {
			JOptionPane.showMessageDialog(null, 
				"Number passenger: " + esql.executeQueryAndReturnResult(query).get(0).get(0),
				"Passenger with status",
				JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch (Exception e) {
		System.err.println(e.getMessage());
	    }
	}
}