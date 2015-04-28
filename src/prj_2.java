import java.sql.*;
import java.util.*;

public class prj_2
{
	static String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
	static String m_driverName = "oracle.jdbc.driver.OracleDriver";


	public static String search(String UserEmail,String m_userName,String m_password)
	{
		Scanner in = new Scanner(System.in);
		Connection m_con;

		ArrayList<String> user_info = new ArrayList<String>();

		int user_num = 0;
		Statement stmt;

		int place = 0;
		boolean review_options = false;
		System.out.print("Find users by email, or search users by name:");		    
		String name = in.nextLine();
		if (name.length() > 20){
			name = name.substring(0, 20);
		}


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

		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();


			String name_query = "SELECT name, email, count(*), avg(rating) " +
					"FROM users, ads, reviews " +
					"WHERE (users.email = '" +name+ "' OR upper(users.name) like upper('%" + name + "%') ) " +
					"AND users.email = ads.poster AND users.email = reviews.reviewee " +
					"GROUP BY (users.name, users.email)";

			ResultSet result = stmt.executeQuery(name_query);
			String nam = "";
			String email = "";


			int counter = 0;
			System.out.println("------------------------");
			while(result.next()){
				nam = result.getString(1);
				email = result.getString(2);


				user_info.add(email);


				System.out.println("(" + counter + ")" + nam + "email: "+ email);
				counter ++;
			}
			System.out.println("------------------------");

			boolean menu = true;
			while(menu){
				int answer = -1;
				while(user_num<user_info.size()){
					System.out.println("(" + user_num + ")Show Additional information of User");		    
					user_num ++;}



				if(review_options){
					System.out.println("(" + user_num + ")Read selected users reviews");
					System.out.println("(" + (user_num+1) + ")Write a review for selected user");
					user_num = user_num + 2;}

				System.out.println("(" + user_num + ")Continue");


				while(answer > user_num || answer < 0){
					System.out.print("Option:");
					try{
						answer = in.nextInt();
					}
					catch(NoSuchElementException e){
						System.err.println("Not an Apporpirate Option!");
						System.exit(0);
					}
				}
				if (answer == user_num){
					menu = false;}
				else if((answer == (user_num-2)) && (review_options == true)){
					read_reviews((String) user_info.get(place),m_userName,m_password);
				}
				else if((answer == (user_num-1)) && (review_options == true)){



					write_review(user_info.get(place), UserEmail,m_userName,m_password);
				}

				else {
					review_options = true;
					choose((String) user_info.get(answer),m_userName,m_password);
					place = answer;
					user_num = 0;}

			}

			stmt.close();
			m_con.close();
		}

		catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}


		return UserEmail;
	}


	private static void write_review(String email, String user,String m_userName,String m_password)

	{


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

		System.out.print("Your review for this user (Max 80 characters):");		    
		String review = in.nextLine();
		if (review.length() > 20){
			review = review.substring(0, 80);
		}

		int rating = 6;
		while( rating >5 || rating < 0){
			System.out.print("Rating for this user (between 1 and 5):");		    
			rating = in.nextInt(); }

		int rno = 0;
		boolean valid = false;
		while(!valid){
			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();


				String find_review_num = "SELECT count(*) FROM reviews WHERE rno =" + rno;

				ResultSet result = stmt.executeQuery(find_review_num);

				while(result.next()){
					if(result.getInt(1) == 0){
						valid = true;
					}
					else{
						rno ++;
					}
				}

				stmt.close();
				m_con.close();;
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

			String review_insert = "insert into reviews values (" + rno + "," + rating + ",'" + review + "', '" +user+ "', '" + email + "', SYSDATE)";
			stmt.executeUpdate(review_insert);
			System.out.println("Review Submited!");
			stmt.close();
			m_con.close();
		}

		catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}
	}


	private static void read_reviews(String email,String m_userName,String m_password)
	{
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
		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();

			String query_revs = "SELECT text FROM reviews WHERE reviewee = '" + email + "' ORDER BY rdate DESC";

			ResultSet result = stmt.executeQuery(query_revs);
			System.out.println("-------------------------------");
			while(result.next()){
				String current = result.getString(1);
				System.out.println(current);
			}
			System.out.println("-------------------------------");

			stmt.close();
			m_con.close();

		}catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);
		}}

	private static void choose(String email,String m_userName,String m_password)
	{
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

		try
		{

			m_con = DriverManager.getConnection(m_url, m_userName, m_password);

			stmt = m_con.createStatement();

			String user_query = "SELECT  name, email, count(*), avg(rating) " +
					"FROM users, ads, reviews " +
					"WHERE users.email = '" + email + "'" +
					"AND users.email = ads.poster AND users.email = reviews.reviewee " +
					"GROUP BY (users.name, users.email)"; 

			ResultSet result = stmt.executeQuery(user_query);
			String name_ = "";
			String email_ = "";
			int addno_ = 0;
			float average_rating = 0;

			while(result.next()){
				name_ = result.getString(1);
				email_ = result.getString(2);
				addno_ = result.getInt(3);
				average_rating = result.getFloat(4);}

			String user_info = "name: " + name_ + "email: " + email_ + "number of adds: " + addno_ + "	average rating: " + average_rating;
			System.out.println(user_info);
			// TODO Auto-generated method stub

			stmt.close();
			m_con.close();
		}
		catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}



	}

}
