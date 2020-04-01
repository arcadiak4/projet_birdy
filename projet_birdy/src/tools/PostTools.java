package tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

public class PostTools {
	

	/**
	 * Ajoute un post
	 * @param postsCollection la collection contenant tous les posts
	 * @param username le nom de l'utilisateur qui écrit le post
	 * @param post le texte du post
	 * @return true si l'ajout a réussi, false sinon
	 */
	public static boolean addPost(MongoCollection<Document> postsCollection, String username, String post)
	{
		boolean returnedValue =  false;

		// on crée le nouveau post
		Document listItem = new Document();
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		listItem.append("username", username);
		listItem.append("post", post);
		listItem.append("date", date);
		listItem.append("likes", new ArrayList<Document>());
		
		try {
			// on l'ajoute dans la collection contenant les autres posts
			postsCollection.insertOne(listItem);
			returnedValue = true;
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}

	
	/**
	 * Supprime un post
	 * @param postsCollection la collection contenant tous les posts
	 * @param commentsCollection la collection contenant tous les commentaires
	 * @param post_id l'identifiant du post
	 * @return true si la suppression a réussi, false sinon
	 */
	public static boolean deletePost(MongoCollection<Document> postsCollection, MongoCollection<Document> commentsCollection, ObjectId post_id)
	{
		boolean returnedValue =  false;
		
		// on cherche le post à supprimer dans la base de donnée
		Document findQuery = new Document("_id", post_id);
		
		// on le supprime...
		DeleteResult result1 = postsCollection.deleteOne(findQuery);
		if (result1.getDeletedCount() == 1)
		{
			// ...et ses commentaires et sous-commentaires, etc
			findQuery = new Document("post_id", post_id);
			DeleteResult result2 = commentsCollection.deleteMany(findQuery);
			System.out.println("Nombres de ses commentaires supprimés = " + result2.getDeletedCount());
			returnedValue = true;
		}
		return returnedValue;
	}
	
	
	/**
	 * Récupère le post s'il existe
	 * @param postsCollection la collection contenant tous les posts
	 * @param post_id l'identifiant du post
	 * @return le document contenant le post, null sinon
	 */
	public static Document getPost(MongoCollection<Document> postsCollection, ObjectId post_id)
	{
		Document returnedValue = null;
		
		Document findQuery = new Document("_id", post_id);
		MongoCursor<Document> cursor = postsCollection.find(findQuery).iterator();
		if (cursor.hasNext()) returnedValue = cursor.next();
		
		return returnedValue;
	}


	/**
	 * Récupère la liste des posts
	 * @param postsCollection la collection contenant tous les posts
	 * @return un json contenant la liste des posts sous forme de jsonArray
	 */
	public static JSONObject getPostList(MongoCollection<Document> postsCollection)
	{
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		FindIterable<Document> output = postsCollection.find();
		for (Document dbObject : output) jsonArray.put(dbObject);
		try {
			json.put("posts", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
	
}
