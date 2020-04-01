package tests;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import services.Authentification;
import services.Comment;
import tools.AuthentificationTools;
import tools.UserTools;

public class CommentTest {
	
	public static void main(String args[]) {
		
		JSONObject json = null;
		String userToken = null;
		try {
			
			String username = "admin3", password = "admin";
			ObjectId post_id = new ObjectId("5e8270b0cb041274a90b6778");
			ObjectId parent_id = new ObjectId("5e8270b0cb041274a90b6778");
			ObjectId comment_id = new ObjectId("5e827459c1e9ce10be442528");
			
			/*
			 +==================================+
			 |	  CONNEXION DE L'UTILISATEUR
			 +==================================+
			 */
			
			System.out.println(Authentification.login(username, password) + "\n");
			userToken = AuthentificationTools.getToken(UserTools.getUserID(username));
			
			/*
			 +==================================+
			 |	    AJOUT D'UN COMMENTAIRE
			 +==================================+
			 */

			json = Comment.addComment(userToken, post_id, parent_id, "oh oui ! que du bon ça");
			System.out.println(json + "\n");
			
			/*
			 +==================================+
			 |	 SUPPRESSION D'UN COMMENTAIRE
			 +==================================+
			 */
			
//			json = Comment.deleteComment(userToken, post_id, comment_id);
//			System.out.println(json + "\n");
			
			/*
			 +==================================+
			 |	   RETRAIT DE COMMENTAIRES
			 +==================================+
			 */
			
			System.out.println("Affichage d'un commentaire\n" + Comment.getComment(userToken, comment_id) + "\n");
			System.out.println("Affichage de la liste des commentaires du commentaire\n" + Comment.getCommentList(userToken, comment_id) + "\n");
			System.out.println("Affichage de la liste des commentaires du post\n" + Comment.getPostCommentList(userToken, post_id) + "\n");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
