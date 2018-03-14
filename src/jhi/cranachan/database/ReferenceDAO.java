package jhi.cranachan.database;

import java.util.*;
import java.sql.*;
import javax.annotation.*;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.sql.*;

import jhi.cranachan.data.*;

@Named
@ApplicationScoped
public class ReferenceDAO
{
	private static final String REFERENCE_BY_ID = "SELECT * FROM refseqsets WHERE ID = ?";
	private static final String CHROMOSOMES_FOR_REFERENCE = "SELECT * FROM refseqs WHERE refseqs.refseqset_id = ?";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public ReferenceDAO()
	{
	}

	public Reference getById(String id)
	{
		Reference reference = new Reference();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, REFERENCE_BY_ID, id);
			 ResultSet resultSet = stmt.executeQuery())
		{
			if (resultSet.next())
				reference = getReference(resultSet);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (reference != null)
		{
			try (Connection con = ds.getConnection();
				 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, CHROMOSOMES_FOR_REFERENCE, id);
				 ResultSet resultSet = stmt.executeQuery())
			{
				List<Chromosome> chromosomes = new ArrayList<>();
				while (resultSet.next())
					chromosomes.add(getChromosome(resultSet));

				reference.setChromosomes(chromosomes);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return reference;
	}

	private Reference getReference(ResultSet resultSet)
		throws SQLException
	{
		Reference reference = new Reference();

		reference.setId(resultSet.getInt("id"));
		reference.setDoi(resultSet.getString("doi"));
		reference.setName(resultSet.getString("name"));
		reference.setCreatedOn(resultSet.getDate("created_on"));
		reference.setUpdatedOn(resultSet.getDate("updated_on"));

		return reference;
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
