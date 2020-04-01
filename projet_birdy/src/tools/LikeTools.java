package tools;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class LikeTools {
	
	
	public static boolean addLikeMessage(MongoCollection<Document> messagesCollection, MongoCollection<Document> usersCollection, String username, ObjectId message_id)
	{
	
		return false;
	}
	
	
	public static boolean addLikeComment(MongoCollection<Document> collection, String username, ObjectId message_id,
			ObjectId comment_id)
	{
		
		return false;
	}

	
	public static boolean unlikeMessage(MongoCollection<Document> collection, String username, ObjectId message_id)
	{

		return false;
	}

	
	public static boolean unlikeComment(MongoCollection<Document> collection, String username, ObjectId message_id,
			ObjectId comment_id)
	{

		return false;
	}


	public static JSONObject getMessageLikeList(MongoCollection<Document> collection, ObjectId message_id)
	{
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public static JSONObject getCommentLikeList(MongoCollection<Document> commentsCollection, ObjectId comment_id)
	{
		JSONObject json = new JSONObject();
		try {
			// on retrouve le commentaire liké dans la base de donnée
			Document findQuery = new Document("_id", comment_id);
			MongoCursor<Document> cursor = commentsCollection.find(findQuery).iterator();
			
			while (cursor.hasNext())
			{
				Document doc = cursor.next();
				List<Document> list = (List<Document>) doc.get("likes");	
				json.put("comment", doc.get("comment"));
				json.put("likes", list);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	
	@SuppressWarnings("unchecked")
	public static boolean likeExists(MongoCollection<Document> postsCollection, MongoCollection<Document> commentsCollection, ObjectId _id, int postOrComment)
	{
		boolean returnedValue =  false;
		
		switch(postOrComment)
		{
			case 0:
				// on retrouve le post dans la base de donnée
				Document findQuery = new Document("_id", _id);
				MongoCursor<Document> cursor = postsCollection.find(findQuery).iterator();
				
				List<Document> likes =  (List<Document>) cursor.next().get("likes");
				for (Document like : likes)
				{
					if (like.get("comment_id").equals(_id)) returnedValue = true;
				}
			case 1:
				// on retrouve le commentaire dans la base de donnée
				findQuery = new Document("_id", _id);
				cursor = commentsCollection.find(findQuery).iterator();
				
				likes =  (List<Document>) cursor.next().get("likes");
				for (Document like : likes)
				{
					if (like.get("comment_id").equals(_id)) returnedValue = true;
				}
			default:
				returnedValue = false;
		}

		return returnedValue;
	}

}
