// Copyright 2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.snps.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ExtractionServlet extends HttpServlet
{
	private String bcfTools, bcfFile, tmpDir;

	@Override
	public void init(ServletConfig servletConfig)
		throws ServletException
	{
		super.init(servletConfig);

		bcfTools = getInitParameter("bcfTools");
		bcfFile = getInitParameter("bcfFile");
		tmpDir = getInitParameter("tmpDir");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		String chromosome = request.getParameter("chromosome");
		String start = request.getParameter("start");
		String end = request.getParameter("end");

		File vcfFile = File.createTempFile(request.getRemoteAddr() + "-", ".vcf", new File(tmpDir));

//		response.setContentType("text/html");
//		PrintWriter pw = response.getWriter();
//		pw.println("<p>bcftools: " + bcfTools + "</p>");
//		pw.println("<p>bcfFile: " + bcfFile + "</p>");
//		pw.println("<p>tmpDir: " + tmpDir + "</p>");
//		pw.close();
//		if (true) return;

		try
		{
			RunTools.run(bcfTools, tmpDir, bcfFile, vcfFile, chromosome, start, end);
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}


		response.setContentType("application/octet-stream");
		response.setContentLength((int) vcfFile.length());
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", vcfFile.getName()));

		OutputStream out = response.getOutputStream();
		try (FileInputStream in = new FileInputStream(vcfFile))
		{
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0)
				out.write(buffer, 0, length);
		}
		out.flush();
		out.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		doGet(request, response);
	}
}