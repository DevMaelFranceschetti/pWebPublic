package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import junit.framework.Assert;
import services.User;

class testUsers {

	@Test
	void test() {//vérifier que "Super_JUNIT" n'est pas dans la BDD avant ce test !
		JSONObject userCreate = null;
		if(!User.checkUserExist("Super_JUNIT")) {//création
			userCreate = User.createUser("jUnit", "junit", "Super_JUNIT", "password", 20);
		}
		else {//login
			userCreate = User.loginUser("Super_JUNIT", "password");
		}
		String key = null;
		try {
			key = userCreate.getString("Key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertTrue(key!=null && key!="");
		assertTrue(User.checkUserLogged("Super_JUNIT"));//vérification que l'user est bien login en BDD
		JSONObject logout = User.logoutUser(key);//logout
		Integer retour = null;
		try {
			retour = logout.getInt("OK");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertTrue(retour!=null && retour == 1);
		assertFalse(User.checkUserLogged("Super_JUNIT"));//vérification que l'user est bien logout en BDD
	}

}
