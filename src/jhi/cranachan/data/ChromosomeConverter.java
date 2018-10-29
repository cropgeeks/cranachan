package jhi.cranachan.data;

import jhi.cranachan.database.*;

import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import javax.inject.*;

@FacesConverter(value = "chromosomeConverter")
public class ChromosomeConverter implements Converter
{
	@Inject
	private ChromosomeDAO chromosomeDAO;

	@Override
	public Object getAsObject(FacesContext fc, UIComponent comp, String value)
	{
		if (value == null || value.isEmpty())
			return null;

		return chromosomeDAO.getById(value);
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent comp, Object value)
	{
		if (value != null)
			return ((Chromosome) value).getName();

		return null;
	}
}
