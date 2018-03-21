package jhi.cranachan.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.annotation.*;
import javax.faces.context.*;
import javax.faces.view.*;
import javax.inject.*;

import jhi.cranachan.bcftools.BcfToolsView;
import jhi.cranachan.database.*;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named
@ViewScoped
public class ExtractorManagedBean implements Serializable
{
	private Dataset dataset;
	private Reference reference;

	private String selectedChromosome;
	private long extractStart;
	private long extractEnd;

	private List<Sample> samples;

	private String tmpDir;
	private String bcfToolsPath;

	private StreamedContent file;

	@Inject
	private DatasetDAO datasetDAO;
	@Inject
	private ReferenceDAO referenceDAO;
	@Inject
	private SampleDAO sampleDAO;

	public ExtractorManagedBean()
	{
	}

	@PostConstruct
	public void init()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
		String datasetId = params.get("datasetId");

		tmpDir = fc.getExternalContext().getInitParameter("tmpDir");
		bcfToolsPath = fc.getExternalContext().getInitParameter("bcfTools");

		dataset = datasetDAO.getById(datasetId);
		reference = referenceDAO.getById("" + dataset.getRefSeqSetId());
		samples = sampleDAO.getByDatasetId(datasetId);
	}

	public StreamedContent getFile()
	{
		File output = new File(tmpDir, dataset.getName() + "-" + System.currentTimeMillis() + ".vcf");
		try
		{
			BcfToolsView view = new BcfToolsView(new File(dataset.getFilepath()))
				.withOnlySNPs()
				.withVCFOutput()
				.withRegions(selectedChromosome, extractStart, extractEnd)
				.withOutputFile(output);

			view.run("BCFTOOLS", tmpDir);

			return new DefaultStreamedContent(new FileInputStream(output), "text/plain", output.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return new DefaultStreamedContent();
	}

	public Dataset getDataset()
		{ return dataset; }

	public void setDataset(Dataset dataset)
		{ this.dataset = dataset; }

	public Reference getReference()
		{ return reference; }

	public void setReference(Reference reference)
		{ this.reference = reference; }

	public String getSelectedChromosome()
		{ return selectedChromosome; }

	public void setSelectedChromosome(String selectedChromosome)
		{ this.selectedChromosome = selectedChromosome; }

	public long getExtractStart()
		{ return extractStart; }

	public void setExtractStart(long extractStart)
		{ this.extractStart = extractStart; }

	public long getExtractEnd()
		{ return extractEnd; }

	public void setExtractEnd(long extractEnd)
		{ this.extractEnd = extractEnd; }

	public List<Sample> getSamples()
		{ return samples; }
}
