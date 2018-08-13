// Copyright 2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.cranachan.bcftools;

import java.io.*;
import java.util.logging.*;

public class StreamCatcherLogger extends StreamCatcher
{
	private static Logger LOG = Logger.getLogger(StreamCatcherLogger.class.getName());

	public StreamCatcherLogger(InputStream in)
		throws IOException
	{
		super(in);
	}

	@Override
	protected void processLine(String line)
		throws Exception
	{
		LOG.log(Level.INFO, line);
	}
}