package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.json.*;

import tools.DataBase;

public class User {
	
	static String createUserSQL = "INSERT INTO user(nom, prenom, login, password,age) VALUES (? , ? , ? , ?, ?)";
	static String checkUserSQL = "SELECT login FROM user WHERE login = ?";
	static String checkLoggedSQL = "SELECT login FROM connect WHERE login = ?";
	static String loginUserSQL = "SELECT login FROM user WHERE login = ? AND password = ?";
	static String loginKeySQL = "INSERT INTO connect(login, userkey) VALUES (?,?)";
	static String getKeySQL = "SELECT userkey FROM connect WHERE login = ?";
	static String deleteKeySQL = "DELETE FROM connect WHERE login = ?";
	static String keyExistSQL = "SELECT login FROM connect WHERE userkey = ? ";
	static String getUserLoginSQL = "SELECT login FROM connect WHERE userkey = ? ";
	static String getInfosSQL = "SELECT nom, prenom, age FROM user WHERE login = ?";
	static PreparedStatement stm;
	
	//TODO : supprimr un USER
	
	public static JSONObject createUser(String name, String forname, String login, String password, int age) {//OK
		JSONObject json = new JSONObject();
		if(name==null||forname==null||login==null||password==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			Connection c = DataBase.connect();
			try {
				if(!checkUserExist(login)) {
					stm =  c.prepareStatement(createUserSQL);
					stm.setString(1, name);
					stm.setString(2, forname);
					stm.setString(3, login);
					stm.setString(4, password);
					stm.setInt(5, age);
					stm.executeUpdate();
					stm.close();
					c.close();	
					json.put("OK", 1);
				}
				else {
					try {
						json.put("Error", -300);
						return json;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}			
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			json = loginUser(login, password);
		}
		return json;
	}
	
	public static JSONObject loginUser(String login, String password) {//OK
		JSONObject json = new JSONObject();
		if(login==null||password==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			Connection c = DataBase.connect();
			try {
				if(checkUserExist(login)) {
					stm =  c.prepareStatement(loginUserSQL);
					stm.setString(1, login);
					stm.setString(2, password);
					ResultSet res = stm.executeQuery();
					if(res.next()) {
						String key = generateKey(login);//générer la clée
						if(!checkUserLogged(login)) {
							insertKey(login, key);//on insère la nouvelle clée en BDD pour cet User
						}
						try {
							json.put("Key", key);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					try {
						json.put("Error", -2);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				stm.close();
				c.close();	
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public static JSONObject logoutUser(String key) {//OK
		JSONObject json = new JSONObject();
		if(key==null) {
			try {
				json.put("Error", -1);
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			if(keyExist(key)) {
				String login = getLogin(key);
				if(checkUserLogged(login)) {
					Connection c = DataBase.connect();
					try {
						stm =  c.prepareStatement(deleteKeySQL);
						stm.setString(1, login);
						stm.executeUpdate();
						stm.close();
						c.close();	
						try {
							json.put("OK", 1);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} catch (SQLException e) {
						try {
							json.put("Error", -3);
							return json;
						} catch (JSONException j) {
							j.printStackTrace();
						}
						e.printStackTrace();
					}
				}	
			}
			else {
				try {
					json.put("Error", -1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}

	public static boolean checkUserExist(String login) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(checkUserSQL);
			stm.setString(1, login);
			ResultSet res = stm.executeQuery();
			if(res.next()) {
				stm.close();
				c.close();	
				return true;
			}
			stm.close();
			c.close();	
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static void insertKey(String login, String key) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(loginKeySQL);
			stm.setString(1, login);
			stm.setString(2, key);
			stm.executeUpdate();
			stm.close();
			c.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkUserLogged(String login) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(checkLoggedSQL);
			stm.setString(1, login);
			ResultSet res = stm.executeQuery();
			if(res.next()) {
				stm.close();
				c.close();	
				return true;
			}
			stm.close();
			c.close();	
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static String getKey(String login) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(getKeySQL);
			stm.setString(1, login);
			ResultSet res = stm.executeQuery();
			if(res.first()) {
				String result = res.getString("userkey");
				stm.close();
				c.close();	
				return result;
			}
			stm.close();
			c.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String generateKey(String login) {
		if(checkUserLogged(login)) {
			String key = getKey(login);
			if(key!=null && key!="") {
				return key;
			}
		}
		while(true) {
			String generated = DataBase.generateKey();
			if(!keyExist(generated)) {
				return generated;
			}
		}
	}
	
	public static boolean keyExist(String key) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(keyExistSQL);
			stm.setString(1, key);
			ResultSet res = stm.executeQuery();
			if(res.first()) {
				stm.close();
				c.close();	
				return true;
			}
			stm.close();
			c.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getLogin(String key) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(getUserLoginSQL);
			stm.setString(1, key);
			ResultSet res = stm.executeQuery();
			if(res.next()) {
				String s = res.getString("login");
				stm.close();
				c.close();	
				return s;
			}
			stm.close();
			c.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static JSONObject getInfos(String login) {
		JSONObject json = new JSONObject();
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(getInfosSQL);
			stm.setString(1, login);
			ResultSet res = stm.executeQuery();
			if(res.next()) {
				json.put("nom", res.getString("nom"));
				json.put("prenom", res.getString("prenom"));
				json.put("age", res.getInt("age"));
				stm.close();
				c.close();	
				return json;
			}
			stm.close();
			c.close();	
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
}
