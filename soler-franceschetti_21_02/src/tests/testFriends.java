package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import services.Friend;
import services.User;

class testFriends {

	@Test
	void test() {
		JSONObject userLog = null;
		//on créé des utilisateurs et/ou les connecte :
		if(!User.checkUserExist("People_JUNIT")) {//PEOPLE
			userLog = User.createUser("jUnit", "junit", "People_JUNIT", "password", 20);
		}
		else {
			userLog = User.loginUser("People_JUNIT", "password");
		}
		String keyPe = null;
		try {
			keyPe = userLog.getString("Key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(!User.checkUserExist("Followed_JUNIT")) {//FOLLOWED
			userLog = User.createUser("jUnit", "junit", "Followed_JUNIT", "password", 20);
		}
		else {
			userLog = User.loginUser("Followed_JUNIT", "password");
		}
		String keyFo = null;
		try {
			keyFo = userLog.getString("Key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(!User.checkUserExist("Friend_JUNIT")) {//FRIEND
			userLog = User.createUser("jUnit", "junit", "Friend_JUNIT", "password", 20);
		}
		else {
			userLog = User.loginUser("Friend_JUNIT", "password");
		}
		String keyFr = null;
		try {
			keyFr = userLog.getString("Key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//ajout des amis :
		Friend.addFriend(keyPe, "Followed_JUNIT");//People suit Followed sur le site
		Friend.addFriend(keyPe, "Friend_JUNIT");//People suit Friend sur le site
		Friend.addFriend(keyFr, "People_JUNIT");//Friend suit People sur le site
		//on vérifie que c'est bien le cas en BDD:
		assertTrue(Friend.getFriendLogins("People_JUNIT").contains("Followed_JUNIT"));
		assertTrue(Friend.getFriendLogins("People_JUNIT").contains("Friend_JUNIT"));
		assertTrue(Friend.getFriendLogins("Followed_JUNIT").size()==0);
		assertTrue(Friend.getFriendLogins("Friend_JUNIT").contains("People_JUNIT"));
		assertTrue(Friend.alreadyFriend("People_JUNIT","Followed_JUNIT"));
		assertTrue(Friend.alreadyFriend("People_JUNIT","Friend_JUNIT"));
		assertTrue(Friend.alreadyFriend("Friend_JUNIT","People_JUNIT"));
		
		//retrait amis :
		Friend.removeFriend(keyPe, "Friend_JUNIT");//People retire Friend de ses amis
		assertFalse(Friend.alreadyFriend("People_JUNIT","Friend_JUNIT"));
		assertFalse(Friend.getFriendLogins("People_JUNIT").contains("Friend_JUNIT"));
		
		JSONObject resultat = Friend.searchMyFriends(keyPe, "in", "JUNIT");
		try {
			ArrayList<String> loginsFriends = new ArrayList<>();
			JSONArray friends = resultat.getJSONArray("users");
			for(int i=0; i< friends.length();i++) {
				loginsFriends.add(friends.getJSONObject(i).getString("login"));
			}
			assertTrue(loginsFriends.contains("Followed_JUNIT"));
			assertFalse(loginsFriends.contains("Friend_JUNIT"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.print(resultat);
		
		User.logoutUser(keyPe);//logout
		User.logoutUser(keyFr);
		User.logoutUser(keyFo);
	}

}
