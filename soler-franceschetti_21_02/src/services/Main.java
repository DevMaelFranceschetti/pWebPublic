package services;

import java.util.Date;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mysql.jdbc.Messages;

public class Main {

	public static void main(String[] args) {
		//Message.flushMessages();
		String keyt="";
		try {
			keyt = User.loginUser("N_0", "TEST_0").getString("Key");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject testUnpermittedDelete = Message.deleteComment(keyt,"69bf5c4a-8f61-48c8-a366-a34ccf0851a5");
		System.out.println(testUnpermittedDelete);
		Message.deleteComment(keyt,"ee55985b-eb5a-4a18-a065-e0961bd4c093");
		JSONObject msgs = Message.searchMessage( keyt, "N_0", "", "" , false, "", "");
		System.out.println(msgs);
	/*	for(int i = 0; i<10; i++) {
			//JSONObject key = User.createUser("test"+i, "numero_"+i, "N_"+i, "TEST_"+i, i);
			JSONObject key = User.loginUser("N_"+i, "TEST_"+i);
			String clee="";
			try {
				clee = key.getString("Key");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Message.addComment(clee, "test numero "+i);
			//Message.deleteAllMessages("N_"+i);
			
			if(i>1) {
				Friend.addFriend(clee, "N_"+(i-1));
			}
			//Friend.removeFriend(clee, "N_0");
			JSONObject liste = Friend.searchMyFriends(clee, "in", "");
		    System.out.println("N_"+i+"  -> "+liste);/*
			JSONObject liste2 = Friend.searchMyFriends(clee, "out", "");
			System.out.println(liste2);
			JSONObject liste3 = Friend.searchMyFriends(clee, "", "");
			System.out.println(liste3);
			
			User.logoutUser("N_"+i);
			
		}*/
	}

}
