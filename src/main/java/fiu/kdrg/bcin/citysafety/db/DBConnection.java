package fiu.kdrg.bcin.citysafety.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	public static Connection getConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception ex) {
			System.out.println("Cannot Load Driver!");
			return null;
		}

		try {
			return DriverManager.getConnection(
					"jdbc:mysql://rescue.cs.fiu.edu:33061/bcin", "hadoop",
					"zwb");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) {

		Connection conn = DBConnection.getConnection();
		System.out.println(conn);

	}

}
