package study.sdshare.com;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;

import study.sdshare.server.configuration.Datacollection;

/**
 * Servlet implementation class OverviewFeed
 * It is processing the request './overview'
 */
@WebServlet("/overview")
public class OverviewFeedServlet extends AbstractFeedServlet {
	private static final long serialVersionUID = 1L;

    public OverviewFeedServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	protected SyndFeed getFeed(HttpServletRequest req) throws IOException,FeedException {

        SdshareUtil sdsharefactory = new SdshareUtil();
		SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("Sdshare Feeds Overview");
        feed.setLink("./overview");
        //feed.setDescription("Datasources overview");
        Collection<Datacollection> collections = CollectionManager.getCollectionManager().getAllCollections();
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (Datacollection coll : collections) {
        		SyndEntry entry = new SyndEntryImpl();
            String feedId = coll.getId();
            String feedDescription = coll.getDescription();
        		entry.setTitle(feedId);
            entry.setLinks(sdsharefactory.createCollectionLinks(feedId));
            SyndContent description = new SyndContentImpl();
            description.setValue(feedDescription);
            entry.setDescription(description);
            entries.add(entry);
        }    
        
        feed.setEntries(entries);
		return feed;
	}

}
