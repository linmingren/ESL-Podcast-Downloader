package me.linmingren;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * http://www.eslpod.com is a very nice place to learn English.
 * This application will download the mp3 files and text content from http://www.eslpod.com.
 * You can copy the mp3 files to your phone and listen to them offline.
 */
public class EslPodCastCrawlerClient
{
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		String crawlStorageFolder = "crawler/";

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		final CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		controller.addSeed("http://www.eslpod.com/website/show_all.php");
		

		JFrame f = new JFrame("ESL Podcast Crawler");
		f.setPreferredSize(new Dimension(600, 600));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridLayout experimentLayout = new GridLayout(2,2);
		experimentLayout.setHgap(10);
		experimentLayout.setVgap(10);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		JPanel startEndPodcastPanel = new JPanel(experimentLayout);
		startEndPodcastPanel.add(new JLabel("Start downloading from podcast: "));
		final JTextField startPodcastText = new JTextField("1");
		startEndPodcastPanel.add(startPodcastText);
		startEndPodcastPanel.add(new JLabel("Stop downloading at podcast: "));
		final JTextField stopPodcastText = new JTextField("900");
		startEndPodcastPanel.add(stopPodcastText);
		contentPanel.add(startEndPodcastPanel, BorderLayout.NORTH);
		
		final ConsolePane p = new ConsolePane();
		contentPanel.add(p,BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		JButton downloadButton = new JButton("Start Downloading");
		downloadButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				new Thread(new Runnable(){

					@Override
					public void run()
					{
						controller.setCustomData(new EslPodCastDownloadingConfig(Integer.parseInt(startPodcastText.getText()), Integer.parseInt(stopPodcastText.getText()), p.getPrintStream()));
						controller.start(EslPodCastCrawler.class, 1);
					}
				}).start();
			}
		});
		footer.add(downloadButton);
		contentPanel.add(footer,BorderLayout.SOUTH);
		
		f.setContentPane(contentPanel);
		f.pack();
		f.setVisible(true);
		
		
	}
}
