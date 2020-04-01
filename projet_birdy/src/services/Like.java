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
import tools.LikeTools;
import tools.PostTools;
import tools.UserTools;

public class Like {
	
	
	private static MongoDatabase mDB = DatabaseMongo.getMongoDBConnection();	
	private static MongoCollection<Document> messagesCollection = mDB.getCollection("messages");
	private static MongoCollection<Document> usersCollection = mDB.getCollection("users");
	
	
	public static JSONObject addLikeMessage(String userToken, ObjectId message_id)
	{
		// Vérifier si le token existe
		
		// Vérifier si le token est encore valide
				
		// mettre à jour le token
		
		// SI le message n'existe pas dans la base de données
		if(PostTools.getPost(messagesCollection, message_id) == null)
			return ErrorJSON.serviceRefused("Message inexistant", ErrorJSON.notInDatabaseError);
		
		// SI l'ajout du like a réussi
		int user_id = AuthentificationTools.getUserID(userToken);
		String username = UserTools.getUsername(user_id);
		if(LikeTools.addLikeMessage(messagesCollection, usersCollection, username, message_id))
			return ErrorJSON.serviceAccepted("message", "Message liké !");			

		return ErrorJSON.serviceRefused("Erreur de l'ajout du like de message", ErrorJSON.insertError);
	}
	
	
	public static JSONObject addLikeComment(String userToken, ObjectId message_id, ObjectId comment_id)
	{
		// Vérifier si le token existe
		
		// Vérifier si le token est encore valide
				
		// mettre à jour le token
		
		// SI le message n'existe pas dans la base de données
		if(PostTools.getPost(messagesCollection, message_id) == null)
			return ErrorJSON.serviceRefused("Message inexistant", ErrorJSON.notInDatabaseError);
		
		// SI le commentaire n'existe pas dans la base de données
		if(CommentTools.getComment(messagesCollection, comment_id) == null)
			return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);

		// SI l'ajout du like a réussi
		int user_id = AuthentificationTools.getUserID(userToken);
		String username = UserTools.getUsername(user_id);
		if(LikeTools.addLikeComment(messagesCollection, username, message_id, comment_id))
			return ErrorJSON.serviceAccepted("message", "Commentaire liké !");			

		return ErrorJSON.serviceRefused("Erreur de l'ajout du like de commentaire", ErrorJSON.insertError);
	}
	
	
	public static JSONObject unlikeMessage(String userToken, ObjectId message_id)
	{
		// Vérifier si le token existe
		
		// Vérifier si le token est encore valide
				
		// mettre à jour le token
		
		// SI le message n'existe pas dans la base de données
		if(PostTools.getPost(messagesCollection, message_id) == null)
			return ErrorJSON.serviceRefused("Message inexistant", ErrorJSON.notInDatabaseError);
		
		// SI l'unlike a réussi
		int user_id = AuthentificationTools.getUserID(userToken);
		String username = UserTools.getUsername(user_id);
		if(LikeTools.unlikeMessage(messagesCollection, username, message_id))
			return ErrorJSON.serviceAccepted("message", "Message unliké !");			

		return ErrorJSON.serviceRefused("Erreur de l'unlike du message", ErrorJSON.deleteError);
	}
	
	
	public static JSONObject unlikeComment(String userToken, ObjectId message_id, ObjectId comment_id)
	{
		// Vérifier si le token existe
		
		// Vérifier si le token est encore valide
				
		// mettre à jour le token
		
		// SI le message n'existe pas dans la base de données
		if(PostTools.getPost(messagesCollection, message_id) == null)
			return ErrorJSON.serviceRefused("Message inexistant", ErrorJSON.notInDatabaseError);
		
		// SI le commentaire n'existe pas dans la base de données
		if(CommentTools.getComment(messagesCollection, comment_id) == null)
			return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);

		// SI l'unlike a réussi
		int user_id = AuthentificationTools.getUserID(userToken);
		String username = UserTools.getUsername(user_id);
		if(LikeTools.unlikeComment(messagesCollection, username, message_id, comment_id))
			return ErrorJSON.serviceAccepted("message", "Commentaire unliké !");			

		return ErrorJSON.serviceRefused("Erreur de l'unlike du commentaire", ErrorJSON.deleteError);
	}
	
	
	public static JSONObject getMessageLikeList(ObjectId message_id) throws JSONException
	{
		// SI le message n'existe pas dans la base de données
		if(PostTools.getPost(messagesCollection, message_id) == null)
			return ErrorJSON.serviceRefused("Message inexistant", ErrorJSON.notInDatabaseError);
		
		// SI le message a des likes
		JSONObject json = LikeTools.getMessageLikeList(messagesCollection, message_id);
		if(json.getJSONArray("like").length() > 0)
			return ErrorJSON.serviceAccepted(json);
		
		return ErrorJSON.serviceRefused("Le message n'a pas de likes", ErrorJSON.emptyListError);
	}
	
	
	public static JSONObject getCommentLikeList(ObjectId message_id, ObjectId comment_id) throws JSONException
	{
		// SI le message n'existe pas dans la base de données
		if(PostTools.getPost(messagesCollection, message_id) == null)
			return ErrorJSON.serviceRefused("Message inexistant", ErrorJSON.notInDatabaseError);
		
		// SI le commentaire n'existe pas dans la base de données
		if(CommentTools.getComment(messagesCollection, comment_id) == null)
			return ErrorJSON.serviceRefused("Commentaire inexistant", ErrorJSON.notInDatabaseError);
		
		// SI le commentaire a des likes
		JSONObject json = LikeTools.getCommentLikeList(messagesCollection, comment_id);
		if(json.getJSONArray("like").length() > 0)
			return ErrorJSON.serviceAccepted(json);
		
		return ErrorJSON.serviceRefused("Le commentaire n'a pas de likes", ErrorJSON.emptyListError);
	}
	
}
