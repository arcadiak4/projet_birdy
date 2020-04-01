package tests;

import org.json.JSONException;
import org.json.JSONObject;

import services.User;

public class UserTest {	
	
	public static void main(String args[]) {
		
		// ajout d'utilisateurs
		JSONObject json = new JSONObject();
		json = User.signUp("admin1", "admin", "RESTES", "Célia", "08-02-1998", "celia.restes@protonmail.ch");
		System.out.println(json);
		json = User.signUp("admin2", "admin", "SELVARADJOU", "Sona", "15-06-1996", "selvaradjousona@gmail.com");
		System.out.println(json);
		json = User.signUp("admin3", "admin", "MALLET", "Julien", "18-11-1997", "Julien.Mallet@protonmail.ch");
		System.out.println(json);
		
		// Affichage
		try {
			// affichage des informations d'un utilisateur
			int user_id = 3;
			System.out.println("\nAffichage d'un utilisateur\n" + User.getUser(user_id) + "\n");
			// affichage des informations de tous les utilisateurs
			System.out.println("Affichage de la liste des utilisateurs\n" + User.getUserList() + "\n");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// suppression d'utilisateurs
//		json = User.deleteUser("admin1");
//		System.out.println(json);
//		json = User.deleteUser("admin2");
//		System.out.println(json);
//		json = User.deleteUser("admin3");
//		System.out.println(json);
	}	
}


