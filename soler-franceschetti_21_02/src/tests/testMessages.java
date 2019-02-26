package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import services.Message;
import services.User;

class testMessages {

	@Test
	void test() {
		JSONObject userCreate = null;
		if(!User.checkUserExist("Msg_JUNIT")) {//création
			userCreate = User.createUser("jUnit", "junit", "Msg_JUNIT", "password", 20);
		}
		else {//login
			Message.deleteAllMessages("Msg_JUNIT");//on supprime tous les anciens messages de Msg_JUNIT pour ne pas fausser le test
			userCreate = User.loginUser("Msg_JUNIT", "password");
		}
		String key = null;
		try {
			key = userCreate.getString("Key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//on vérifie qu'il n'y a bien aucun messages :
		ArrayList<String> logins = new ArrayList<>();
		logins.add("Msg_JUNIT");
		JSONObject searchMessage = Message.listMessages(null, null, logins, null, null);//on liste les messages postés par Msg_JUNIT
		assertTrue(searchMessage.length()==0);
		
		//Msg_JUNIT écrit un message :
		Message.addComment(key, "Test Junit d'insertion de message");	
		searchMessage = Message.listMessages("", "", logins, null, null);
		assertTrue(searchMessage.length()>0);//on vérifie qu'il y a bien un message
		String text = "";
		try {//on vérifie que le texte de ce message correspond bien
			JSONArray messages = searchMessage.getJSONArray("messages");
			System.out.println(searchMessage);
			for(int i=0; i< messages.length();i++) {
				JSONObject unMessage = new JSONObject(messages.getString(i));
				text = unMessage.getString("message");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertEquals(text,"Test Junit d'insertion de message");
		
	}
	

}