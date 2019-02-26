package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.Friend;
import services.User;

public class Login  extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request==null || response == null) {
			throw new ServletException("Request Error");
		}
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		JSONObject state= User.loginUser(login,password);
		
		PrintWriter out = response.getWriter();
		out.print(state);
	}

}