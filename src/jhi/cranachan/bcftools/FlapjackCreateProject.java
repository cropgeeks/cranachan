package jhi.cranachan.bcftools;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class FlapjackCreateProject
{
	private static Logger LOG = Logger.getLogger(FlapjackCreateProject.class.getName());

	private File genotypeFile;
	private File mapFile;
	private File projectFile;
	private File qtlFile;
	private String datasetName;

	public FlapjackCreateProject(File genotypeFile, File projectFile)
	{
		this.genotypeFile = genotypeFile;
		this.projectFile = projectFile;
	}

	public FlapjackCreateProject withMapFile(File mapFile)
	{
		this.mapFile = mapFile;

		return this;
	}

	public FlapjackCreateProject withQtlFile(File qtlFile)
	{
		if (qtlFile != null && qtlFile.exists())
			this.qtlFile = qtlFile;

		return this;
	}

	public FlapjackCreateProject withDatasetName(String datasetName)
	{
		this.datasetName = datasetName;

		return this;
	}

	public void run(String flapjackPath, String tmpDir)
		throws Exception
	{
		List<String> args = new ArrayList<>();

		args.add("java");
		args.add("-cp");
		args.add(flapjackPath);
		args.add("jhi.flapjack.io.cmd.CreateProject");
		if (genotypeFile != null && projectFile != null)
		{
			args.add("-g");
			args.add(genotypeFile.getPath());
			args.add("-p");
			args.add(projectFile.getPath());
		}
		if (mapFile != null)
		{
			args.add("-m");
			args.add(mapFile.getPath());
		}
		if (qtlFile != null)
		{
			args.add("-q");
			args.add(qtlFile.getPath());
		}
		if (datasetName != null)
		{
			args.add("-n");
			args.add(datasetName);
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
		StreamCatcher oStream = new StreamCatcherLogger(proc.getInputStream());

		proc.waitFor();
		LOG.log(Level.INFO, "Waiting for process...");

		while (oStream.isAlive())
			Thread.sleep(500);

		LOG.log(Level.INFO, "Process completed");
	}
}
