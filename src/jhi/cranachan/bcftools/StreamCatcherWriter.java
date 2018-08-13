package jhi.cranachan.bcftools;

import java.io.*;
import java.util.logging.*;

public class StreamCatcherWriter extends Thread
{
	private static Logger LOG = Logger.getLogger(StreamCatcherWriter.class.getName());

	private InputStream in;
	private File file;

	public StreamCatcherWriter(InputStream in, File file)
	{
		this.in = in;
		this.file = file;
	}

	@Override
	public void run()
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			PrintWriter writer = new PrintWriter(new FileWriter(file)))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
				writer.println(line);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
