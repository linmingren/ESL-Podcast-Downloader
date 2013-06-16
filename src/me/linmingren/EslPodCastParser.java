package me.linmingren;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;

/**
 *
 */
public class EslPodCastParser
{
    public EslPodCast parse(Page page)
    {
    	String url = page.getWebURL().getURL();
    	EslPodCast pod = null;
    	
    	if(url.contains("show_podcast"))
		{
			if(page.getParseData() instanceof HtmlParseData)
			{
				
				HtmlParseData htmlParseData = (HtmlParseData)page.getParseData();
				String html = htmlParseData.getHtml();
				
				Document doc = Jsoup.parse(html, "UTF-8");
				Elements links = doc.select("td[width=99%][bgcolor=#FFFFFF]");
		        for (Element e : links)
		        {
		        	if (!e.select("title").isEmpty())
		        	{
		        		if (!e.select("title").get(0).ownText().contains("ESL Podcast"))
		        		{
		        			continue;
		        		}
		        			
		        		pod = new EslPodCast();
		        		pod.setTitle(e.select("title").get(0).ownText());
		        		
		        		Element downloadLink = e.select("a[href$=mp3]").get(0);
		        		pod.setAudioLink(downloadLink.attr("href"));
		            	
		            	Element body = e.select("table").get(1).select("span").get(0);
		            	
		            	pod.setContent(body.html());
		        	}
		        }
			}
		}
    	
    	return pod;
    }
}
