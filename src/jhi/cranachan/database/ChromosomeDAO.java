package jhi.cranachan.database;

import jhi.cranachan.data.*;

import javax.annotation.*;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.sql.*;
import java.sql.*;
import java.util.*;

@Named
@ApplicationScoped
public class ChromosomeDAO
{
	private static final String CHROMOSOME_BY_ID = "SELECT * FROM refseqs WHERE ID = ?";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public ChromosomeDAO()
	{
	}

	public Chromosome getById(String id)
	{
		Chromosome chromosome = new Chromosome();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, CHROMOSOME_BY_ID, id);
			 ResultSet resultSet = stmt.executeQuery())
		{
			if (resultSet.next())
				chromosome = getChromosome(resultSet);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return chromosome;
	}

	private Chromosome getChromosome(ResultSet resultSet)
		throws SQLException
	{
		Chromosome chromosome = new Chromosome();

		chromosome.setId(resultSet.getInt("id"));
		chromosome.setDoi(resultSet.getString("doi"));
		chromosome.setName(resultSet.getString("name"));
		chromosome.setLength(resultSet.getInt("length"));
		chromosome.setCreatedOn(resultSet.getDate("created_on"));
		chromosome.setUpdatedOn(resultSet.getDate("updated_on"));

		return chromosome;
	}
}
