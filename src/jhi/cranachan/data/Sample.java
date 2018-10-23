package jhi.cranachan.data;

public class Sample
{
	private String name;
	private boolean published;

	public Sample()
	{
	}

	public Sample(String name)
	{
		this.name = name;
	}

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public boolean isPublished()
		{ return published; }

	public void setPublished(boolean published)
		{ this.published = published; }
}