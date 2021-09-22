package jhi.cranachan.data;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.faces.context.*;
import javax.faces.view.*;
import javax.inject.*;
import javax.servlet.http.*;

import jhi.cranachan.database.*;

@Named
@ViewScoped
public class ExtractorManagedBean implements Serializable
{
	private Dataset dataset;
	private Reference reference;

	private Chromosome selectedChromosome;
	private long extractStart = 1;
	private long extractEnd = 1;
	private SampleList sampleList;

	private List<SampleList> sampleLists;

	@Inject
	private DatasetDAO datasetDAO;
	@Inject
	private ReferenceDAO referenceDAO;
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

		if (params.containsKey("acceptedDisclaimer"))
		{
			boolean acceptedDisclaimer = Boolean.parseBoolean(params.get("acceptedDisclaimer"));
			setCookie(acceptedDisclaimer, datasetId);
		}

		dataset = datasetDAO.getById(datasetId);
		reference = referenceDAO.getById("" + dataset.getRefSeqSetId());
		sampleLists = sampleListDAO.getByDatasetId(datasetId);

		selectedChromosome = reference.getChromosomes().get(0);
	}

	private void setCookie(boolean acceptedDisclaimer, String datasetId)
	{
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> requestCookieMap = context.getRequestCookieMap();
		Cookie cookie = (Cookie) requestCookieMap.get("cranachanDataset" + datasetId);

		if (acceptedDisclaimer)
		{
			HttpServletRequest request = (HttpServletRequest) context.getRequest();

			if (cookie == null)
			{
				cookie = new Cookie("cranachanDataset" + datasetId, "disclaimer-accepted");
				cookie.setPath(request.getContextPath());
			}
		}

		if (cookie != null)
		{
			// Set cookie expiry for roughly 6 months
			cookie.setMaxAge(24 * 60 * 60 * 182);

			HttpServletResponse response = (HttpServletResponse) context.getResponse();
			response.addCookie(cookie);
		}
	}

	public Dataset getDataset()
		{ return dataset; }

	public void setDataset(Dataset dataset)
		{ this.dataset = dataset; }

	public Reference getReference()
		{ return reference; }

	public void setReference(Reference reference)
		{ this.reference = reference; }

	public Chromosome getSelectedChromosome()
		{ return selectedChromosome; }

	public void setSelectedChromosome(Chromosome selectedChromosome)
		{ this.selectedChromosome = selectedChromosome; }

	public long getExtractStart()
		{ return extractStart; }

	public void setExtractStart(long extractStart)
		{ this.extractStart = extractStart; }

	public long getExtractEnd()
		{ return extractEnd; }

	public void setExtractEnd(long extractEnd)
		{ this.extractEnd = extractEnd; }

	public SampleList getSampleList()
		{ return sampleList; }

	public void setSampleList(SampleList sampleList)
		{ this.sampleList = sampleList; }

	public List<SampleList> getSampleLists()
		{ return sampleLists; }

	public void setSampleLists(List<SampleList> sampleLists)
		{ this.sampleLists = sampleLists; }
}
