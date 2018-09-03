package jhi.cranachan.data;

import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import java.io.*;

@FacesConverter("fileConverter")
public class FileConverter implements Converter<File>
{
	@Override
	public File getAsObject(FacesContext facesContext, UIComponent uiComponent, String s)
	{
		if (s == null)
			return null;

		return new File(s);
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, File file)
	{
		if (file == null)
			return null;

		return file.getAbsolutePath();
	}
}
