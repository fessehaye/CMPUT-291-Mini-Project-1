import java.util.NoSuchElementException;
import java.util.Scanner;


public class ujiji {
	private static String UserEmail;
	/**
	 * @param args
	 */
	/*
	 * Main function for the ujiji application:
	 * first allows you to enter your oracle username and password
	 * then prompts you to the user login menu until you successfully login or registers
	 * then prompts the user so they can choose to either:
	 * 1.search for ads
	 * 2.list own ads
	 * 3.Search users
	 * 4.Post an ad
	 * 5.Logoff
	 * 
	 * The entire program is looped so the only way to to go to the login menu and quit.
	 * All selections can be chosen by the selected indexs.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Oracle Sign In
		Scanner in = new Scanner(System.in);
		System.out.print("Oracle UserName:");
		String m_userName = in.nextLine();
		System.out.print("Oracle Password:");
		String m_password = in.nextLine();
		
		//start the application
		boolean running = true;
		while(running){

			while(UserEmail == null){
				System.out.println("Welcome to Ujiji ");
				UserEmail = login.menu(UserEmail,m_userName,m_password);
			}
			System.out.println("------------------------");
			System.out.println("(1)Search for ads");		    
			System.out.println("(2)List own ads");
			System.out.println("(3)Search for users");
			System.out.println("(4)Post an Ad");		    
			System.out.println("(5)Logout");
			System.out.println("------------------------");
			
			Integer Answer = 0;
			//Loops until given proper input
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
				//search for ads
				UserEmail = ads.search(UserEmail,m_userName,m_password);
				break;
			case 2:
				//list own ads
				UserEmail = ads_2.List_Ads(UserEmail,m_userName,m_password);
				break;
			case 3:
				//search for users
				UserEmail = prj_2.search(UserEmail,m_userName,m_password);
				break;
			case 4:
				//post an ad
				UserEmail = ads.post_ad(UserEmail,m_userName,m_password);
				break;
			case 5:
				UserEmail = login.logoff(UserEmail,m_userName,m_password);
				break;
			}

		}
	}

}
