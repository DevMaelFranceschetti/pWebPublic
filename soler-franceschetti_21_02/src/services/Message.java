package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.json.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Message {
	
	public static JSONObject addComment(String key, String text) {
		JSONObject json = new JSONObject();		
		if(key==null||text==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			String login = "";
			String nom ="";
			String prenom = "";
			if(User.keyExist(key)) {
				login = User.getLogin(key);
				JSONObject infos = User.getInfos(login);
				try {
					nom = infos.getString("nom");
					prenom = infos.getString("prenom");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				MongoDatabase mydb = tools.MongoBase.connect();
				MongoCollection<Document> c = mydb.getCollection("Messages");
				Document insert = new Document();
				insert.append("msgID", tools.DataBase.generateKey());
				insert.append("login", login);
				insert.append("nom", nom);
				insert.append("prenom", prenom);
				insert.append("date", new Date());
				insert.append("message", text);
				c.insertOne(insert);
				try {
					json.put("OK", 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}
	
	public static JSONObject deleteComment(String key, String msgID) {
		JSONObject json = new JSONObject();		
		if(key==null||msgID==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			String login = "";
			if(User.keyExist(key)) {
				login = User.getLogin(key);
				MongoDatabase mydb = tools.MongoBase.connect();
				MongoCollection<Document> c = mydb.getCollection("Messages");
				Document remove = new Document();
				remove.append("login", login);
				remove.append("msgID", msgID);
				c.deleteOne(remove);
				try {
					json.put("OK", 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}
	
	public static JSONObject editComment(String key, String msgID, String text) {
		JSONObject json = new JSONObject();
		if(key==null||msgID==null||text==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			//BDD
			try {
				json.put("TestM", 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public static JSONObject searchMessage(String key, String login, String nom, String prenom , boolean friend, String dateDeb, String dateF) {
		JSONObject json = new JSONObject();
		Date dateDebut = null;
		Date dateFin = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
        	if(dateDeb!=null&&dateDeb!="") {
    			dateDebut = sdf.parse(dateDeb);
    		}
    		if(dateF!=null&&dateF!="") {
    			dateFin = sdf.parse(dateF);
    		}
        }
        catch (ParseException pe)
        {
        	try {
				json.put("Error", -2);
			} catch (JSONException e) {
				e.printStackTrace();
			}
            pe.printStackTrace();
        }

		if(key==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			if(User.keyExist(key)) {
				List<String> friendLogins = new ArrayList<String>();
				String myLogin = User.getLogin(key);
				if((login == null || login == "")&&friend) {//si on a pas spécifié de login mais qu'on recherche dans les amis
					friendLogins = Friend.getFriendLogins(myLogin);//on récupère la liste des amis de 'myLogin' (celui qui fait la recherche)
				}
				else {
					if(login!=null && login!="") {//si on recherche une personne particulière (on ne tient pas compte de "friend" dans ce cas
						friendLogins.add(login);//sinon si login est spécifié on s'intéresse à cette personne
					}
				}
				System.err.println(friendLogins);
				return listMessages(nom, prenom, friendLogins, dateDebut, dateFin);
			}
			try {
				json.put("TestM", 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public static JSONObject listMessages(String nom, String prenom, List<String> logins, Date dateDebut, Date dateFin) {
		JSONObject json = new JSONObject();	
		MongoDatabase mydb = tools.MongoBase.connect();
		MongoCollection<Document> c = mydb.getCollection("Messages");
		Document search = new Document();
		if(nom !=null && nom !="") {
			search.append("nom", nom);
		}
		if(prenom !=null && prenom !="") {
			search.append("prenom", prenom);
		}
		if(logins !=null && logins.size()>0) {
			Document login = new Document("$in", logins);
			search.append("login", login);
		}
		if(dateDebut !=null) {
			search.append("date", new Document("$gt", dateDebut));
		}
		if(dateFin !=null) {
			search.append("date", new Document("$lt", dateFin));
		}
		MongoCursor<Document> cursor = c.find(search).iterator();
		while(cursor.hasNext()) {
			Document d = cursor.next();
			JSONObject msg = new JSONObject();
			try {
				String message = d.getString("message");
				String msgID = d.getString("msgID");
				String login = d.getString("login");
				String nom2 = d.getString("nom");
				String prenom2 = d.getString("prenom");
				Date date = d.getDate("date");
				message = message.replaceAll("'", "\'");//Au cas où il y a des ' ou des "
				
				msg.put("msgID", msgID); 
				msg.put("login", login);
				msg.put("nom", nom2);
				msg.put("prenom", prenom2);
				msg.put("date", date);
				msg.put("message", message);
				
				/*  //Version de début pour éliminer le PB des String dans des tableaux fictifs : 
				String jsonObjectString = "{\"nom\":\""+nom2+"\",\"prenom\":\""+prenom2+"\",\"msgID\":\""+msgID+"\",\"login\":\""+login+"\",\"message\":\""+message+"\",\"date\":\""+date+"\"}";
				JSONObject jo = new JSONObject(jsonObjectString);
				*/
				
				json.append("messages", msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public static JSONObject deleteAllMessages(String login) {
		JSONObject json = new JSONObject();	
		MongoDatabase mydb = tools.MongoBase.connect();
		MongoCollection<Document> c = mydb.getCollection("Messages");
		Document d = new Document();
		d.append("login", login);
		c.deleteMany(d);
		try {
			json.put("OK", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject flushMessages() {//DEV ONLY
		JSONObject json = new JSONObject();	
		MongoDatabase mydb = tools.MongoBase.connect();
		MongoCollection<Document> c = mydb.getCollection("Messages");
		c.deleteMany(new Document());
		try {
			json.put("OK", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
}
