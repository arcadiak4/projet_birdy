package services;

import org.json.JSONException;
import org.json.JSONObject;

import tools.ErrorJSON;
import tools.FriendTools;
import tools.UserTools;

public class Friend {
	

	/**
	 * Ajoute un ami dans la liste d'amis d'un utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @param friend_id l'identifiant de l'ami à rajouter dans la liste d'amis
	 * @return un json contenant un message de succès ou d'échec de l'opération
	 */
	public static JSONObject addFriend(int user_id, int friend_id)
	{
		// SI l'identifiant n'existe pas
		if(!UserTools.userIDExists(user_id)) 
			return ErrorJSON.serviceRefused("Utilisateur inexistant", ErrorJSON.unknownUserError);
		
		// SI les deux utilisateurs sont amis
		if (FriendTools.friendExists(user_id, friend_id))
			return ErrorJSON.serviceRefused("L'utilisateur " + friend_id + " est déjà votre ami de l'utilisateur " + user_id, ErrorJSON.userExistsError);
		
		// on ajoute l'ami dans la liste d'amis
		if(FriendTools.addFriend(user_id, friend_id) > 0)
			return ErrorJSON.serviceAccepted("message", "Ami ajouté !");			

		return ErrorJSON.serviceRefused("Erreur d'ajout de l'ami", ErrorJSON.insertError);
	}
	
	
	/**
	 * Supprime un ami de la liste d'amis d'un utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @param friend_id l'identifiant de l'ami à enlever de la liste d'amis
	 * @return un json contenant un message de succès ou d'échec de l'opération
	 */
	public static JSONObject removeFriend(int user_id, int friend_id)
	{
		// SI l'identifiant n'existe pas
		if(!UserTools.userIDExists(user_id)) 
			return ErrorJSON.serviceRefused("Utilisateur inexistant", ErrorJSON.unknownUserError);
		
		// SI les deux utilisateurs ne sont pas amis
		if (!FriendTools.friendExists(user_id, friend_id))
			return ErrorJSON.serviceRefused("L'utilisateur " + friend_id + " n'est pas dans votre liste d'amis de l'utilisateur " + user_id, ErrorJSON.unknownUserError);
		
		// on supprime l'ami de la liste d'amis
		if(FriendTools.deleteFriend(user_id, friend_id) > 0)
			return ErrorJSON.serviceAccepted("message", "Ami retiré !");			
		
		return ErrorJSON.serviceRefused("Erreur de suppression de l'ami", ErrorJSON.deleteError);
	}
	
	
	/**
	 * Récupère la liste d'amis d'un utilisateur
	 * @param user_id l'identifiant de l'utilisateur dont on veut récupérer la liste d'amis 
	 * @return un json contenant la liste d'amis ou un message d'échec de l'opération
	 * @throws JSONException
	 */
	public static JSONObject getFollowingList(int user_id) throws JSONException
	{
		// SI l'identifiant n'existe pas
		if(!UserTools.userIDExists(user_id))
			return ErrorJSON.serviceRefused("Utilisateur inexistant", ErrorJSON.unknownUserError);
		
		// SI l'utilisateur a des amis
		JSONObject json = FriendTools.getFriendList(user_id);
		if(json.getJSONArray("liste d'amis").length() > 0)
			return ErrorJSON.serviceAccepted(json);
		
		return ErrorJSON.serviceRefused("L'utilisateur " + user_id + " n'a pas d'amis", ErrorJSON.emptyListError);
	}
	
}
