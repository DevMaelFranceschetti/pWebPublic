package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class DataBase {
	static final String url="jdbc:mysql://localhost/teamPorto";
	static final String login="root";
	static final String password = "root";
	
	public static Connection connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection(url, login, password);
			return c;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String generateKey() {
		return  UUID.randomUUID().toString();
	}
	
	
}
