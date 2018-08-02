package jhi.cranachan.data;

import java.io.*;

import org.primefaces.model.*;

public class GeneTableRow implements Serializable
{
	public String gene;
	public String chromosome;
	public long start;
	public long stop;
	public long numberSnps;
	public File vcf;

	public GeneTableRow()
	{
	}

	public GeneTableRow(String gene, String chromosome, long start, long stop, long numberSnps, File vcf)
	{
		this.gene = gene;
		this.chromosome = chromosome;
		this.start = start;
		this.stop = stop;
		this.numberSnps = numberSnps;
		this.vcf = vcf;
	}

	public DefaultStreamedContent getFileForDownload()
		throws Exception
	{
		return new DefaultStreamedContent(new FileInputStream(vcf), "text/plain", vcf.getName());
	}

	public String getGene()
		{ return gene; }

	public void setGene(String gene)
		{ this.gene = gene; }

	public String getChromosome()
		{ return chromosome; }

	public void setChromosome(String chromosome)
		{ this.chromosome = chromosome; }

	public long getStart()
		{ return start; }

	public void setStart(long start)
		{ this.start = start; }

	public long getStop()
		{ return stop; }

	public void setStop(long stop)
		{ this.stop = stop; }

	public long getNumberSnps()
		{ return numberSnps; }

	public void setNumberSnps(long numberSnps)
		{ this.numberSnps = numberSnps; }

	public File getVcf()
		{ return vcf; }

	public void setVcf(File vcf)
		{ this.vcf = vcf; }
}
