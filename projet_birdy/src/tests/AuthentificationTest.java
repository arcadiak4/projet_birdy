package tests;

import services.Authentification;
import tools.AuthentificationTools;
import tools.UserTools;

public class AuthentificationTest {

	public static void main(String[] args) {
		
		
		/* TEST 1 */
		/* avec timeout */
		
		// teste la connexion d'un utilisateur
		String username = "admin1", password = "admin";
		System.out.println(Authentification.login(username, password));
		
		// on reteste la connexion
		System.out.println(Authentification.login(username, password));
		
		// verification token
		String token = AuthentificationTools.getToken(UserTools.getUserID(username));
		System.out.println(Authentification.logoutIfTimeOut(token) + "\n");
		
		// temps d'attente
		try {
			System.out.println("Temps d'attente = " + (AuthentificationTools.SESSION_TIME_THRESHOLD) + "ms");
			System.out.println("...\n");
			Thread.sleep(AuthentificationTools.SESSION_TIME_THRESHOLD);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// verification token
		token = AuthentificationTools.getToken(UserTools.getUserID(username));
		System.out.println(Authentification.logoutIfTimeOut(token) + "\n");
		
		
		/* TEST 2 */
		/* sans timeout */
		
		// Nouvelle connexion
		System.out.println(Authentification.login(username, password));
		
		// forcer la fermeture de session (sans timout)
		token = AuthentificationTools.getToken(UserTools.getUserID(username));
		System.out.println(Authentification.logout(token));
	}

}
