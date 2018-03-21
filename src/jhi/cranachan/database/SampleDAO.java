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
public class SampleDAO
{
	private static final String ALL_SAMPLES = "SELECT name FROM samples_to_datasets, samples WHERE dataset_id=? and samples_to_datasets.sample_id = samples.id order by name;";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public SampleDAO()
	{
	}

	public List<Sample> getByDatasetId(String id)
	{
		List<Sample> samples = new ArrayList<>();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, ALL_SAMPLES, id);
			 ResultSet resultSet = stmt.executeQuery())
		{
			while (resultSet.next())
			{
				Sample sample = new Sample();
				sample.setName(resultSet.getString(1));

				samples.add(sample);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return samples;
	}
}
