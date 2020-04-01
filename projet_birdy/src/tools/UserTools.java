package tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.Database;

public abstract class UserTools {
	
	private static int cpt = 1;

	/**
	 * Teste si le nom du nouvel utilisateur existe déjà dans la base de données
	 * @param username le nom de l'utilisateur
	 * @return true si le nom de l'utilisateur existe déjà, false sinon
	 */
	public static boolean userNameExists(String username)
	{
		boolean returnedValue = false;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT username FROM User"
						 + " WHERE username='" + username + "';";
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next())
			{
				if(rs.getString("username") == null) returnedValue = false;
				else returnedValue = true;
			}
			
			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	

	/**
	 * Insère un nouvel utilisateur dans la base de données
	 * @param username le nom de l'utilisateur
	 * @param password le mot de passe de l'utilisateur
	 * @param lastName le nom de famille de l'utilisateur
	 * @param firstName le prénom de l'utilisateur
	 * @param dateOfBirth la date de naissance de l'utilisateur
	 * @param email l'adresse mél de l'utilisateur
	 * @return un entier supérieur à 0 si il a été inséré, 0 sinon
	 */
	public static int insertUser(String username, String password, String lastName, String firstName, String dateOfBirth, String email)
	{
		int user_id = cpt;
		int returnedValue = 0;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "INSERT INTO User VALUES ('" + user_id + "','" + username + "','" + password + "','" + lastName + "','" + firstName + "','" + dateOfBirth + "','" + email + "');";
			returnedValue = st.executeUpdate(query);
			cpt++;
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	
	
	/**
	 * Supprime l'utilisateur de la base de données
	 * @param username le nom de l'utilisateur
	 */
	public static int deleteUser(String username)
	{
		int returnedValue = 0;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "DELETE FROM User"
						 + " WHERE username='" + username + "';";
			returnedValue = st.executeUpdate(query);
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	
	
	/**
	 * Teste si l'utilisateur existe dans la base de données
	 * @param user_id l'identifiant de l'utilisateur
	 * @return true si l'utilisateur existe dans la base de données, false sinon
	 */
	public static boolean userIDExists(int user_id)
	{
		boolean returnedValue = false;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT user_id FROM User"
						 + " WHERE user_id=" + user_id + ";";
			ResultSet rs = st.executeQuery(query);
			
			if(rs.next()) returnedValue = true;
			
			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}

	
	/**
	 * Récupère l'identifiant à partir du nom de l'utilisateur
	 * @param username le nom de l'utilisateur
	 * @return l'identifiant de l'utilisateur, -1 sinon
	 */
	public static int getUserID(String username)
	{
		int returnedValue = -1;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT user_id FROM User"
						 + " WHERE username='" + username + "';";
			ResultSet rs = st.executeQuery(query);
			
			if(!rs.next()) returnedValue = -1;
			else returnedValue = rs.getInt("user_id");
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	
	
	/**
	 * Récupère le username de l'utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @return le username de l'utilisateur
	 */
	public static String getUsername(int user_id)
	{
		String returnedValue = null;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT username FROM User"
						 + " WHERE user_id='" + user_id + "';";
			ResultSet rs = st.executeQuery(query);
			
			if(rs.next()) returnedValue = rs.getString("username");
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	
	
	/**
	 * Récupère les informations d'un utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @return un json contenant les informations de l'utilisateur
	 */
	public static JSONObject getUser(int user_id)
	{
		JSONObject json = new JSONObject();
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM User"
						 + " WHERE user_id=" + user_id + ";";
			ResultSet rs = st.executeQuery(query);
			rs.next();
			
			json.put("username", rs.getString("username"));
			json.put("firstname", rs.getString("firstname"));
			json.put("lastname", rs.getString("lastname"));
			json.put("dateOfBirth", rs.getString("dateOfBirth"));
			json.put("email", rs.getString("email"));
			
			JSONArray jsonArray = FriendTools.getFriendList(user_id).getJSONArray("liste d'amis");;
			json.put("liste d'amis", jsonArray);

			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		return json;
	}


	/**
	 * Récupère la liste des utilisateurs et leurs informations respectives
	 * @return un json contenant la liste des utilisateurs et leurs informations respectives
	 */
	public static JSONObject getUserList()
	{
		JSONObject finalJson = new JSONObject();
		JSONArray jsonArray = null;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM User;";
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next())
			{
				JSONObject json = new JSONObject();
				json.put("username", rs.getString("username"));
				json.put("firstname", rs.getString("firstname"));
				json.put("lastname", rs.getString("lastname"));
				json.put("dateOfBirth", rs.getString("dateOfBirth"));
				json.put("email", rs.getString("email"));
				
				jsonArray = FriendTools.getFriendList(rs.getInt("user_id")).getJSONArray("liste d'amis");
				json.put("liste d'amis", jsonArray);
				
				finalJson.put(rs.getString("user_id"), json);
			}

			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		return finalJson;
	}
	
}
