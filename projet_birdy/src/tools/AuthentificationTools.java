package tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import bd.Database;

public class AuthentificationTools {
	

	/**
	 * Temps maximal (en milliseconde) avant qu'un utilisateur perde son token d'authentification
	 */
	public static final int SESSION_TIME_THRESHOLD = 900000; // 900000 = 15min - 30000/2 = 15s
	
	
	/**
	 * Teste si le login et le mot de passe rentrés par l'utilisateur sont corrects
	 * @param username le nom de l'utilisateur
	 * @param password le mot de passe de l'utilisateur
	 * @return true si le mot de passe est correct, false sinon
	 */
	public static boolean checkPassword(String username, String password)
	{
		boolean returnedValue = false;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM User"
						 + " WHERE username='" + username
						 + "' AND password='" + password + "';";
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
	 * Ouvre une nouvelle session, l'utilisateur obtient un jeton d'authentification
	 * @param user_id l'identifiant de l'utilisateur
	 * @return le token, null sinon
	 */
	public static String openSession(int user_id)
	{
		String returnedValue = null;
		String token = UUID.randomUUID().toString().replace("-", "");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Timestamp actualTime = new Timestamp(System.currentTimeMillis());
		String lastTokenTimestamp = dateFormat.format(actualTime);

		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "INSERT INTO Authentification VALUES ('" + user_id + "','" + lastTokenTimestamp + "','" + token + "');";
			if(st.executeUpdate(query) > 0) returnedValue = token;
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}
	
	
	/**
	 * Ferme la session, l'utilisateur perd son jeton d'authentification
	 * @param token le token d'authentification
	 * @return un entier supérieur à 0 si la fermeture de session a réussi, 0 sinon
	 */
	public static int closeSession(String token)
	{
		int returnedValue = 0;
		
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "DELETE FROM Authentification"
						 + " WHERE token='" + token + "';";
			returnedValue = st.executeUpdate(query);
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}
	
	
	/**
	 * Vérifie si l'utilisateur possède encore son token d'authentification ou si le délai est passé
	 * @param token le token d'authentification
	 * @return true si le délai est dépassé, false sinon
	 */
	public static boolean isValidToken(String token)
	{
		boolean returnedValue = true;
		
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT lastTokenTimestamp FROM Authentification"
						 + " WHERE token='" + token + "';";
			// on récupère le timestamp du token
			ResultSet rs = st.executeQuery(query);
			if(!rs.next()) throw new NullPointerException();	
			String timestamp = rs.getString("lastTokenTimestamp");
			
			// affichage
			System.out.println("Dernier timestamp = " + timestamp);
			
			// on convertit de String en Timestamp
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
			Date parsedDate = dateFormat.parse(timestamp);
			Timestamp lastTokenTimestamp = new java.sql.Timestamp(parsedDate.getTime());
			
			// on récupère le temps actuel
			Timestamp actualTime = new Timestamp(System.currentTimeMillis());
			
			// SI le délai (en millisecondes) de connexion est dépassé
			long duration = actualTime.getTime() - lastTokenTimestamp.getTime();
			String elapsedTime = new SimpleDateFormat("mm:ss:SSS").format(new Date(duration));
			System.out.println(elapsedTime + "ms écoulées depuis la dernière session\n");
			if(duration > SESSION_TIME_THRESHOLD) returnedValue = false;		

			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}
	
	
	/**
	 * Vérifie si l'utilisateur existe dans la base de données des sessions ouvertes
	 * @param user_id l'identifiant de l'utilisateur
	 * @return true si il existe, false sinon
	 */
	public static boolean userIDExists(int user_id)
	{
		boolean returnedValue = false;
		
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT user_id FROM Authentification"
						 + " WHERE user_id='" + user_id + "';";
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
	 * Récupère le token à partir de l'identifiant de l'utilisateur
	 * @param user_id l'identifiant de l'utilisateur
	 * @return le token d'authentification, null sinon
	 */
	public static String getToken(int user_id)
	{
		String returnedValue = null;
		
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT token FROM Authentification"
						 + " WHERE user_id=" + user_id + ";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next()) returnedValue = rs.getString("token");
			
			rs.close();
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}

	
	/**
	 * Récupère l'identifiant de l'utilisateur connecté
	 * @param token le token d'authentification de l'utilisateur
	 * @return l'identifiant de l'utilisateur connecté
	 */
	public static int getUserID(String token)
	{
		int returnedValue = -1;
		
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT user_id FROM Authentification"
						 + " WHERE token='" + token + "';";
			ResultSet rs = st.executeQuery(query);
			if(rs.next()) returnedValue = rs.getInt("user_id");
			
			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}


	/**
	 * Vérifie si le token d'authentification existe dans la BDD des sessions ouvertes
	 * @param token le token d'authentification
	 * @return true si il existe, false sinon
	 */
	public static boolean tokenExists(String token)
	{
		boolean returnedValue = false;
		
		// SI le token est vide
		if (token == null || token.trim().length() == 0)
			return returnedValue;
		
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT token FROM Authentification"
						 + " WHERE token='" + token + "';";
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
	 * Met à jour le timestamp du token 
	 * @param token le token d'authentification
	 * @return true si la modificaiton a réussi, false sinon
	 */
	public static boolean updateSession(String token)
	{
		boolean returnedValue = false;
		try {
			Connection c = Database.getMySQLConnection();
			Statement st = c.createStatement();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
			Timestamp actualTime = new Timestamp(System.currentTimeMillis());
			String lastTokenTimestamp = dateFormat.format(actualTime);

			String query = "UPDATE Authentification" + 
						   " SET lastTokenTimestamp='" + lastTokenTimestamp + "'" + 
						   " WHERE token='" + token + "';";
			if(st.executeUpdate(query) > 0) returnedValue = true;

			st.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnedValue;
	}

}
