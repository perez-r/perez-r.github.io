/*
 * FILENAME: ZooDB.java
 *
 * DESCRIPTION: 
 * 		Provides the program with 
 * 		access to the MongoDB Database.
 * 		Currently only accesses one table
 * 		from database but can be modified 
 * 		to access other tables in DB.
 * 		Constructor generates new ZooDB
 * 		object providing access to MongoDB.
 * 
 * PUBLIC METHODS: 
 * 		String		getHash
 * 		boolean 	userExists
 * 		String 		getLevel
 * 		boolean 	addNewUser
 * 		boolean		updateUser
 * 		boolean		deleteUser
 * 
 * AUTHOR INFO: 		
 * 		ORGANIZATION: 	Southern New Hampshire University
 * 		COURSE: IT-145 Foundations in Application Development
 * 		
 * 		INSTRUCTOR: Joe Parker
 * 		
 * 		STUDENT: 	Ruben Perez		START DATE: 		04/01/2022
 * 
 */
package com.snhu;

//MongoDB Driver 
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonReader;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public class ZooDB {
	MongoClient mongoClient;
	MongoDatabase database;
	static MongoCollection<Document> collection;
	String key;
	
	// Default Constructor 
	ZooDB() {
		String uri = "mongodb+srv://ruben_perez:8wxvo9KymYpSUdpc@zoocluster.srtdw.mongodb.net/zooDB?retryWrites=true&w=majority";
        
		mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("zoodb");
        collection = database.getCollection("zoousers");
        key = "portfolio";
	}
	
	/*
	 * GetHash function
	 * 
	 * Returns the hashed password 
	 * stored on the database. Program 
	 * never stores the unencrypted 
	 * password for security purposes. 
	 * 
	 */
	public String getHash(String username) {
		String hashed = null;
		
		try {
			Bson projection = Projections.fields(
					Projections.include("hash"), 
					Projections.excludeId());
			Document doc = collection.find(eq("username", username))
					.projection(projection)
					.first();
			if (doc == null) {
				System.out.println("No results found.");
			}
			else {
				JsonReader jsonReader = new JsonReader(doc.toJson());
		    	jsonReader.readStartDocument();
		    	
		    	jsonReader.readName("hash");
		    	hashed = jsonReader.readString();
				
		    	return hashed;
			}
		} catch (MongoException me) {
			System.err.println("Error encountered: " + me);
		}
		return hashed;
		
	}
	
	/*
	 * UserExists Function
	 * 
	 * Calls on database to check if
	 * a user exists by the supplied 
	 * username.
	 */
	public boolean userExists(String username) {
		if (collection.find(eq("username", username)).first() == null) {
			return false;
		}
		else {
			return true;
		}
	}

	/*
	 * Get Access Level
	 * 
	 * Returns the access level of the 
	 * user for authorization
	 * purposes. 
	 */
	public String getLevel(String username) {
		String level = "";
		
		Bson projection = Projections.fields(Projections.include("level"),Projections.excludeId());
		
		Document doc = collection.find(eq("username", username))
				.projection(projection)
				.first();
		
		if (doc == null) {
			System.out.println("No results found.");
		}
		else {
			JsonReader jsonReader = new JsonReader(doc.toJson());
			jsonReader.readStartDocument();
			
			jsonReader.readName("level");
			level = jsonReader.readString();
			
			System.out.println(level);
		}		
		return level;
	}
	
	/*
	 * Add New User Function
	 * 
	 * Interacts with DB to add a new 
	 * user using the supplied information in
	 * the function arguments. 
	 * 
	 */
	public boolean addNewUser(String username, String password, String level) {
		/*
		 * START XOR ENCRYPTION
		 */
		Encryption encrypt = new Encryption();
		String hashed = encrypt.encryptDecrypt(password, key);
		/*
		 * END XOR ENCRYPTION
		 */
		
		try {
			InsertOneResult result = collection.insertOne(new Document()
					.append("username", username)
					.append("level", level)
					.append("hash", hashed));
			return result.wasAcknowledged();
		} catch (MongoException me) {
			System.err.println("Unable to insert due to an error: " + me);
			return false;
		}
	}

	/*
	 * Update User Function
	 * 
	 * Interacts with DB to update the 
	 * user data using the supplied information 
	 * in the function arguments 
	 * 
	 */
	public boolean updateUser(String username, String password, String level) {

		Document query = new Document().append("username", username);
		/*
		 * START XOR ENCRYPTION
		 */
		Encryption encrypt = new Encryption();
		String hashed = encrypt.encryptDecrypt(password, key);
		/*
		 * END XOR ENCRYPTION
		 */
		
		Bson updates = Updates.combine(
				Updates.set("hash", hashed),
				Updates.set("level", level));
		UpdateOptions options = new UpdateOptions().upsert(true);
		
		try {
			UpdateResult result = collection.updateOne(query, updates, options);
			
			System.out.println("Modified document count: " + result.getModifiedCount());
			
			return result.wasAcknowledged();
		} catch (MongoException me) {
			System.err.println("Unable to update due to an error: " + me);
			return false;
		}
		
	}

	/*
	 * Delete User Function
	 * 
	 * Interacts with DB to delete the user 
	 * specified in the function arguments. 
	 * 
	 */
	public boolean deleteUser(String username) {
		Bson query = eq("username", username);
		
		try {
			DeleteResult result = collection.deleteOne(query);
			System.out.println("Deleted document count: " + result.getDeletedCount());
			return result.wasAcknowledged();
		} catch (MongoException me) {
			return false;
		}
	}
}
