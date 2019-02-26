package servlet;

import org.json.*;

import services.User;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateUser extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request==null || response == null) {
			throw new ServletException("Request Error");
		}
		String name = request.getParameter("name");
		String forname = request.getParameter("forname");
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		int age = Integer.parseInt(request.getParameter("age"));
		
		JSONObject state= User.createUser( name, forname, login,password, age);
		
		PrintWriter out = response.getWriter();
		out.print(state);
	}

}
