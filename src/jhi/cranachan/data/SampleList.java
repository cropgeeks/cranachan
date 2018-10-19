package jhi.cranachan.data;

import java.util.*;
import java.util.stream.*;

public class SampleList
{
	private String id;
	private String name;
	private List<Sample> samples;

	public SampleList()
	{
	}

	public String asTabSeparatedString()
	{
		return samples.stream().map(Sample::getName).sorted().collect(Collectors.joining("\t"));
	}

	public String getId()
		{ return id; }

	public void setId(String id)
		{ this.id = id; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public List<Sample> getSamples()
		{ return samples; }

	public void setSamples(List<Sample> samples)
		{ this.samples = samples; }
}
