import java.sql.*;
import java.sql.Date;
import java.util.*;


public class ads_2
{
	static String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
	static String m_driverName = "oracle.jdbc.driver.OracleDriver";

	public static String List_Ads(String userEmail,String m_userName,String m_password)
	{
		Scanner in = new Scanner(System.in);
		Connection m_con;
		int pagecount = 1;
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
		boolean continuing = false;

		while(continuing == false){
			String[] aids = new String[5];
			String[] promos = new String[5];
			int aidcount = 0;
			try
			{

				m_con = DriverManager.getConnection(m_url, m_userName, m_password);

				stmt = m_con.createStatement();

				String record = "SELECT atype, title, price, pdate, aid, promote, days_left, poster " +
						"FROM (SELECT  poster, ads.atype, ads.title, ads.price, ads.pdate, ads.aid, CEIL((row_number() over (ORDER BY ads.pdate DESC))/5) as pageno, " +
						"(CASE WHEN ads.aid IN " + 
						"(SELECT ads.aid " +
						"FROM ads, purchases, offers " +
						"WHERE ads.aid = purchases.aid " +
						"AND purchases.ono = offers.ono " +
						"AND (purchases.start_date + offers.ndays) > SYSDATE) " +
						"THEN 'Y' ELSE 'N' END) promote,days_left " +
						"FROM ads " +
						"LEFT OUTER JOIN (SELECT aid as offaid, MAX(start_date + ndays - sysdate) as days_left " +
						"from purchases left join offers ON purchases.ono = offers.ono " +
						"group by aid having max(start_date + ndays - sysdate) > 0) ON ads.aid = offaid " +
						"WHERE poster = '"+userEmail+"' " +
						"order by pdate desc) WHERE  pageno =" + pagecount;

				ResultSet result = stmt.executeQuery(record);

				while(result.next()){
					Date OurDate = result.getDate(4);
					int price = result.getInt(3);
					String title = result.getString(2);
					String type = result.getString(1);
					String aid = result.getString(5);
					String promo = result.getString(6);
					int days_left = result.getInt(7);
					System.out.println("(" + (aidcount+1) + ")" + type + "|"+ title + "|"+ price + "|" + OurDate + "|" + promo + "|" + days_left);

					aids[aidcount] = aid;
					promos[aidcount] = promo;
					aidcount++;

				}

				boolean menu = true;
				while(menu){
					int answer = 0;
					System.out.println("------------------------");
					System.out.println("(1)Select Ad 1");		    
					System.out.println("(2)Select Ad 2");
					System.out.println("(3)Select Ad 3");
					System.out.println("(4)Select Ad 4");
					System.out.println("(5)Select Ad 5");
					System.out.println("(6)Show the next five ads");
					System.out.println("(7)Continue");
					System.out.println("------------------------");
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

					switch (answer){
					case 1:
						continuing = select(aids[0],m_userName,m_password,promos[0]);
						menu = false;
						break;
					case 2:
						continuing = select(aids[1],m_userName,m_password,promos[1]);
						menu = false;
						break;
					case 3:
						continuing = select(aids[2],m_userName,m_password,promos[2]);
						menu = false;
						break;
					case 4:
						continuing = select(aids[3],m_userName,m_password,promos[3]);
						menu = false;
						break;
					case 5:
						continuing = select(aids[4],m_userName,m_password,promos[4]);
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
				System.exit(0);

			}
		}
		// TODO Auto-generated method stub
		return userEmail;
	}

	private static boolean select(String aid,String m_userName,String m_password,String promo)
	{

		Scanner in = new Scanner(System.in);


		boolean menu = true;
		while(menu){
			int answer = 0;
			System.out.println("------------------------");
			System.out.println("(1)Delete ad");	
			if (promo.compareTo("N") == 0){
				System.out.println("(2)Purchase Promotion for ad");
			}
			System.out.println("(3)Continue");
			System.out.println("------------------------");

			while(answer > 3 || answer < 1){
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
			case 1:
				delet_ad(aid,m_userName,m_password);
				return true;
			case 2:
				if (promo.compareTo("N") == 0){
					promote_ad(aid,m_userName,m_password);
				}
				return true;
			case 3:
				menu = false;
				break;
			}
			// TODO Auto-generated method stub

		}
		return true;
	}

	private static void delet_ad(String aid,String m_userName,String m_password)
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
			String del_purchases = "DELETE FROM purchases WHERE aid = '" + aid + "'";
			stmt.executeUpdate(del_purchases);
			String del_ads = "DELETE FROM ads WHERE aid = '" + aid + "'";
			stmt.executeUpdate(del_ads);
			System.out.println("Ad deleted!");
			stmt.close();
			m_con.close();

		} catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}
	}




	private static void promote_ad(String aid,String m_userName,String m_password)
	{
		Scanner in = new Scanner(System.in);
		Connection m_con;
		Statement stmt;
		ArrayList<Integer> offer_list = new ArrayList<Integer>();
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
			String offers = "SELECT * FROM offers";
			ResultSet result = stmt.executeQuery(offers);

			System.out.println("------------------------");
			while(result.next()){
				int ono = result.getInt(1);
				int ndays = result.getInt(2);
				float price = result.getFloat(3);
				System.out.println("Offer Number: " + ono + "		Number of Days: " + ndays + "	Price: " + price);
				offer_list.add(ono);				
			}
			System.out.println("------------------------");

			int answer = 0;
			while(answer > offer_list.size() || answer < 1){
				System.out.print("Offer Selection:");
				try{
					answer = in.nextInt();
				}
				catch(NoSuchElementException e){
					System.err.println("Not an Apporpirate Offer!");
					System.exit(0);
				}
			}
			int ono = offer_list.get(answer-1);

			int pur_id = 0;
			boolean valid = false;
			while(!valid){
				String find_purchase_num = "SELECT count(*) FROM purchases WHERE pur_id ='" + pur_id + "'";

				ResultSet p_result = stmt.executeQuery(find_purchase_num); 

				while(p_result.next()){
					if(p_result.getInt(1) == 0){
						valid = true;
					}
					else{
						pur_id ++;
					}
				}}
			String make_purchase = "insert into purchases values ('" + pur_id + "', SYSDATE, '" + aid + "', " + ono + ")";
			stmt.executeUpdate(make_purchase);
			System.out.println("Ad is now promoted!");
			stmt.close();
			m_con.close();
		}
		catch(SQLException ex) {

			System.err.println("SQLException: " +
					ex.getMessage());
			System.exit(0);

		}

		// TODO Auto-generated method stub

	}}




