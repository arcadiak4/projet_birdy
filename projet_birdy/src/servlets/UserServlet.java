package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.User;
/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet()
    {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		/*
		 * response.getWriter().append("Served at: ").append(request.getContextPath());
		 * response.getWriter().println(request.getParameter("login"));
		 * response.getWriter().println(request.getParameter("pwd"));
		 */
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		JSONObject json = User.signUp(request.getParameter("username"),
									  request.getParameter("password"),
									  request.getParameter("lastName"),
									  request.getParameter("firstName"),
									  request.getParameter("dateOfBirth"),
									  request.getParameter("email")
									 );

		response.getWriter().println(json.toString());
		response.getWriter().println("le post a marché");
	}

}
