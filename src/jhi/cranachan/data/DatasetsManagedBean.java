package jhi.cranachan.data;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.faces.view.*;
import javax.inject.*;

import jhi.cranachan.database.*;

@Named
@ViewScoped
public class DatasetsManagedBean implements Serializable
{
	private List<Dataset> datasets = new ArrayList<>();

	@Inject
	private DatasetDAO dao;

	public DatasetsManagedBean()
	{
	}

	@PostConstruct
	public void init()
	{
		datasets = dao.getAll();
	}

	public List<Dataset> getDatasets()
		{ return datasets; }

	public void setDatasets(List<Dataset> datasets)
		{  this.datasets = datasets; }
}
