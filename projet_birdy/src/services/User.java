package services;

import org.json.JSONException;
import org.json.JSONObject;
import tools.ErrorJSON;
import tools.UserTools;

public class User {

	
	/**
	 * Insère un nouvel utilisateur dans la base de données
	 * @param username le nom de l'utilisateur
	 * @param password le mot de passe de l'utilisateur
	 * @param lastName le nom de famille de l'utilisateur
	 * @param firstName le prénom de l'utilisateur
	 * @param dateOfBirth la date de naissance de l'utilisateur
	 * @param email l'adresse mél de l'utilisateur
	 * @return un json contenant un message d'échec ou de réussite de la création de l'utilisateur
	 */
	public static JSONObject signUp(String username, String password, String lastName, String firstName, String dateOfBirth, String email)
	{
		// SI un champ est vide
		if(username == null || password == null || lastName == null || firstName == null || dateOfBirth == null || email == null)
			return ErrorJSON.serviceRefused("Un champ est vide", ErrorJSON.missingArgumentError);
		
		// SI l'utilisateur existe déjà dans la base de données
		if(UserTools.userNameExists(username))
			return ErrorJSON.serviceRefused("Nom d'utilisateur déjà existant", ErrorJSON.userExistsError);
		
		// SI l'adresse mail est déjà utilisé
		if(UserTools.userNameExists(username))
			return ErrorJSON.serviceRefused("Adresse mail déjà utilisée", ErrorJSON.userExistsError);
		
		// on insère le nouvel utilisateur
		if(UserTools.insertUser(username, password, lastName, firstName, dateOfBirth, email) > 0)
			return ErrorJSON.serviceAccepted("message", "Utilisateur '" + username + "' crée");
		
		return ErrorJSON.serviceRefused("Erreur d'insertion", ErrorJSON.insertError);
	}
	
	
	/**
	 * Supprime un utilisateur de la base de données
	 * @param username le nom de l'utilisateur
	 * @return un json contenant un message d'échec ou de réussite de la suppression de l'utilisateur
	 */
	public static JSONObject deleteUser(String username)
	{
		// SI le username n'existe pas dans la base de données
		if(!UserTools.userNameExists(username))
			return ErrorJSON.serviceRefused("Utilisateur inexistant", ErrorJSON.unknownUserError);
		
		// on supprime l'utilisateur
		if(UserTools.deleteUser(username) > 0)
			return ErrorJSON.serviceAccepted("message", "Utilisateur '" + username +  "' supprimé");			
		
		return ErrorJSON.serviceRefused("Erreur de suppression", ErrorJSON.deleteError);
	}
	
	
	/**
	 * Récupère les informations de l'utilisateur
	 * @param username le lastName de l'utilisateur
	 * @return un json contenant les informations de l'utilisateur
	 * @throws JSONException 
	 */
	public static JSONObject getUser(int user_id) throws JSONException
	{
		// SI l'utilisateur n'existe pas dans la base de données
		if(!UserTools.userIDExists(user_id)) 
			return ErrorJSON.serviceRefused("Utilisateur inexistant", ErrorJSON.unknownUserError);
		
		// SI le retrait d'informations de l'utilisateur a réussi
		JSONObject json;
		if((json = UserTools.getUser(user_id)) != null)
			return ErrorJSON.serviceAccepted(json);	

		return ErrorJSON.serviceRefused("Erreur du retrait des informations de l'utilisateur", ErrorJSON.getError);
	}
	
	
	/**
	 * Récupère la liste des utilisateurs et leurs informations
	 * @return un json contenant la liste des utilisateurs et leurs informations
	 * @throws JSONException 
	 */
	public static JSONObject getUserList() throws JSONException
	{
		// SI le retrait d'informations de l'utilisateur marche
		if(UserTools.getUserList() != null)
			return ErrorJSON.serviceAccepted(UserTools.getUserList());

		return ErrorJSON.serviceRefused("Erreur du retrait des informations de l'utilisateur", ErrorJSON.emptyListError);
	}
	
}
