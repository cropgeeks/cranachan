package jhi.cranachan.database;

import java.sql.*;

public class DatabaseUtils
{
	public static PreparedStatement createByIdStatement(Connection con, String query, String id)
		throws SQLException
	{
		// Prepare statement with ID
		PreparedStatement statement = con.prepareStatement(query);
		statement.setString(1, id);

		return statement;
	}
}