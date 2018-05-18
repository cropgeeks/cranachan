// Copyright 2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.cranachan.bcftools;

import java.io.*;

// Not really a proper test class, but it's handy to have for now
public class BcfToolsViewTest
{
	public static void main(String[] args)
		throws Exception
	{
		String chromosome = "chr1H";
		long start = 1;
		long end = 50000;

		File bcfFile = new File("test.bcf");
		File vcfFile = new File("test.vcf");


		BcfToolsView bView = new BcfToolsView(bcfFile)
			.withOnlySNPs()
			.withVCFOutput()
			.withRegions(chromosome, start, end)
			.withOnlySamples("ACUTE\tADONIS\tAGENDA")
			.withOutputFile(vcfFile);

		String path = "";
		bView.run(path, ".");
	}
}