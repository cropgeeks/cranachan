// Copyright 2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.snps.servlet;

import java.io.*;

public class RunTools
{
	public static void run(String bcftools, String tmpDir, String bcfFile, File vcfFile, String chr, String start, String end)
		throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(
			bcftools,
			"view",
			"--types=snps",
			"--regions=" + chr + ":" + start + "-" + end,
			"--output-type=v",
			"--output-file=" + vcfFile.getPath(),
			bcfFile);

		System.out.println();
		for (String command: pb.command())
			System.out.print(command + " ");
		System.out.println();
		System.out.println();

		//bcftools view -v snps -r chr1H:1-10000 -O v -o wibble.vcf /mnt/shared/scratch/mb40521/201702_wildBarleys/jointGenotyper/wild_barley_GATK_variants.bcf

		pb.directory(new File(tmpDir));
		pb.redirectErrorStream(true);

		Process proc = pb.start();
		System.out.println("Process started");

		// Open up the output stream (to write to) (prog's in stream)
//		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
//			proc.getOutputStream()));
//		writer.print("source(\"" + rScript.getPath().replace('\\', '/') + "\")");
//		writer.close();

		// Open up the input stream (to read from) (prog's out stream)
		StreamCatcher oStream = new StreamCatcher(proc.getInputStream(), true);

		proc.waitFor();
		System.out.println("Waiting for process");

		while (oStream.isAlive())
			Thread.sleep(500);

		System.out.println("Completed process");
	}

	private static class StreamCatcher extends Thread
	{
		protected BufferedReader reader = null;
		protected boolean showOutput = false;

		public StreamCatcher(InputStream in, boolean showOutput)
		{
			reader = new BufferedReader(new InputStreamReader(in));
			this.showOutput = showOutput;

			start();
		}

		public void run()
		{
			try
			{
				String line = reader.readLine();

				while (line != null)
				{
					if (showOutput)
						System.out.println(line);

					line = reader.readLine();
				}
			}
			catch (Exception e) {}

			try { reader.close(); }
			catch (IOException e) {}
		}
	}
}