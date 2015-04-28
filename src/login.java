import java.sql.*;
import java.sql.Date;
import java.util.*;
/*
 * All functions that involve any administrative duties are contained here
 * These involves: logging in, logging out, and creating new users.
 */
public class login {

	/**
	 * @param args
	 */
	static String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";

	/*
	 * menu function is called from ujiji.java to display a menu to choose either
	 * to quit the application, login as a registered user, or to signup based of the
	 * Index keys that are displayed. Depending on the answer the menu function also links to other
	 * functions.
	 */
	public static String menu(String Email,String m_userName,String m_password) {
		// TODO Auto-generated method stub
		String m_driverName = "oracle.jdbc.driver.OracleDriver";

		Scanner in = new Scanner(System.in);
		int Answer = 0;

		System.out.println("(1)Login As Registered User");		    
		System.out.println("(2)Login As Unregistered User And Signup");
		System.out.println("(3)Quit");

		//Cannot choose another key outside of 1 to 3
		while(Answer > 3 || Answer < 1){
			System.out.print("Option:");
			try{
				Answer = in.nextInt();
			}
			catch(NoSuchElementException e){
				System.err.println("Not an Apporpirate Option!");
				System.exit(0);
			}
		}
		//Start drivermanager
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

		switch (Answer){
		case 1:
			//Enter login info
			Email = enter(Email,m_userName,m_password);
			break;
		case 2:
			//Register login info
			Email = register(Email,m_userName,m_password);
			break;
		case 3:
			//Quit Application
			System.out.println("BYE BYE");
			System.exit(0);
			break;
		}
		return Email;

	}

	/*
	 * User is prompt to enter information to create an account using their email,name, and password
	 * and using jdbc inserting that info to the users table also if any strings are larger than domain,
	 *  we substring the string so it fits the proper domain
	 */
	private static String register(String UserEmail,String m_userName,String m_password){
		Connection m_con;

		Statement stmt;

		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();
			Scanner in = new Scanner(System.in);
			//name
			System.out.print("Name(MAX:20chr):");
			String name = in.nextLine();
			if (name.length() > 20){
				name = name.substring(0, 20);
			}

			//email
			System.out.print("Email(MAX:20chr):");
			String email = in.nextLine();
			if (email.length() > 20){
				email = email.substring(0, 20);
			}

			//password
			System.out.print("Password(MAX:4chr):");
			String pass = in.nextLine();
			if (pass.length() > 4){
				pass = pass.substring(0, 4);
			}

			String record = "insert into users values('"+email+"','"+name+"','"+pass+"',NULL)";

			stmt.executeUpdate(record);
			System.out.println("Created User " + name);
			UserEmail = email;

			stmt.close();
			m_con.close();
			return UserEmail;

		} catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}
		return null;
	}

	/*
	 * The user enters their name and password and we do a check if there is a user
	 * that fits that condition in the table. If there is one we return that email for future use
	 */
	private static String enter(String UserEmail,String m_userName,String m_password){
		Connection m_con;

		Statement stmt ;

		boolean success = false;
		int count = 0;
		String name = null;

		//User cannot leave this view until the user provides correct information
		while(success == false){

			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();
				Scanner in = new Scanner(System.in);
				//prompt user for email 
				System.out.print("Email:");
				String l_mail = in.nextLine();
				if (l_mail.length() > 20){
					l_mail = l_mail.substring(0, 20);
				}

				//prompt user for password 
				System.out.print("Password:");
				String pass = in.nextLine();
				if (pass.length() > 4){
					pass = pass.substring(0, 4);
				}

				String record = "SELECT count(*) as count,name,email " +
						"FROM users " +
						"WHERE pass='"+pass+ "' AND email='" + l_mail+
						"' GROUP BY (email,name)";


				ResultSet result = stmt.executeQuery(record);

				while(result.next()){
					count = result.getInt(1);

					name = result.getString(2);
					UserEmail = result.getString(3);

				}
				//there is a user with that matching info
				if(count == 1){
					success = true;
					System.out.println("Welcome:" + name);
					stmt.close();
					m_con.close();

					review(UserEmail,m_userName,m_password);
					return UserEmail;
				}
				//there is NO user with that matching info
				else{
					System.out.println("Not valid email/password");
					UserEmail = null;
					stmt.close();
					m_con.close();
					return UserEmail;
				}


			} catch(SQLException ex) {

				System.err.println("SQLException: " +
						ex.getMessage());
				System.exit(0);

			}
		}
		return null;

	}
	/*
	 * When user successfully logins in the user will be prompt by the first 3 reviews that have
	 * been created since their last login and will only display five at a time with the pagecount counter
	 * we implement. User also has the ability to see the full text of that review.
	 */
	private static void review(String UserEmail,String m_userName,String m_password) {
		// TODO Auto-generated method stub
		System.out.println("New reviews:...");

		Connection m_con;
		int pagecount = 1;

		boolean continuing = false;
		//loop so we can recreate query with different pageno w/o exiting
		while(continuing == false){
			String[] fulltext = new String[3];
			int textcount = 0;
			Statement stmt;
			String record = "SELECT rdate,rating,text FROM (" +
					"SELECT  rdate,rating,text,CEIL((row_number() over (ORDER BY rdate DESC))/3) as pageno " +
					"FROM users,reviews " +
					"WHERE reviewee = '" + UserEmail + "'" +
					"AND email = reviewee " +
					"AND rdate >= last_login " +
					"ORDER BY rdate DESC) WHERE pageno =" + pagecount;

			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();


				ResultSet result = stmt.executeQuery(record);


				while(result.next()){
					Date OurDate = result.getDate(1);
					int rate = result.getInt(2);
					String text = result.getString(3);
					//saving the original text for future use
					fulltext[textcount] = text;
					textcount++;
					//restrict to 40 characters
					System.out.println( OurDate + "|"+ rate + "|"+ text.substring(0, 40));

				}

				stmt.close();
				m_con.close();

			} catch(SQLException ex) {

				System.err.println("SQLException: " +
						ex.getMessage());
				System.exit(0);

			}
			boolean menu = true;
			while(menu){
				int Answer = 0;

				Scanner in = new Scanner(System.in);

				System.out.println("(1)Show Full text of Review 1");		    
				System.out.println("(2)Show Full text of Review 2");
				System.out.println("(3)Show Full text of Review 3");
				System.out.println("(4)Show the next three reviews");
				System.out.println("(5)Continue");

				while(Answer > 5 || Answer < 1){
					System.out.print("Option:");
					try{
						Answer = in.nextInt();
					}
					catch(NoSuchElementException e){
						System.err.println("Not an Apporpirate Option!");
						System.exit(0);
					}
				}

				switch (Answer){
				case 1:
					//reveal full text of review 1
					System.out.println(fulltext[0]);
					break;
				case 2:
					//reveal full text of review 2
					System.out.println(fulltext[1]);
					break;
				case 3:
					//reveal full text of review 3
					System.out.println(fulltext[2]);
					break;
				case 4:
					//Goto next page of reviews
					menu = false;
					pagecount++;
					break;
				case 5:
					//leave menu
					menu = false;
					continuing = true;
					break;
				}
			}
		}
	}

	/*
	 * Logs off the user and update his last login date
	 * Always returns null so that the application goes back to the login screen
	 */
	public static String logoff(String UserEmail,String m_userName,String m_password){
		System.out.println("Logging Off...");

		Connection m_con;

		Statement stmt;
		String record = "UPDATE users " +
				"SET last_login = sysdate " +
				"WHERE email = '" + UserEmail + "'";

		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();
			//Updates last login time
			stmt.executeUpdate(record);

			stmt.close();
			m_con.close();

		} catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}
		return null;
	}
}
