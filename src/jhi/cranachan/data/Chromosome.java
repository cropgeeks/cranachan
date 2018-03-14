package jhi.cranachan.data;

import java.sql.Date;

public class Chromosome
{
	private int id;
	private String doi;
	private String name;
	private int length;
	private Date createdOn;
	private Date updatedOn;

	public Chromosome()
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

	public int getLength()
		{ return length; }

	public void setLength(int length)
		{ this.length = length; }

	public Date getCreatedOn()
		{ return createdOn; }

	public void setCreatedOn(Date createdOn)
		{ this.createdOn = createdOn; }

	public Date getUpdatedOn()
		{ return updatedOn; }

	public void setUpdatedOn(Date updatedOn)
		{ this.updatedOn = updatedOn; }
}