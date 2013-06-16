package me.linmingren;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;



/**
 *
 */
public class EslPodCastPdfRenderer
{
	public void createPDF(Reader reader, String pdfPath) throws IOException, com.lowagie.text.DocumentException
	{
		OutputStream os = null;
		try
		{
			os = new FileOutputStream(pdfPath);

			ITextRenderer renderer = new ITextRenderer();
			Document doc = XMLResource.load(new InputSource(reader)).getDocument();

			renderer.setDocument(doc, "");
			renderer.layout();
			renderer.createPDF(os);

			os.close();
			os = null;
		}
		finally
		{
			if(os != null)
			{
				try
				{
					os.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
		}
	}

	public void createPDF(String url, String pdfPath) throws IOException, com.lowagie.text.DocumentException
	{
		OutputStream os = null;
		try
		{
			os = new FileOutputStream(pdfPath);

			ITextRenderer renderer = new ITextRenderer();
			Document doc = XMLResource.load(new InputSource(url)).getDocument();

			renderer.setDocument(doc, url);
			renderer.layout();
			renderer.createPDF(os);

			os.close();
			os = null;
		}
		finally
		{
			if(os != null)
			{
				try
				{
					os.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
		}
	}
}
