package tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

public class CommentTools {

	
	/**
	 * Ajouter un commentaire
	 * @param commentsCollection la collection contenant tous les commentaires
	 * @param username le nom de l'utilisateur qui écrit le commentaire
	 * @param post_id l'identifiant du post
	 * @param parent_id l'identifiant du post ou commentaire parent à commenter
	 * @param text le texte du commentaire
	 * @return true si le commentaire a bien été ajouté, false sinon
	 */
	public static boolean addComment(MongoCollection<Document> commentsCollection, String username, ObjectId post_id, ObjectId parent_id, String text)
	{
		boolean returnedValue =  false;

		// on crée le nouveau sous-commentaire
		Document listItem = new Document();
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		listItem.append("post_id", post_id);
		listItem.append("parent_id", parent_id);
		listItem.append("username", username);
		listItem.append("comment", text);
		listItem.append("date", date);
		listItem.append("likes", new ArrayList<Document>());
		
		// on ajoute dans la base de données des commentaires
		try {
			commentsCollection.insertOne(listItem);
			returnedValue = true;
		} catch (MongoException e) {
			e.printStackTrace();
		}

		return returnedValue;
	}


	/**
	 * Supprime un commentaire
	 * @param postsCollection la collection contenant tous les posts
	 * @param post_id l'identifiant du post à commenter
	 * @param comment_id l'identifiant du commentaire
	 * @return true si le post a bien été supprimé, false sinon
	 */
	@SuppressWarnings("unchecked")
	public static boolean deleteComment(MongoCollection<Document> postsCollection, MongoCollection<Document> commentsCollection, ObjectId post_id, ObjectId comment_id)
	{
		boolean returnedValue =  true;
		
		// 1. $grapheLookup aggregation (stocke dans le tableau children les sous-commentaires)
		Document graph = new Document();
		graph.append("from", "comments");
		graph.append("startWith", "$_id");
		graph.append("connectFromField", "_id");
		graph.append("connectToField", "parent_id");
		graph.append("as", "children");
		
		// 2. $match aggregation (sélectionne le commentaire à supprimer)
		Document match = new Document();
		match.append("_id", comment_id);
		
		// on applique l'aggregation
		List<Document> pipeline = new ArrayList<Document>();
		pipeline.add(new Document("$graphLookup", graph));
		pipeline.add(new Document("$match", match));
		AggregateIterable<Document> output = commentsCollection.aggregate(pipeline);
		
		// suppression
		MongoCursor<Document> cursor = output.iterator();
		
		// on récupère la liste children des sous-commentaires
		List<Document> childrenToDelete = null;
		if (cursor.hasNext())
		{
			Document doc = cursor.next();
			childrenToDelete = (List<Document>) doc.get("children");
			
			// on supprime les sous-commentaires
			for (Document child : childrenToDelete)
			{
				DeleteResult result = commentsCollection.deleteOne(child);
				if (result.getDeletedCount() != 1) 
				{
					returnedValue = false;
					break;
				}
			}
			
			System.out.println("Nombres de ses commentaires supprimés = " + childrenToDelete.size());
			
			// on supprime le commentaire lui-même
			Document commentToDelete = new Document("_id", comment_id);
			cursor = commentsCollection.find(commentToDelete).iterator();
			DeleteResult result = commentsCollection.deleteOne(cursor.next());
			if (result.getDeletedCount() != 1) returnedValue = false;
		}
		 
		return returnedValue;
	}

	
	/**
	 * Récupère un commentaire
	 * @param commentsCollection la collection contenant tous les commentaires
	 * @param post_id l'identifiant du post
	 * @param comment_id l'identifiant du commentaire
	 * @return le document contenant le commentaire, null sinon
	 */
	public static Document getComment(MongoCollection<Document> commentsCollection, ObjectId comment_id)
	{
		Document returnedValue = null;
		
		Document findQuery = new Document("_id", comment_id);
		MongoCursor<Document> cursor = commentsCollection.find(findQuery).iterator();
		if (cursor.hasNext()) returnedValue = cursor.next();

		return returnedValue;
	}
	
	
	/**
	 * Récupère la liste des commentaires d'un commentaire
	 * @param commentsCollection la collection contenant tous les commentaires
	 * @param comment_id l'identifiant du commentaire dont on souhaite récupérer la liste de commentaires
	 * @return contenant la liste des commentaires sous forme de jsonArray
	 */
	public static JSONObject getCommentList(MongoCollection<Document> commentsCollection, ObjectId comment_id)
	{
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			// on récupère les commentaires du commentaire identifié par comment_id
			Document findQuery = new Document("parent_id", comment_id);
			MongoCursor<Document> cursor = commentsCollection.find(findQuery).iterator();
			
			while (cursor.hasNext())
			{
				Document doc = cursor.next();
				jsonArray.put(doc);
			}
			
			json.put("comment", CommentTools.getComment(commentsCollection, comment_id));
			json.put("comments", jsonArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	
	/**
	 * Récupère les commentaires d'un post
	 * @param postsCollection la collection contenant tous les posts
	 * @param commentsCollection la collection contenant tous les commentaires
	 * @param post_id l'identifiant du post dont on souhaite récupérer les commentaires
	 * @return contenant la liste des commentaires sous forme de jsonArray
	 */
	public static JSONObject getPostCommentList(MongoCollection<Document> postsCollection, MongoCollection<Document> commentsCollection, ObjectId post_id)
	{
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			// on récupère les commentaires du post identifié par post_id
			Document findQuery = new Document("parent_id", post_id);
			MongoCursor<Document> cursor = commentsCollection.find(findQuery).iterator();
			
			while (cursor.hasNext())
			{
				Document doc = cursor.next();
				jsonArray.put(doc);
			}
			
			json.put("post", PostTools.getPost(postsCollection, post_id));
			json.put("comments", jsonArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
	
}
