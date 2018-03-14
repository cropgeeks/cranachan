package jhi.cranachan.data;

import java.sql.*;

public class Dataset
{
	private int id;
	private String doi;
	private String name;
	private String version;
	private String filepath;
	private int refSeqSetId;
	private DatasetDescription description;
	private Date createdOn;
	private Date updatedOn;

	public Dataset()
	{
	}

	public int getId()
		{ return id; }

	public void setId(int id)
		{ this.id = id; }

	public String getDoi()
		{ return doi; }

	public void setDoi(String doi)
		{ this.doi = doi; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public String getVersion()
		{ return version; }

	public void setVersion(String version)
		{ this.version = version; }

	public String getFilepath()
		{ return filepath; }

	public void setFilepath(String filepath)
		{ this.filepath = filepath; }

	public int getRefSeqSetId()
		{ return refSeqSetId; }

	public void setRefSeqSetId(int refSeqSetId)
		{ this.refSeqSetId = refSeqSetId; }

	public DatasetDescription getDescription()
		{ return description; }

	public void setDescription(DatasetDescription description)
		{ this.description = description; }

	public Date getCreatedOn()
		{ return createdOn; }

	public void setCreatedOn(Date createdOn)
		{ this.createdOn = createdOn; }

	public Date getUpdatedOn()
		{ return updatedOn; }

	public void setUpdatedOn(Date updatedOn)
		{ this.updatedOn = updatedOn; }
}