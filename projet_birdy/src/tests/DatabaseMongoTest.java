package tests;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import bd.DatabaseMongo;

public class DatabaseMongoTest {
	
	public static void main(String args[]) {
		
		MongoDatabase mDB = DatabaseMongo.getMongoDBConnection();	
	
		// teste l'ajout d'un utilisateur
		Document doc = new Document();
		doc.append("name", "MongoDB");
		doc.append("type", "database");
		doc.append("count", 1);
		Document info = new Document();
		info.append("x", 203);
		info.append("y", 102);
		doc.append("info", info);
		
		// créer une collection
		MongoCollection<Document> collection = mDB.getCollection("testCollection");
		collection.insertOne(doc);
		
		// teste l'affichage
		Document query = new Document("name", "MongoDB");
		MongoCursor<Document> cursor = collection.find(query).iterator();
		while(cursor.hasNext())
		{
			System.out.println(cursor.next());
		}
		
		// teste la suppression d'un utilisateur
		query = new Document("name", "MongoDB");
		//collection.deleteMany(requete);	
	}
}
