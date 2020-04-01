package tools;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorJSON {
	
	/**
	 * Codes d'erreur
	 */
	public static int unknownUserError = 1;
	public static int notInDatabaseError = 2;
	public static int permissionError = 3;
	public static int emptyListError = 4;
	// User
	public static int missingArgumentError = 10;
	public static int userExistsError = 11;
	public static int insertError = 12;
	public static int deleteError = 13;
	public static int getError = 14;
	// Authentification
	public static int badArgumentError = 20;
	public static int badPasswordError = 21;
	public static int authentificationError = 22;
	public static int userIDExistsError = 23;
	public static int timeOutSessionError = 24;
	public static int updateError = 25;
	
	/**
	 * Génère un json décrivant l'erreur du service
	 * @param message le message d'erreur
	 * @param errorCode le code d'erreur spécifié dans les attributs de classe
	 * @return un json
	 */
	public static JSONObject serviceRefused(String message, int errorCode)
	{
		JSONObject finalJson = new JSONObject();
		try {
			JSONObject json = new JSONObject();
			json.put("erreur", errorCode);
			json.put("message", message);
			
			finalJson.put("Service refusé", json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJson;
	}
	
	
	/**
	 * Génère un json avec une clé et sa valeur
	 * @param key la clé
	 * @param value la valeur
	 * @return un json
	 */
	public static JSONObject serviceAccepted(String key, String value)
	{
		JSONObject json = new JSONObject();
		try {
			json.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
	/**
	 * Renvoie un json
	 * @param json
	 * @return un json
	 * @throws JSONException
	 */
	public static JSONObject serviceAccepted(JSONObject json) throws JSONException
	{
		return json;
	}

}
