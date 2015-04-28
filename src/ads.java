import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class ads {

	static String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
	static String m_driverName = "oracle.jdbc.driver.OracleDriver";

	/*
	 * Prompts the User to enter 1 or more keywords and then using a query 
	 * to select all ads that have one of them in pdate order in groups of five
	 */
	public static String search(String userEmail,String m_userName,String m_password) {

		// TODO Auto-generated method stub

		Scanner in = new Scanner(System.in);
		Connection m_con;
		int pagecount = 1;

		Statement stmt;

		System.out.print("Enter keyword(s)(seperate with commas):");		    
		String keys = in.nextLine();
		//split the keywords based on the " , "
		String Answer[] = keys.split(",");
		
		try
		{

			Class<?> drvClass = Class.forName(m_driverName);
			DriverManager.registerDriver((Driver)
					drvClass.newInstance());

		} catch(Exception e)
		{

			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			System.exit(0);

		}
		//concats part of the sql command so it an cover all keywords user asked
		String keyword_q = "WHERE lower(title) like '%" + Answer[0].toLowerCase() + "%' OR lower(descr) like '%" +Answer[0].toLowerCase() + "%' ";

		for(int i = 1;i<Answer.length;i++){
			keyword_q = keyword_q + "OR lower(title) like '%" + Answer[i].toLowerCase() + "%' OR lower(descr) like '%" +Answer[i].toLowerCase() + "%' ";
		}

		boolean continuing = false;

		while(continuing == false){
			String[] aids = new String[5];
			int aidcount = 0;
			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();
				
				String record = "SELECT atype,title,price,pdate,aid FROM (" +
						"SELECT  atype,title,price,pdate,aid,CEIL((row_number() over (ORDER BY pdate DESC))/5) as pageno " +
						"FROM ads " +
						keyword_q + //keywords
						"ORDER BY pdate DESC ) WHERE pageno =" + pagecount;

				ResultSet result = stmt.executeQuery(record);

				System.out.println("------------------------");
				while(result.next()){
					Date OurDate = result.getDate(4);
					int price = result.getInt(3);
					String title = result.getString(2);
					String type = result.getString(1);
					String aid = result.getString(5);
					System.out.println( type + "|"+ title + "|"+ price + "|" + OurDate);
					//store aid of ads revealed
					aids[aidcount] = aid;
					aidcount++;

				}
				System.out.println("------------------------");

				boolean menu = true;
				while(menu){
					int answer = 0;

					System.out.println("(1)Show Additional information of Review 1");		    
					System.out.println("(2)Show Additional information of Review 2");
					System.out.println("(3)Show Additional information of Review 3");
					System.out.println("(4)Show Additional information of Review 4");
					System.out.println("(5)Show Additional information of Review 5");
					System.out.println("(6)Show the next five reviews");
					System.out.println("(7)Continue");

					while(answer > 7 || answer < 1){
						System.out.print("Option:");
						try{
							answer = in.nextInt();
						}
						catch(NoSuchElementException e){
							System.err.println("Not an Apporpirate Option!");
							System.exit(0);
						}
					}
					//select more info based off input
					switch (answer){
					case 1:
						info(aids[0],m_userName,m_password);
						break;
					case 2:
						info(aids[1],m_userName,m_password);
						break;
					case 3:
						info(aids[2],m_userName,m_password);
						break;
					case 4:
						info(aids[3],m_userName,m_password);
						break;
					case 5:
						info(aids[4],m_userName,m_password);
						break;
					case 6:
						menu = false;
						pagecount++;
						break;
					case 7:
						menu = false;
						continuing = true;
						break;
					}
				}
				stmt.close();
				m_con.close();

			} catch(SQLException ex) {

				System.err.println("SQLException: " +
						ex.getMessage());

			}
		}
		return userEmail;
	}
	/*
	 * Reveal additional information about the selected ad including:
	 * location
	 * description
	 * category
	 * poster name and email
	 * poster avg rating
	 */
	private static void info(String aid,String m_userName,String m_password) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		Connection m_con;
		Statement stmt;

		try
		{

			Class<?> drvClass = Class.forName(m_driverName);
			DriverManager.registerDriver((Driver)
					drvClass.newInstance());

		} catch(Exception e)
		{

			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());

		}

		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();
			//query for additional information about the selected ad
			String record = "SELECT  descr,location,cat,poster,name,AVG(rating) as avgrat " +
					"FROM ads,reviews,users " +
					"WHERE aid ='" +aid+ "' " +
					"AND poster = reviewee AND reviewee = email " +
					"GROUP BY (descr,location,cat,poster,name)"; 


			ResultSet result = stmt.executeQuery(record);
			String descr = "";
			String location = "";
			String cat = "";
			String name = "";
			String email = "";
			float avg = 0;

			while(result.next()){
				descr = result.getString(1);
				location = result.getString(2);
				cat = result.getString(3);
				name = result.getString(5);
				email = result.getString(4);
				avg = result.getFloat(6);


			}

			boolean menu = true;
			while(menu){
				int answer = 0;

				System.out.println("------------------------");
				System.out.println("(1)Description");		    
				System.out.println("(2)Location");
				System.out.println("(3)ad Category");
				System.out.println("(4)Poster Name & email:");
				System.out.println("(5)Poster Average Rating");
				System.out.println("(6)Continue");
				System.out.println("------------------------");

				while(answer > 6 || answer < 1){
					System.out.print("Option:");
					try{
						answer = in.nextInt();
					}
					catch(NoSuchElementException e){
						System.err.println("Not an Apporpirate Option!");
						System.exit(0);
					}
				}

				switch (answer){
				case 1://Display Description
					System.out.println(descr);
					break;
				case 2://Display Location
					System.out.println(location);
					break;
				case 3://Display Category
					System.out.println(cat);
					break;
				case 4://Display Poster Email and name
					System.out.println(email + "|" + name);
					break;
				case 5:
					//Display Poster avg rating
					System.out.println(avg);
					break;
				case 6:
					menu = false;
					break;
				}
			}
			stmt.close();
			m_con.close();

		} catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}
	}
	/*
	 * Prompts the user for a title, an atype, description, price, location, and a category
	 * so they can submit their ad on the user they are signed up on.
	 */
	public static String post_ad(String userEmail,String m_userName,String m_password) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		Connection m_con;
		Statement stmt;

		try
		{

			Class<?> drvClass = Class.forName(m_driverName);
			DriverManager.registerDriver((Driver)
					drvClass.newInstance());

		} catch(Exception e)
		{

			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			System.exit(0);

		}
		String type = "";
		//user can only choose w or s
		while(!type.equals("W") && !type.equals("S")  ){
			System.out.print("Wanted or For-sale(W or S):");		    
			type = in.nextLine().toUpperCase();
		}
		System.out.print("Title:");		    
		String title = in.nextLine();
		if (title.length() > 20){
			title = title.substring(0, 20);
		}

		System.out.print("Price:");		    
		int price = in.nextInt();
		in.nextLine();

		System.out.print("Description:");		    
		String descr = in.nextLine();
		if (descr.length() > 20){
			descr = descr.substring(0, 40);
		}

		System.out.print("Location:");		    
		String location = in.nextLine();
		if (location.length() > 20){
			location = location.substring(0, 15);
		}

		String cat = null;
		boolean valid = false;
		while(!valid){
			System.out.print("Category:");		    
			cat = in.nextLine();
			if (cat.length() > 20){
				cat = cat.substring(0, 15);
			}
			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();
				//checks if the category exists in the data
				String record = "SELECT  count(*) " +
						"FROM categories " +
						"WHERE cat ='" +cat+ "' " ;

				ResultSet result = stmt.executeQuery(record);
				while(result.next()){
					if(result.getInt(1) == 1){
						valid = true;
						//allow use of category
					}
					else{
						System.out.println("No such Category Exist!");
					}
				}
				stmt.close();
				m_con.close();}
			catch(SQLException ex) {

				System.err.println("SQLException: " +
						ex.getMessage());
				System.exit(0);

			}	

		}

		int aid = 0;
		valid = false;

		while(!valid){
			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();
				//checks if current aid is in use
				String record = "SELECT  count(*) " +
						"FROM ads " +
						"WHERE aid ='" +aid+ "' " ;

				ResultSet result = stmt.executeQuery(record);
				while(result.next()){
					if(result.getInt(1) == 1){
						//if it is in use go to the next integer value (1,2,3,....etc,,10000)
						aid++;
					}
					else{
						valid = true;
					}
				}stmt.close();
				m_con.close();
			}
			catch(SQLException ex) {

				System.err.println("SQLException: " +
						ex.getMessage());
				System.exit(0);

			}	

		}

		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();

			String record = "INSERT INTO ads VALUES('"+String.valueOf(aid)+"','"+type+"','"+title+"'" +
					","+price+",'"+descr+"','"+location+"',sysdate,'"+cat+"','"+userEmail+"')";

			stmt.executeUpdate(record);
			System.out.println("Ad Submited!");

			stmt.close();
			m_con.close();
		}
		catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);
		}	


		return userEmail;
	}


}
