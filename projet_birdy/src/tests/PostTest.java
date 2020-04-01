package tests;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import services.Authentification;
import services.Post;
import tools.AuthentificationTools;
import tools.UserTools;

public class PostTest {
	
	public static void main(String args[]) {
		
		JSONObject json = null;
		String userToken = null;
		try {
			
			String username = "admin3", password = "admin";
			ObjectId post_id = new ObjectId("5e827083b2ddd11752808a11");
			
			/*
			 +==================================+
			 |	  CONNEXION DE L'UTILISATEUR
			 +==================================+
			 */
			
			System.out.println(Authentification.login(username, password) + "\n");
			userToken = AuthentificationTools.getToken(UserTools.getUserID(username));
			
			/*
			 +==================================+
			 |	    	AJOUT D'UN POST
			 +==================================+
			 */

			json = Post.addPost(userToken, "Petit stream ce soir, cette fois-ci Bloodborne au rendez-vous !");
			System.out.println(json + "\n");
			
			/*
			 +==================================+
			 |	 	SUPPRESSION D'UN POST
			 +==================================+
			 */
			
//			json = Post.deletePost(userToken, post_id);
//			System.out.println(json + "\n");

			/*
			 +==================================+
			 |	   RETRAIT DE COMMENTAIRES
			 +==================================+
			 */
			
			System.out.println("Affichage d'un post\n" + Post.getPost(userToken, post_id) + "\n");
			System.out.println("Affichage de la liste des posts\n" + Post.getPostList(userToken) + "\n");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
