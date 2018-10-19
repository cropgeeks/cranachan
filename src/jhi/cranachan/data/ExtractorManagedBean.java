package jhi.cranachan.data;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.faces.context.*;
import javax.faces.view.*;
import javax.inject.*;

import jhi.cranachan.database.*;

import org.primefaces.model.*;

@Named
@ViewScoped
public class ExtractorManagedBean implements Serializable
{
	private Dataset dataset;
	private Reference reference;

	private String selectedChromosome;
	private long extractStart;
	private long extractEnd;
	private SampleList sampleList;

	private List<SampleList> sampleLists;
//	private List<Sample> samples;
//	private List<Sample> selectedSamples;
//	private DualListModel<Sample> sampleModel;
	private String sampleGroupName;

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

		dataset = datasetDAO.getById(datasetId);
		reference = referenceDAO.getById("" + dataset.getRefSeqSetId());
		sampleLists = sampleListDAO.getByDatasetId(datasetId);

//		samples = sampleDAO.getByDatasetId(datasetId);
//		selectedSamples = new ArrayList<Sample>();
//		sampleModel = new DualListModel<Sample>(samples, selectedSamples);
		sampleGroupName = "";
	}

//	public void processList()
//	{
//		List<Sample> selected = sampleModel.getTarget();
//
//		String value = selected.stream()
//		   .map(Sample::getName)
//		   .collect(Collectors.joining("\t"));
//
//		sampleListDAO.addList(sampleGroupName, ""+dataset.getId(), value);
//	}

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

//	public List<Sample> getSamples()
//		{ return samples; }
//
//	public DualListModel<Sample> getSampleModel()
//		{ return sampleModel; }
//
//	public void setSampleModel(DualListModel<Sample> sampleModel)
//		{ this.sampleModel = sampleModel; }

	public SampleList getSampleList()
		{ return sampleList; }

	public void setSampleList(SampleList sampleList)
		{ this.sampleList = sampleList; }

	public List<SampleList> getSampleLists()
		{ return sampleLists; }

	public void setSampleLists(List<SampleList> sampleLists)
		{ this.sampleLists = sampleLists; }

	public String getSampleGroupName()
		{ return sampleGroupName; }

	public void setSampleGroupName(String sampleGroupName)
		{ this.sampleGroupName = sampleGroupName; }
}
