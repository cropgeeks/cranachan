// Copyright 2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.cranachan.bcftools;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * A simple wrapper class used for running bcftools on the command line.
 */
public class BcfToolsStats
{
	private static Logger LOG = Logger.getLogger(BcfToolsStats.class.getName());

	private File bcfFile;
	private File outputFile;

	/**
	 * Constructs a new BcfToolsView object that will run bcftools stats upon the
	 * given bcfFile path.
	 * @param bcfFile the path to the BCF stats file to be processed
	 */
	public BcfToolsStats(File bcfFile)
	{
		this.bcfFile = bcfFile;
	}

	/** > outputFile */
	public BcfToolsStats withOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
		return this;
	}

	public void run(String bcftools, String tmpDir)
		throws Exception
	{
		List<String> args = new ArrayList<>();

		args.add("bcftools");
		args.add("stats");
		if (outputFile != null)
		{
			args.add(bcfFile.getPath());
		}

		ProcessBuilder pb = new ProcessBuilder(args);

		System.out.println();
		for (String command: pb.command())
			System.out.print(command + " ");
		System.out.println();

		for (String command: pb.command())
			LOG.log(Level.INFO, command);

		pb.directory(new File(tmpDir));
		pb.redirectErrorStream(true);

		Process proc = pb.start();
		LOG.log(Level.INFO, "Process started");

		// Open up the input stream (to read from) (prog's out stream)
		StreamCatcherWriter writer = new StreamCatcherWriter(proc.getInputStream(), outputFile);
		writer.run();

		LOG.log(Level.INFO, "Process completed");
	}
}