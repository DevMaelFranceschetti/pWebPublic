package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.Friend;
import services.Message;

public class AddMsg  extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request==null || response == null) {
			throw new ServletException("Request Error");
		}
		
		String key = request.getParameter("key");
		String message = request.getParameter("message");
		
		JSONObject state= Message.addComment(key,message);
		
		PrintWriter out = response.getWriter();
		out.print(state);
	}

}