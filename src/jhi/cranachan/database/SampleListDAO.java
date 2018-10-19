package jhi.cranachan.database;

import jhi.cranachan.data.*;

import java.sql.*;
import java.util.*;
import java.util.stream.*;

import javax.annotation.*;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.sql.*;

@Named
@ApplicationScoped
public class SampleListDAO
{
	private static final String SAMPLE_LISTS_BY_DATASET = "SELECT id, name, sample_list FROM sample_lists WHERE dataset_id=? order by name;";
	private static final String SAMPLE_LISTS_BY_ID = "SELECT id, name, sample_list FROM sample_lists WHERE id=?";
	private static final String INSERT_LIST = "INSERT INTO sample_lists (id, name, dataset_id, sample_list) VALUES (?, ?, ?, ?)";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public SampleListDAO()
	{
	}

	public SampleList getById(String id)
	{
		SampleList sampleList = new SampleList();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, SAMPLE_LISTS_BY_ID, id);
			 ResultSet resultSet = stmt.executeQuery())
		{
			if (resultSet.next())
			{
				sampleList.setId(resultSet.getString(1));
				sampleList.setName(resultSet.getString(2));

				String samplesString = resultSet.getString(3);
				List<Sample> samples = Stream.of(samplesString.split("\t")).map(Sample::new).collect(Collectors.toCollection(ArrayList::new));
				sampleList.setSamples(samples);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return sampleList;
	}

	public List<SampleList> getByDatasetId(String datasetId)
	{
		List<SampleList> sampleLists = new ArrayList<SampleList>();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, SAMPLE_LISTS_BY_DATASET, datasetId);
			 ResultSet resultSet = stmt.executeQuery())
		{
			while (resultSet.next())
			{
				SampleList sampleList = new SampleList();
				sampleList.setId(resultSet.getString(1));
				sampleList.setName(resultSet.getString(2));

				String samplesString = resultSet.getString(3);
				List<Sample> samples = Stream.of(samplesString.split("\t")).map(Sample::new).collect(Collectors.toCollection(ArrayList::new));
				sampleList.setSamples(samples);

				sampleLists.add(sampleList);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return sampleLists;
	}

	public String addList(String name, String datasetId, String list)
	{
		String uuid = UUID.randomUUID().toString();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createInsertStatement(con, INSERT_LIST, uuid, name, datasetId, list))
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
