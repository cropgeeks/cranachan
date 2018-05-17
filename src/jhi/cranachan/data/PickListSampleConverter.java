package jhi.cranachan.data;

import org.primefaces.component.picklist.*;
import org.primefaces.model.*;

import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;

@FacesConverter(value = "sampleConverter")
public class PickListSampleConverter implements Converter
{
	@Override
	public Object getAsObject(FacesContext fc, UIComponent comp, String value)
	{
		DualListModel<Sample> model = (DualListModel<Sample>) ((PickList) comp).getValue();
		for (Sample employee : model.getSource())
		{
			if (employee.getName().equals(value))
			{
				return employee;
			}
		}
		for (Sample employee : model.getTarget())
		{
			if (employee.getName().equals(value))
			{
				return employee;
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent comp, Object value)
	{
		return ((Sample) value).getName();
	}
}