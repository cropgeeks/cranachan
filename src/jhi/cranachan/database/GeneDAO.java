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
public class GeneDAO
{
	private static final String GENES_BY_NAME_AND_DATASET_ID = "SELECT refseqs.*, genes.name, " +
		" genes_to_refseqs.position_start, genes_to_refseqs.position_end FROM datasets" +
		" LEFT JOIN refseqsets ON refseqsets.id = datasets.refseqset_id" +
		" LEFT JOIN refseqs ON refseqs.refseqset_id = refseqsets.id" +
		" LEFT JOIN genes_to_refseqs ON genes_to_refseqs.refseq_id = refseqs.id" +
		" LEFT JOIN genes ON genes.id = genes_to_refseqs.gene_id" +
		" WHERE genes.name = ? AND datasets.id = ?";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public GeneDAO()
	{
	}


	public static PreparedStatement genePreparedStatement(Connection con, String query, String id, String geneName)
		throws SQLException
	{
		// Prepare statement with ID
		PreparedStatement statement = con.prepareStatement(query);
		statement.setString(1, geneName);
		statement.setString(2, id);

		return statement;
	}

	public List<Gene> getByNameAndDatasetId(String name, String datasetId)
	{
		List<Gene> genes = new ArrayList<>();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = genePreparedStatement(con, GENES_BY_NAME_AND_DATASET_ID, datasetId, name);
			 ResultSet resultSet = stmt.executeQuery())
		{
			while (resultSet.next())
			{
				Gene gene = getGene(resultSet);
				genes.add(gene);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return genes;
	}

	private Gene getGene(ResultSet resultSet)
		throws SQLException
	{
		Gene gene = new Gene();

		gene.setId(resultSet.getInt("id"));
		gene.setDoi(resultSet.getString("doi"));
		gene.setName(resultSet.getString("genes.name"));
		gene.setChromosome(resultSet.getString("name"));
		gene.setStart(resultSet.getLong("position_start"));
		gene.setEnd(resultSet.getLong("position_end"));

		return gene;
	}
}
