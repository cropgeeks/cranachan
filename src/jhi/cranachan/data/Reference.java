package jhi.cranachan.data;

import java.sql.Date;
import java.util.*;

public class Reference
{
	private int id;
	private String doi;
	private String name;
	private List<Chromosome> chromosomes;
	private Date createdOn;
	private Date updatedOn;

	public Reference()
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

	public List<Chromosome> getChromosomes()
		{ return chromosomes; }

	public void setChromosomes(List<Chromosome> chromosomes)
		{ this.chromosomes = chromosomes; }

	public Date getCreatedOn()
		{ return createdOn; }

	public void setCreatedOn(Date createdOn)
		{ this.createdOn = createdOn; }

	public Date getUpdatedOn()
		{ return updatedOn; }

	public void setUpdatedOn(Date updatedOn)
		{ this.updatedOn = updatedOn; }
}
