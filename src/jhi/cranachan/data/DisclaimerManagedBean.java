package jhi.cranachan.data;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.faces.context.*;
import javax.faces.view.*;
import javax.inject.*;
import javax.servlet.http.*;

@Named
@ViewScoped
public class DisclaimerManagedBean implements Serializable
{
	private String datasetId;

	private boolean checked = false;

	public DisclaimerManagedBean()
	{
	}

	@PostConstruct
	public void init()
	{
		// Grab the request parameters map. This should let us get at parameters which have been posted to the results page
		Map<String,String> requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		this.datasetId = requestParams.get("datasetId");
	}

	public String checkCookie()
	{
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> requestCookieMap = context.getRequestCookieMap();

		Cookie cookie = (Cookie) requestCookieMap.get("cranachanDataset" + datasetId);
		if (cookie != null)
			return "search-by-gene.xhtml?datasetId=" + datasetId;

		return "disclaimer.xhtml";
	}

	public String getDatasetId()
		{ return datasetId; }

	public void setDatasetId(String datasetId)
		{ this.datasetId = datasetId; }

	public boolean isChecked()
		{ return checked; }

	public void setChecked(boolean checked)
		{ this.checked = checked; }
}
