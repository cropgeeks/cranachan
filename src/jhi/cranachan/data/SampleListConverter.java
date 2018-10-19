package jhi.cranachan.data;

import jhi.cranachan.database.*;

import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import javax.inject.*;

@FacesConverter(value = "sampleListConverter")
public class SampleListConverter implements Converter
{
	@Inject
	private SampleListDAO sampleListDAO;

	@Override
	public Object getAsObject(FacesContext fc, UIComponent comp, String value)
	{
		if (value == null || value.isEmpty())
			return null;

		return sampleListDAO.getById(value);
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent comp, Object value)
	{
		if (value != null)
			return ((SampleList) value).getName();

		return null;
	}
}