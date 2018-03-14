package jhi.cranachan.database;

import java.sql.*;
import java.util.*;
import javax.annotation.*;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.sql.*;

import jhi.cranachan.data.*;

import com.google.gson.*;

@Named
@ApplicationScoped
public class DatasetDAO
{
	private static final String ALL_DATASETS = "SELECT * FROM datasets";
	private static final String DATASET_BY_ID = "SELECT * FROM datasets WHERE id = ?";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public DatasetDAO()
	{
	}

	public List<Dataset> getAll()
	{
		List<Dataset> datasets = new ArrayList<>();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = con.prepareStatement(ALL_DATASETS);
			 ResultSet resultSet = stmt.executeQuery())
		{
			while (resultSet.next())
			{
				Dataset dataset = getDataset(resultSet);
				datasets.add(dataset);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return datasets;
	}

	public Dataset getById(String id)
	{
		Dataset dataset = new Dataset();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, DATASET_BY_ID, id);
			 ResultSet resultSet = stmt.executeQuery())
		{
			if (resultSet.next())
				dataset = getDataset(resultSet);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return dataset;
	}

	private Dataset getDataset(ResultSet resultSet)
		throws SQLException
	{
		Dataset dataset = new Dataset();

		dataset.setId(resultSet.getInt("id"));
		dataset.setDoi(resultSet.getString("doi"));
		dataset.setName(resultSet.getString("name"));
		dataset.setVersion(resultSet.getString("version"));
		dataset.setFilepath(resultSet.getString("filepath"));
		dataset.setRefSeqSetId(resultSet.getInt("refseqset_id"));
		dataset.setCreatedOn(resultSet.getDate("created_on"));
		dataset.setUpdatedOn(resultSet.getDate("updated_on"));

		String descString = resultSet.getString("description");
		if (descString != null)
		{
			GsonBuilder builder = new GsonBuilder();
			builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
			Gson gson = builder.create();
			dataset.setDescription(gson.fromJson(descString, DatasetDescription.class));
		}

		return dataset;
	}
}
