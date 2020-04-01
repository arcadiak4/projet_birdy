package bd;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class DatabaseMongo {
	
	public static MongoDatabase getMongoDBConnection()
	{
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.WARNING); 
		
		@SuppressWarnings("resource")
		MongoClient mongo = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		MongoDatabase mDB = mongo.getDatabase(DBStatic.mongo_bd);
		
		return mDB;
	}
}
