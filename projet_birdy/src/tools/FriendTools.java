package tools;

import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import bd.Database;

public class FriendTools {
	
	
	/**
	 * Ajoute un ami dans la liste d'amis d'un utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @param friend_id l'identifiant de l'ami à rajouter dans la liste d'amis
	 */
	public static int addFriend(int user_id, int friend_id)
	{	
		int returnedValue = 0;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "INSERT INTO Friend VALUES (" + user_id + "," + friend_id + ");";
			returnedValue = st.executeUpdate(query);
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	
	
	/**
	 * Enlève un ami de la liste d'amis de l'utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @param friend_id l'identifiant de l'ami à enlever de la liste d'amis
	 */
	public static int deleteFriend(int user_id, int friend_id)
	{	
		int returnedValue = 0;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "DELETE FROM Friend"
						 + " WHERE user_id=" + user_id
						 + " AND friend_id=" + friend_id + ";";
			returnedValue = st.executeUpdate(query);
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}
	
	
	/**
	 * Vérifie si un ami existe déjà dans la liste d'amis de l'utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @param friend_id l'identifiant de l'ami
	 * @return true si l'ami existe déjà, false sinon
	 */
	public static boolean friendExists(int user_id, int friend_id)
	{	
		boolean returnedValue = false;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM Friend"
						 + " WHERE user_id=" + user_id
						 + " AND friend_id=" + friend_id + ";";
			ResultSet rs = st.executeQuery(query);
			
			if (rs.next()) returnedValue = true;
			
			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}

	
	/**
	 * Récupère la liste d'amis et leurs informations respectives 
	 * @param user_id l'identifiant de l'utilisateur
	 * @return un JSONArray contenant la liste d'amis et leurs informations respectives 
	 * @throws JSONException
	 */
	public static JSONObject getFriendList(int user_id)
	{
		JSONArray jsonArray = new JSONArray();
		JSONObject finalJson = new JSONObject();
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT username, lastname, firstname FROM Friend,User"
						 + " WHERE User.user_id=Friend.friend_id"
						 + " AND Friend.user_id=" + user_id + ";";
			ResultSet rs = st.executeQuery(query);
			
			// on parcourt la table d'amis
			while (rs.next())
			{
				JSONObject json = new JSONObject();
				json.put("username", rs.getString("username"));
				jsonArray.put(json);
			}
			
			finalJson.put("username", UserTools.getUsername(user_id));
			finalJson.put("liste d'amis", jsonArray);
			
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
