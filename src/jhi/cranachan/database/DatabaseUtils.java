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

	public static PreparedStatement createInsertStatement(Connection con, String query, Object... values)
			throws SQLException
	{
		PreparedStatement stmt = con.prepareStatement(query);

		if(values != null)
		{
			int i = 1;
			for (Object value : values)
			{
				stmt.setString(i++, value.toString());
			}
		}

		return stmt;
	}
}