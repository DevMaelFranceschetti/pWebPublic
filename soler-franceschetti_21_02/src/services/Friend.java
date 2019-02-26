package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.*;

import tools.DataBase;

public class Friend {

	static String createFriendSQL = "INSERT INTO friend(login, followed) VALUES( ? , ? )";
	static String deleteFriendSQL = "DELETE FROM friend WHERE login = ? AND followed = ?";
	static String searchMyFriendSQL = "SELECT U.nom, U.prenom, U.login, U.age FROM user as U, friend as F WHERE F.login = ? AND U.login = F.followed AND F.followed LIKE ?";
	static String searchNotMyFriendSQL = "SELECT U.nom, U.prenom, U.login, U.age FROM user as U WHERE U.login LIKE ? AND U.login NOT IN (SELECT F.followed FROM friend F WHERE F.login = ? )";
	static String searchUserSQL = "SELECT U.nom, U.prenom, U.login, U.age FROM user as U WHERE U.login LIKE ?";
	static String checkFriendSQL = "SELECT login FROM friend WHERE login = ? AND followed = ?";
	static String getFriendLoginsSQL = "SELECT followed FROM friend WHERE login = ?";
	static PreparedStatement stm;
	
	public static JSONObject addFriend(String key, String friendID) {
		JSONObject json = new JSONObject();
		if(key==null||friendID==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			String login = User.getLogin(key);
			if(!alreadyFriend(login, friendID)&&friendID!=login) {
				Connection c = DataBase.connect();
				try {
					stm =  c.prepareStatement(createFriendSQL);
					stm.setString(1, login);
					stm.setString(2, friendID);
					stm.executeUpdate();
					stm.close();
					c.close();	
				} catch (SQLException e) {
					e.printStackTrace();
					try {
						json.put("Error", -403);
						return json;
					} catch (JSONException js) {
						js.printStackTrace();
					}
				}
			}
			try {
				json.put("OK", 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public static JSONObject removeFriend(String key, String friendID)  {
		JSONObject json = new JSONObject();
		if(key==null||friendID==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			String login = User.getLogin(key);
			Connection c = DataBase.connect();
			try {
				stm =  c.prepareStatement(deleteFriendSQL);
				stm.setString(1, login);
				stm.setString(2, friendID);
				stm.executeUpdate();
				stm.close();
				c.close();	
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					json.put("Error", -403);
					return json;
				} catch (JSONException js) {
					js.printStackTrace();
				}
			}
			try {
				json.put("OK", 1);			try {
					json.put("OK", 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public static JSONObject searchMyFriends(String key, String query, String friend) {
		if(User.keyExist(key)) {
			String login = User.getLogin(key);
			return searchFriends(login,query,friend);
		}
		else {
			JSONObject json = new JSONObject();
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}
	
	public static JSONObject searchFriends(String login, String query, String friend) {
		JSONObject json = new JSONObject();
		if(login==null||friend==null) {
			try {
				json.put("Error", -1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			Connection c = DataBase.connect();
			try {
				switch(query) {
					case "in"://dans les amis
						stm =  c.prepareStatement(searchMyFriendSQL);
						stm.setString(1, login);
						stm.setString(2, "%"+friend+"%");
						break;
					case "out"://dans les non-amis
						stm =  c.prepareStatement(searchNotMyFriendSQL);
						stm.setString(1, "%"+friend+"%");
						stm.setString(2, login);
						break;
					default ://dans tous
						stm =  c.prepareStatement(searchUserSQL);
						stm.setString(1, "%"+friend+"%");
						break;
				}
				ResultSet res = stm.executeQuery();
				json = filtrerInfosFriend(res);
				stm.close();
				c.close();	
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					json.put("Error", -405);
					return json;
				} catch (JSONException js) {
					js.printStackTrace();
				}
			}
		}
		return json;
	}

	private static JSONObject filtrerInfosFriend(ResultSet s) {
		JSONObject json =new JSONObject();
		try {
			while(s.next())
			{
				JSONObject user =new JSONObject();
				user.put("nom", s.getString("nom"));
				user.put("prenom", s.getString("prenom"));
				user.put("login", s.getString("login"));
				user.put("age", s.getString("age"));
				json.append("users",user); //old: json.put("user"+String.valueOf(indice) , user);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
			try {
				json.put("Error", -407);
				return json;
			} catch (JSONException js) {
				js.printStackTrace();
			}
		}
		return json;
	}
	
	public static boolean alreadyFriend(String login, String followed) {
		Connection c = DataBase.connect();
		try {
			stm =  c.prepareStatement(checkFriendSQL);
			stm.setString(1, login);
			stm.setString(2, followed);
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
	
	public static ArrayList<String> getFriendLogins(String login){
		Connection c = DataBase.connect();
		ArrayList<String> l =new ArrayList<String>();
		try {
			stm =  c.prepareStatement(getFriendLoginsSQL);
			stm.setString(1, login);
			ResultSet s = stm.executeQuery();
			while(s.next())
			{
				l.add(s.getString("followed"));
			}
		} catch (SQLException e ) {
			e.printStackTrace();
		}
		return l;
	}


	
}
