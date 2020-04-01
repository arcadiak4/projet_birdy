package services;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import bd.DatabaseMongo;
import tools.AuthentificationTools;
import tools.ErrorJSON;
import tools.PostTools;
import tools.UserTools;

public class Post {
	
	
	private static MongoDatabase mDB = DatabaseMongo.getMongoDBConnection();	
	private static MongoCollection<Document> postsCollection = mDB.getCollection("posts");
	private static MongoCollection<Document> commentsCollection = mDB.getCollection("comments");

	
	/**
	 * Ajoute un post
	 * @param userToken le token d'authentification de l'utilisateur qui écrit le post
	 * @param post le texte post
	 * @return un json contenant un message d'échec ou de réussite de l'opération
	 */
	public static JSONObject addPost(String userToken, String post)
	{
		/* Check du token */

		// SI le token n'existe pas
		if (!AuthentificationTools.tokenExists(userToken))
			return ErrorJSON.serviceRefused("Token inexistant", ErrorJSON.notInDatabaseError);
		// SI le token n'est plus valide
		if (!AuthentificationTools.isValidToken(userToken))
			if (AuthentificationTools.closeSession(userToken) > 0)
				return ErrorJSON.serviceRefused("Session TIMEOUT. Fermeture de session: " + userToken, ErrorJSON.timeOutSessionError);
		// mise à jour du token
		if(!AuthentificationTools.updateSession(userToken))
			return ErrorJSON.serviceRefused("Erreur de la mise à jour du token d'authentification", ErrorJSON.updateError);

		/* ajout du post */
		
		// SI le post à ajouter est vide
		if (post.trim().length() == 0)
			return ErrorJSON.serviceRefused("Le texte est vide", ErrorJSON.missingArgumentError);
		// SI l'ajout a réussi
		String username = UserTools.getUsername(AuthentificationTools.getUserID(userToken));
		if (PostTools.addPost(postsCollection, username, post))
			return ErrorJSON.serviceAccepted("message", "Le post a été ajouté");			
		
		return ErrorJSON.serviceRefused("Erreur de l'ajout du post", ErrorJSON.insertError);
	}

	
	/**
	 * Supprime le post de l'utilisateur correspondant au token d'authentification
	 * @param userToken le token d'authentification de l'utilisateur
	 * @param post_id l'identifiant du post
	 * @return un json contenant un message d'échec ou de réussite de l'opération
	 * @throws JSONException
	 */
	public static JSONObject deletePost(String userToken, ObjectId post_id) throws JSONException
	{
		/* Check du token */

		// SI le token n'existe pas
		if (!AuthentificationTools.tokenExists(userToken))
			return ErrorJSON.serviceRefused("Token inexistant", ErrorJSON.notInDatabaseError);
		// SI le token n'est plus valide
		if (!AuthentificationTools.isValidToken(userToken))
			if (AuthentificationTools.closeSession(userToken) > 0)
				return ErrorJSON.serviceRefused("Session TIMEOUT. Fermeture de session: " + userToken, ErrorJSON.timeOutSessionError);
		// mise à jour du token
		if(!AuthentificationTools.updateSession(userToken))
			return ErrorJSON.serviceRefused("Erreur de la mise à jour du token d'authentification", ErrorJSON.updateError);

		/* suppression du post */
		
		// SI le post n'existe pas dans la base de données
		if (PostTools.getPost(postsCollection, post_id) == null)
			return ErrorJSON.serviceRefused("Post inexistant", ErrorJSON.notInDatabaseError);
		// SI le post n'appartient pas à l'utilisateur
		String username = UserTools.getUsername(AuthentificationTools.getUserID(userToken));
		if (!PostTools.getPost(postsCollection, post_id).getString("username").equals(username))
			return ErrorJSON.serviceRefused("Permission non autorisée", ErrorJSON.permissionError);
		// SI la suppresion a réussi
		if (PostTools.deletePost(postsCollection, commentsCollection, post_id))
			return ErrorJSON.serviceAccepted("message", "Post supprimé !");			

		return ErrorJSON.serviceRefused("Erreur de la suppression du post", ErrorJSON.deleteError);
	}
	
	
	/**
	 * Récupère un post donné
	 * @param userToken le token d'authentification de l'utilisateur
	 * @param post_id l'identifiant du post
	 * @return un json contenant le post ou un message d'échec de l'opération
	 * @throws JSONException
	 */
	public static JSONObject getPost(String userToken, ObjectId post_id) throws JSONException
	{
		/* Check du token */

		// SI le token n'existe pas
		if (!AuthentificationTools.tokenExists(userToken))
			return ErrorJSON.serviceRefused("Token inexistant", ErrorJSON.notInDatabaseError);
		// SI le token n'est plus valide
		if (!AuthentificationTools.isValidToken(userToken))
			if (AuthentificationTools.closeSession(userToken) > 0)
				return ErrorJSON.serviceRefused("Session TIMEOUT. Fermeture de session: " + userToken, ErrorJSON.timeOutSessionError);
		// mise à jour du token
		if(!AuthentificationTools.updateSession(userToken))
			return ErrorJSON.serviceRefused("Erreur de la mise à jour du token d'authentification", ErrorJSON.updateError);

		/* retrait du post */
		
		// SI le retrait du post a réussi
		JSONObject json = new JSONObject();
		Document post = null;
		if ((post  = PostTools.getPost(postsCollection, post_id)) != null)
			return ErrorJSON.serviceAccepted(json.put("post", post));
		
		return ErrorJSON.serviceRefused("Post inexistant", ErrorJSON.notInDatabaseError);
	}
	
	
	/**
	 * Récupère la liste des posts
	 * @param userToken le token d'authentification de l'utilisateur
	 * @return contenant la liste des posts ou un message d'échec de l'opération
	 * @throws JSONException
	 */
	public static JSONObject getPostList(String userToken) throws JSONException
	{
		/* Check du token */

		// SI le token n'existe pas
		if (!AuthentificationTools.tokenExists(userToken))
			return ErrorJSON.serviceRefused("Token inexistant", ErrorJSON.notInDatabaseError);
		// SI le token n'est plus valide
		if (!AuthentificationTools.isValidToken(userToken))
			if (AuthentificationTools.closeSession(userToken) > 0)
				return ErrorJSON.serviceRefused("Session TIMEOUT. Fermeture de session: " + userToken, ErrorJSON.timeOutSessionError);
		// mise à jour du token
		if(!AuthentificationTools.updateSession(userToken))
			return ErrorJSON.serviceRefused("Erreur de la mise à jour du token d'authentification", ErrorJSON.updateError);

		/* retrait de la liste des posts */
		
		// SI la base de données a des posts
		JSONObject json = PostTools.getPostList(postsCollection);
		if (json.getJSONArray("posts").length() > 0)
			return ErrorJSON.serviceAccepted(json);

		return ErrorJSON.serviceRefused("Il n'y a pas de posts", ErrorJSON.getError);
	}
	
}
