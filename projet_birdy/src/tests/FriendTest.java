package tests;

import org.json.JSONException;
import org.json.JSONObject;

import services.Friend;

public class FriendTest {
	
	public static void main(String args[]){
		
		// ajouter un ami
		JSONObject json = new JSONObject();
		json = Friend.addFriend(1, 2);
		System.out.println(json);
		json = Friend.addFriend(1, 3);
		System.out.println(json);
		
		// affichage de la liste d'amis
		try {
			System.out.println(Friend.getFollowingList(3));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// retire un ami
//		json = Friend.removeFriend(1, 2);
//		System.out.println(json);
//		json = Friend.removeFriend(1, 3);
//		System.out.println(json);
	}
}
