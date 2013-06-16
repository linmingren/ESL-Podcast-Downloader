package me.linmingren;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AutoRetryHttpClient;
import org.w3c.tidy.Tidy;

import com.lowagie.text.DocumentException;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 *
 */
public class EslPodCastCrawler extends WebCrawler {
	private static String storageFolder = "eslpod/";

	EslPodCastDownloadingConfig config;

	Vector<String> mp3PodCastNeedToDownload = new Vector<String>();
	Vector<String> pdfPodCastNeedToDownload = new Vector<String>();

	@Override
	public void onStart() {
		config = (EslPodCastDownloadingConfig) this.getMyController()
				.getCustomData();

		config.getPrinter().println(
				"start downloading podcast in [" + config.getStartIndex() + ","
						+ config.getEndIndex() + "]");

		File f = new File(storageFolder);
		if (!f.exists()) {
			f.mkdir();
		} else {
			String[] podCasts = f.list();
			List<String> mp3PodCastList = new ArrayList<String>();
			List<String> pdfPodCastList = new ArrayList<String>();

			for (String podCastName : podCasts) {
				try {
					String podCastIndex = podCastName.split(" ")[2];

					if (podCastName.endsWith("mp3")) {
						mp3PodCastList.add(podCastIndex);
					} else {
						pdfPodCastList.add(podCastIndex);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (int i = config.getStartIndex(); i <= config.getEndIndex(); ++i) {
				if (!mp3PodCastList.contains(String.valueOf(i))) {
					mp3PodCastNeedToDownload.add(String.valueOf(i));
				}

				if (!pdfPodCastList.contains(String.valueOf(i))) {
					pdfPodCastNeedToDownload.add(String.valueOf(i));
				}
			}
		}
	}

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		// this.getMyController().
		if (pdfPodCastNeedToDownload.isEmpty()
				&& mp3PodCastNeedToDownload.isEmpty()) {
			return false;
		}

		String href = url.getURL().toLowerCase();
		// cat_id: category id, such as "all","Relationships", etc
		return href.contains("show_podcast.php")
				|| href.contains("show_all.php?cat_id=-59456");
	}

	// store the text content as a PDF, and download the mp3 file
	private void storePodCast(Page page) throws IOException, DocumentException {
		EslPodCast pod = new EslPodCastParser().parse(page);
		if (pod == null) {
			return;
		}

		String podCastIndex = pod.getTitle().split(" ")[2];

		config.getPrinter().println("downloading podcast: " + pod.getTitle());

		if (pdfPodCastNeedToDownload.contains(podCastIndex)) {
			pdfPodCastNeedToDownload.remove(podCastIndex);
			Tidy tidy = new Tidy();
			tidy.setXHTML(true);
			StringReader reader = new StringReader(
					"<!DOCTYPE HTML><html><body>" + pod.getContent()
							+ "</body></html>");
			StringWriter writer = new StringWriter();
			// HTML -> XHTML since iText only accept XHTML format
			tidy.parse(reader, writer);

			new EslPodCastPdfRenderer().createPDF(new StringReader(writer
					.getBuffer().toString()), storageFolder + pod.getTitle()
					+ ".pdf");
		}

		if (mp3PodCastNeedToDownload.contains(podCastIndex)) {
			mp3PodCastNeedToDownload.remove(podCastIndex);

			HttpGet method = new HttpGet(pod.getAudioLink());
			AutoRetryHttpClient httpClient = new AutoRetryHttpClient();
			HttpResponse response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
			FileOutputStream mp3File = new FileOutputStream(storageFolder
					+ pod.getTitle() + ".mp3");

			int len = 0;
			// the MP3 is about 3~10 M
			byte[] b = new byte[1024 * 1024];
			try {
				while ((len = entity.getContent().read(b, 0, 1024 * 1024)) != -1) {
					mp3File.write(b, 0, len);
				}
			} finally {
				mp3File.close();
				entity.getContent().close();
			}
		}
	}

	@Override
	public void visit(Page page) {
		try {
			storePodCast(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
