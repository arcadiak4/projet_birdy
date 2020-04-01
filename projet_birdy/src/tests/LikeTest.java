package tests;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import services.Authentification;
import services.Like;
import tools.AuthentificationTools;
import tools.UserTools;

public class LikeTest {
	
	public static void main(String args[]) {

		JSONObject json = null;
		String userToken = null;
		
		// l'utilisateur se connecte
		String username = "admin3";
		String password = "admin";
		json = Authentification.login(username, password);
		System.out.println(json);
		
		// ajout d'un like de message
		int user_id = UserTools.getUserID(username);
		userToken = AuthentificationTools.getToken(user_id);
		ObjectId message_id = new ObjectId("5e7928ff3abf4d928f9523a2");
		json = Like.addLikeMessage(userToken, message_id);
		System.out.println(json);

		
	}
}
