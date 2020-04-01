package services;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import bd.DatabaseMongo;
import tools.AuthentificationTools;
import tools.CommentTools;
import tools.ErrorJSON;
import tools.PostTools;
import tools.UserTools;

public class Comment {


	private static MongoDatabase mDB = DatabaseMongo.getMongoDBConnection();	
	private static MongoCollection<Document> postsCollection = mDB.getCollection("posts");
	private static MongoCollection<Document> commentsCollection = mDB.getCollection("comments");

	
	/**
	 * Ajoute un commentaire à un post ou commentaire donné par le parent_id
	 * @param userToken le token d'authentification de l'utilisateur qui écrit le commentaire
	 * @param post_id l'identifiant du post
	 * @param parent_id l'identifiant du post ou commentaire parent à commenter
	 * @param text le texte du commentaire
	 * @return un json contenant un message d'échec ou de réussite de l'opération
	 */
	public static JSONObject addComment(String userToken, ObjectId post_id, ObjectId parent_id, String text)
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
				
		/* Ajout du commentaire */ 
		
		// SI le post n'existe pas dans la base de données
		if (PostTools.getPost(postsCollection, post_id) == null)
			return ErrorJSON.serviceRefused("Post inexistant", ErrorJSON.notInDatabaseError);
		// SI c'est à un commentaire qu'on souhaite répondre
		if (!(post_id.toHexString()).equals(parent_id.toHexString()))
			// SI il n'existe pas dans la base de données
			if (CommentTools.getComment(commentsCollection, parent_id) == null)
				return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);
		// SI le commentaire à ajouter est vide
		if (text.trim().length() == 0)
			return ErrorJSON.serviceRefused("Le texte est vide", ErrorJSON.missingArgumentError);
		// SI l'ajout a réussi
		String username = UserTools.getUsername(AuthentificationTools.getUserID(userToken));
		if (CommentTools.addComment(commentsCollection, username, post_id, parent_id, text))
			return ErrorJSON.serviceAccepted("message", "Le commentaire a été ajouté");			
		
		return ErrorJSON.serviceRefused("Erreur de l'ajout de commentaire", ErrorJSON.insertError);
	}
	
	
	/**
	 * Supprime le commentaire de l'utilisateur correspondant au token d'authentification
	 * @param userToken le token d'authentification de l'utilisateur
	 * @param post_id l'identifiant du post où figure son commentaire
	 * @param comment_id l'identifiant du commentaire à supprimer
	 * @return un json contenant un message d'échec ou de réussite de l'opération
	 * @throws JSONException 
	 */
	public static JSONObject deleteComment(String userToken, ObjectId post_id, ObjectId comment_id) throws JSONException
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
		
		/* Suppression du commentaire */
		
		// SI le post n'existe pas dans la base de données
		if (PostTools.getPost(postsCollection, post_id) == null)
			return ErrorJSON.serviceRefused("Post inexistant", ErrorJSON.notInDatabaseError);
		// SI le commentaire n'existe pas dans la base de données
		if (CommentTools.getComment(commentsCollection, comment_id) == null)
			return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);
		// SI le commentaire n'appartient pas à l'utilisateur
		String username = UserTools.getUsername(AuthentificationTools.getUserID(userToken));
		if (!CommentTools.getComment(commentsCollection, comment_id).getString("username").equals(username))
			return ErrorJSON.serviceRefused("Permission non autorisée", ErrorJSON.permissionError);
		// SI la suppresion du commentaire a réussi
		if (CommentTools.deleteComment(postsCollection, commentsCollection, post_id, comment_id))
			return ErrorJSON.serviceAccepted("message", "Commentaire supprimé !");			

		return ErrorJSON.serviceRefused("Erreur de la suppression du commentaire", ErrorJSON.deleteError);
	}
	
	
	/**
	 * Récupère un commentaire
	 * @param userToken le token d'authentification de l'utilisateur
	 * @param post_id l'identifiant du post
	 * @param comment_id l'identifiant du commentaire à récupérer
	 * @return un json contenant le commentaire ou un message d'échec de l'opération
	 * @throws JSONException
	 */
	public static JSONObject getComment(String userToken, ObjectId comment_id) throws JSONException
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

		/* Retrait du commentaire */
		
		// SI le retrait du commentaire a réussi
		JSONObject json = new JSONObject();
		Document doc = null;
		if ((doc = CommentTools.getComment(commentsCollection, comment_id)) != null)
			return ErrorJSON.serviceAccepted(json.put("commentaire", doc));
		
		return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);
	}
	

	/**
	 * Récupère la liste des commentaires d'un post
	 * @param userToken le token d'authentification de l'utilisateur
	 * @param post_id l'identifiant du post dont on souhaite récupérer la liste de commentaires
	 * @return un json contenant la liste des commentaires ou un message d'échec de l'opération
	 * @throws JSONException
	 */
	public static JSONObject getPostCommentList(String userToken, ObjectId post_id) throws JSONException
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

		/* Retrait de la liste des commentaires du post */
		
		// SI le post n'existe pas dans la base de données
		if (PostTools.getPost(postsCollection, post_id) == null)
			return ErrorJSON.serviceRefused("Post inexistant", ErrorJSON.notInDatabaseError);
		// SI le post a des commentaires
		JSONObject json = CommentTools.getPostCommentList(postsCollection, commentsCollection, post_id);
		if (json.getJSONArray("comments").length() > 0)
			return ErrorJSON.serviceAccepted(json);

		return ErrorJSON.serviceRefused("Le post n'a pas de commentaires", ErrorJSON.emptyListError);
	}
	
	
	/**
	 * Récupère la liste des commentaires d'un commentaire
	 * @param userToken le token d'authentification de l'utilisateur
	 * @param comment_id l'identifiant du commentaire dont on souhaite récupérer la liste de commentaires
	 * @return un json contenant la liste des commentaires ou un message d'échec de l'opération
	 * @throws JSONException
	 */
	public static JSONObject getCommentList(String userToken, ObjectId comment_id) throws JSONException
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
		
		/* Retrait de la liste des commentaires du commentaire */
		
		// SI le commentaire n'existe pas dans la base de données
		if (CommentTools.getComment(commentsCollection, comment_id) == null)
			return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);
		// SI le commentaire a des commentaires
		JSONObject json = CommentTools.getCommentList(commentsCollection, comment_id);
		if (json.getJSONArray("comments").length() > 0)
			return ErrorJSON.serviceAccepted(json);

		return ErrorJSON.serviceRefused("Le commentaire n'a pas de commentaires", ErrorJSON.emptyListError);
	}
	
}
