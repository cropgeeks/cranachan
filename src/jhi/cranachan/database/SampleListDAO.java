package jhi.cranachan.database;

import java.sql.*;
import java.util.*;

import javax.annotation.*;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.sql.*;

import jhi.cranachan.data.*;

@Named
@ApplicationScoped
public class SampleListDAO
{
	private static final String INSERT_LIST = "INSERT INTO sample_lists (id, name, sample_list) VALUES (?, ?, ?)";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public SampleListDAO()
	{
	}

	public String addList(String name, String list)
	{
		String uuid = UUID.randomUUID().toString();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createInsertStatement(con, INSERT_LIST, uuid, name, list))
		{
			stmt.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return uuid;
	}
}
