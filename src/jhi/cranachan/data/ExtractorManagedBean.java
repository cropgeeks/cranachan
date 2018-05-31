package jhi.cranachan.data;

import org.primefaces.model.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import javax.annotation.*;
import javax.faces.context.*;
import javax.faces.view.*;
import javax.inject.*;

import jhi.cranachan.bcftools.*;
import jhi.cranachan.database.*;

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
	private List<Sample> selectedSamples;
	private DualListModel<Sample> sampleModel;
	private String sampleGroupName;

	private String tmpDir;
	private String bcfToolsPath;

	private StreamedContent file;

	@Inject
	private DatasetDAO datasetDAO;
	@Inject
	private ReferenceDAO referenceDAO;
	@Inject
	private SampleDAO sampleDAO;
	@Inject
	private SampleListDAO sampleListDAO;

	public ExtractorManagedBean()
	{
	}

	@PostConstruct
	public void init()
	{
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String datasetId = params.get("datasetId");

		tmpDir = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("tmpDir");
		bcfToolsPath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("bcfTools");

		dataset = datasetDAO.getById(datasetId);
		reference = referenceDAO.getById("" + dataset.getRefSeqSetId());
		samples = sampleDAO.getByDatasetId(datasetId);
		selectedSamples = new ArrayList<Sample>();
		sampleModel = new DualListModel<Sample>(samples, selectedSamples);
		sampleGroupName = "";
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

	public void processList()
	{
		List<Sample> selected = sampleModel.getTarget();

		String value = selected.stream()
		   .map(Sample::getName)
		   .collect(Collectors.joining("\t"));

		sampleListDAO.addList(sampleGroupName, ""+dataset.getId(), value);
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

	public DualListModel<Sample> getSampleModel()
		{ return sampleModel; }

	public void setSampleModel(DualListModel<Sample> sampleModel)
		{ this.sampleModel = sampleModel; }

	public String getSampleGroupName()
		{ return sampleGroupName; }

	public void setSampleGroupName(String sampleGroupName)
		{ this.sampleGroupName = sampleGroupName; }
}
