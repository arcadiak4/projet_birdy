package services;

import org.json.JSONObject;

import tools.AuthentificationTools;
import tools.ErrorJSON;
import tools.UserTools;

public class Authentification {

	
	/**
	 * Ouvre une nouvelle session de l'utilisateur
	 * @param username le nom de l'utilisateur
	 * @param password le mot de passe de l'utilisateur
	 * @return un json contenant un message d'échec ou de réussite de l'ouverture de la session
	 */
	public static JSONObject login(String username, String password)
	{
		// SI le login ou mdp entrés sont vides
		if (username == null || password == null)
			return ErrorJSON.serviceRefused("Mauvais argument", ErrorJSON.badArgumentError);
		
		// SI l'utilisateur n'existe pas dans la base de données des utilisateurs
		if (!UserTools.userNameExists(username))
			return ErrorJSON.serviceRefused("Utilisateur inconnu", ErrorJSON.unknownUserError);			
		
		// SI le mot de passe est mauvais
		if (!AuthentificationTools.checkPassword(username, password))
			return ErrorJSON.serviceRefused("Mauvais mot de passe", ErrorJSON.badPasswordError);
		
		// teste si l'ID existe déjà dans la base de données des sessions ouvertes
		int user_id  = UserTools.getUserID(username);
		if (AuthentificationTools.userIDExists(user_id))
			return ErrorJSON.serviceRefused("Session déjà ouverte", ErrorJSON.userIDExistsError);
		
		// insère une nouvelle session dans la base de données si pas d'erreur
		String token = AuthentificationTools.openSession(user_id);
		if (token != null)
			return ErrorJSON.serviceAccepted("Ouverture de session", token);			
		
		return ErrorJSON.serviceRefused("Erreur d'ouverture de session", ErrorJSON.authentificationError);
	}

	
	/**
	 * Ferme la session d'un utilisateur
	 * @param token le token d'authentification
	 * @return un json contenant un message d'échec ou de réussite de la fermeture de session
	 */
	public static JSONObject logout(String token)
	{
		// SI le token n'existe pas
		if (!AuthentificationTools.tokenExists(token))
			return ErrorJSON.serviceRefused("Token inexistant", ErrorJSON.notInDatabaseError);
		
		// SI la fermeture de session a réussi
		if (AuthentificationTools.closeSession(token) > 0)
			return ErrorJSON.serviceAccepted("Fermeture de session", token);							
		
		return ErrorJSON.serviceRefused("Erreur de fermeture de session", ErrorJSON.authentificationError);
	}
	
	
	/**
	 * Ferme la session d'un utilisateur si le délai d'ouverture de session est dépassé
	 * @param token le token d'authentification
	 * @return un json contenant un message d'échec ou de réussite de la fermeture de session
	 */
	public static JSONObject logoutIfTimeOut(String token)
	{
		// SI le token n'existe pas
		if (!AuthentificationTools.tokenExists(token))
			return ErrorJSON.serviceRefused("Token inexistant", ErrorJSON.notInDatabaseError);
		
		// SI le délai d'ouverture de session a dépassé
		if (!AuthentificationTools.isValidToken(token))
			if (AuthentificationTools.closeSession(token) > 0) 
				return ErrorJSON.serviceAccepted("message", "Session TIMEOUT. Fermeture de session: " + token);						
		// SINON
		else return ErrorJSON.serviceAccepted("message", "Pas de TIMEOUT. Session toujours ouverte");
		
	    return ErrorJSON.serviceRefused("Erreur de fermeture de session", ErrorJSON.authentificationError);
	}
	
}
