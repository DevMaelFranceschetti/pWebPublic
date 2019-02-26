package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.Message;

public class ListMsg  extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request==null || response == null) {
			throw new ServletException("Request Error");
		}
		
		String key = request.getParameter("key");
		String login = request.getParameter("login");
		String nom = request.getParameter("nom");
		String prenom = request.getParameter("prenom");
		String dateDebut = request.getParameter("dateDebut");
		String dateFin = request.getParameter("dateFin");
		boolean friend = request.getParameter("friend")=="1"?true:false;
		
		JSONObject state= Message.searchMessage(key, login, nom, prenom, friend, dateDebut,dateFin);
		
		PrintWriter out = response.getWriter();
		out.print(state);
	}

}