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
	private static final String SAMPLE_LISTS = "SELECT sample_lists.id, sample_lists.name, GROUP_CONCAT(DISTINCT samples.name ORDER BY samples.name SEPARATOR  '\\t') sample_names FROM sample_lists JOIN sample_lists_to_samples ON projects_id = sample_lists.id JOIN samples ON sample_lists_to_samples.samples_id = samples.id JOIN samples_to_datasets ON samples_to_datasets.sample_id = samples.id WHERE samples_to_datasets.dataset_id = ? GROUP BY sample_lists.id";
	private static final String SAMPLE_LISTS_BY_ID_DATASET = "SELECT sample_lists.id, sample_lists.name, GROUP_CONCAT(DISTINCT samples.name ORDER BY samples.name SEPARATOR  '\\t') sample_names FROM sample_lists JOIN sample_lists_to_samples ON projects_id = sample_lists.id JOIN samples ON sample_lists_to_samples.samples_id = samples.id JOIN samples_to_datasets ON samples_to_datasets.sample_id = samples.id WHERE samples_to_datasets.dataset_id = ? AND sample_lists_to_samples.projects_id = ? GROUP BY sample_lists.id";
	private static final String SAMPLE_LISTS_BY_ID_PUBLISHED = "SELECT sample_lists.id, sample_lists.name, GROUP_CONCAT(DISTINCT samples.name ORDER BY samples.name SEPARATOR  '\\t') sample_names FROM sample_lists JOIN sample_lists_to_samples ON projects_id = sample_lists.id JOIN samples ON sample_lists_to_samples.samples_id = samples.id JOIN samples_to_datasets ON samples_to_datasets.sample_id = samples.id WHERE samples_to_datasets.dataset_id = ? AND sample_lists_to_samples.projects_id = ? AND samples.published IS TRUE GROUP BY sample_lists.id";
	private static final String SAMPLE_LISTS_BY_ID = "SELECT sample_lists.id, sample_lists.name, GROUP_CONCAT(DISTINCT samples.name ORDER BY samples.name SEPARATOR  '\\t') sample_names FROM sample_lists JOIN sample_lists_to_samples ON projects_id = sample_lists.id JOIN samples ON sample_lists_to_samples.samples_id = samples.id JOIN samples_to_datasets ON samples_to_datasets.sample_id = samples.id WHERE sample_lists_to_samples.projects_id = ? GROUP BY sample_lists.id";

	@Resource(name = "jdbc/cranachan")
	private DataSource ds;

	public SampleListDAO()
	{
	}

	public SampleList get(String id, String datasetId, boolean onlyPublished)
	{
		SampleList sampleList = new SampleList();

		String query = onlyPublished ? SAMPLE_LISTS_BY_ID_PUBLISHED : SAMPLE_LISTS_BY_ID_DATASET;

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createPreparedStatement(con, query, datasetId, id);
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

	public SampleList getById(String id)
	{
		SampleList sampleList = new SampleList();

		try (Connection con = ds.getConnection();
			 PreparedStatement stmt = DatabaseUtils.createPreparedStatement(con, SAMPLE_LISTS_BY_ID, id);
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
			 PreparedStatement stmt = DatabaseUtils.createByIdStatement(con, SAMPLE_LISTS, datasetId);
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
}
