package tools;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;


public class MongoBase {
	
	static final String host="localhost";
	static final int port = 27017;
	static final String dbName = "TeamPortoMongo";
	static final String login="root";
	static final String password = "root";
	
	public static MongoDatabase connect() {
		MongoClient mongo = new MongoClient();
		MongoDatabase database = mongo.getDatabase(dbName);
		return database;
	}
	
	
}
