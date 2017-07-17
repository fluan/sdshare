package study.sdshare.com;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndLinkImpl;

public class SdshareUtil {
	private static final String DEFAULT_ENTRY_MIME_TYPE = "application/atom+xml";
	private static final String DEFAULT_ALTERNATE = "alternate";
	private static final String SDSHARE_COLLECTIONS_URL = "http://www.sdshare.org/2012/core/collectionfeed";
	private static final String SDSHARE_FRAGMENTS_URL = "http://www.sdshare.org/2012/core/fragmentsfeed";
	private static final String SDSHARE_SNAPSHOTS_URL = "http://www.sdshare.org/2012/core/snapshotsfeed";
	private static final String SDSHARE_FRAGMENT_URL = "http://www.sdshare.org/2012/core/fragmentfeed";
	private static final String OVERVIEW_URI = "./collections/";
	private static final String FRAGMENTS_URI = "../fragments/";
	private static final String SNAPSHOTS_URI = "../snapshots/";
	private static final String FRAGMENT_URI = "../fragment/";

	public List<SyndLink> createCollectionLinks(String url) {
		return getLinks(SDSHARE_COLLECTIONS_URL, OVERVIEW_URI + url);
	}

	public List<SyndLink> createFragmentsFeedLinks(String collectionId) {
		return getLinks(SDSHARE_FRAGMENTS_URL, FRAGMENTS_URI + collectionId);
	}

	public List<SyndLink> createSnapshotsFeedLinks(String collectionId) {
		return getLinks(SDSHARE_SNAPSHOTS_URL, SNAPSHOTS_URI + collectionId);
	}
	public List<SyndLink> createFragmentFeedLinks(String collectionId, String entityId) {
		return getLinks(SDSHARE_FRAGMENT_URL, FRAGMENT_URI + collectionId +"?id=" + entityId);
	}
	private List<SyndLink> getLinks(String rel, String href) {
		List<SyndLink> links = new ArrayList<SyndLink>();
		SyndLink link = new SyndLinkImpl();
		link.setType(DEFAULT_ENTRY_MIME_TYPE);
		link.setRel(rel);
		link.setHref(href);
		links.add(link);
		SyndLink altLink = new SyndLinkImpl();
		altLink.setType(DEFAULT_ENTRY_MIME_TYPE);
		altLink.setRel(DEFAULT_ALTERNATE);
		altLink.setHref(href);
		links.add(altLink);
		return links;
	}

	public SimpleDateFormat getDatetimeParser() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	public SyndEntry createFragmentsEntry(String id, String title, List<SyndLink> links, String description,
			String updated) {
		SyndEntry entry;

		entry = new SyndEntryImpl();
		entry.setTitle(title != null ? title : id);
		entry.setLinks(links);
		try {
			entry.setPublishedDate(getDatetimeParser().parse(updated));
		} catch (ParseException ex) {
			// IT CANNOT HAPPEN WITH THIS SAMPLE
		}
		SyndContent content = new SyndContentImpl();
		content.setValue(description);
		entry.setDescription(content);

		return entry;
	}

}
