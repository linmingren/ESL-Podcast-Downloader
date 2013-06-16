package me.linmingren;

import java.io.PrintStream;

/**
 *
 */
public class EslPodCastDownloadingConfig
{
    private int startIndex;
    private int endIndex;
    private PrintStream printer;
    
    public EslPodCastDownloadingConfig(int startIndex, int endIndex, PrintStream printer)
    {
    	this.startIndex = startIndex;
    	this.endIndex = endIndex;
    	this.printer = printer;
    }

	/**
	 * @return the startIndex
	 */
	public int getStartIndex()
	{
		return startIndex;
	}

	/**
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}

	/**
	 * @return the endIndex
	 */
	public int getEndIndex()
	{
		return endIndex;
	}

	/**
	 * @param endIndex the endIndex to set
	 */
	public void setEndIndex(int endIndex)
	{
		this.endIndex = endIndex;
	}

	/**
	 * @return the printer
	 */
	public PrintStream getPrinter()
	{
		return printer;
	}

	/**
	 * @param printer the printer to set
	 */
	public void setPrinter(PrintStream printer)
	{
		this.printer = printer;
	}
	
	
}
