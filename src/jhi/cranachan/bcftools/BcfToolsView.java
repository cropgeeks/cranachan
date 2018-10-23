// Copyright 2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.cranachan.bcftools;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * A simple wrapper class used for running bcftools on the command line. See
 * BcfToolsViewTest for a short example on how to use.
 */
public class BcfToolsView
{
	private static Logger LOG = Logger.getLogger(BcfToolsView.class.getName());

	private File bcfFile;
	private String outputType, outputFile, regions, types, samples, qualityFilter;

	/**
	 * Constructs a new BcfToolsView object that will run bcftools upon the
	 * given bcfFile path.
	 * @param bcfFile the path to the BCF file to be processed
	 */
	public BcfToolsView(File bcfFile)
	{
		this.bcfFile = bcfFile;
	}

	/** --output-type=v */
	public BcfToolsView withVCFOutput()
	{
		outputType = "--output-type=v";
		return this;
	}

	/** --regions=chromosome:start-end */
	public BcfToolsView withRegions(String chromosome, long start, long end)
	{
		regions = "--regions=" + chromosome + ":" + start + "-" + end;
		return this;
	}

	/** --output-file=filename */
	public BcfToolsView withOutputFile(File filename)
	{
		outputFile = "--output-file=" + filename.getPath();
		return this;
	}

	/** --types=snps */
	public BcfToolsView withOnlySNPs()
	{
		types = "--types=snps";
		return this;
	}

	/**
	 * Uses --samples-file
	 * @param samples a tab-separated list of sample names
	 */
	public BcfToolsView withOnlySamples(String samples)
	{
		this.samples = samples;
		return this;
	}

	public BcfToolsView withQualityFilter(int score)
	{
		this.qualityFilter = "--include=QUAL>=" + score;
		return this;
	}

	public void run(String bcftools, String tmpDir)
		throws Exception
	{
		List<String> args = new ArrayList<>();

		args.add("bcftools");
		args.add("view");
		if (types != null)
			args.add(types);
		if (regions != null)
			args.add(regions);
		if (samples != null)
			args.add("--samples-file=" + createSamplesFile(tmpDir));
		if (qualityFilter != null)
			args.add(qualityFilter);
		if (outputType != null)
			args.add(outputType);
		if (outputFile != null)
			args.add(outputFile);
		if (outputFile != null)
			args.add(bcfFile.getPath());

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
		StreamCatcher oStream = new StreamCatcherLogger(proc.getInputStream());

		proc.waitFor();
		LOG.log(Level.INFO, "Waiting for process...");

		while (oStream.isAlive())
			Thread.sleep(500);

		LOG.log(Level.INFO, "Process completed");
	}

	private String createSamplesFile(String tmpDir)
		throws IOException
	{
		File file = File.createTempFile("samples-", ".txt", new File(tmpDir));

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for (String name: samples.split("\t"))
		{
			out.write(name);
			out.newLine();
		}

		out.close();

		return file.getPath();
	}
}