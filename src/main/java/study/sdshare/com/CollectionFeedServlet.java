package study.sdshare.com;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

/**
 * Servlet implementation class OverviewFeed
 * It is processing the request './collections/collectionID'
 */
@WebServlet("/collections/*")
public class CollectionFeedServlet extends AbstractFeedServlet {
	private static final long serialVersionUID = 1L;
	
    public CollectionFeedServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	protected SyndFeed getFeed(HttpServletRequest req) throws IOException,FeedException {
		String collectionId = getCollectionId(req);
		SdshareUtil sdsharefactory = new SdshareUtil();
		
		SyndFeed feed = new SyndFeedImpl();
        feed.setTitle(collectionId + ": Data Collection");
        feed.setLink("./collections" + collectionId);
        //feed.setDescription(feedTitle + " collection");
        
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        SyndEntry fragmentsEntry;   
        fragmentsEntry = new SyndEntryImpl();
        fragmentsEntry.setTitle(collectionId + " fragments");      
        fragmentsEntry.setLinks(sdsharefactory.createFragmentsFeedLinks(collectionId));       
        try {
            fragmentsEntry.setPublishedDate(sdsharefactory.getDatetimeParser().parse("2017-06-08T00:00:00"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        SyndContent description = new SyndContentImpl();
        description.setValue(collectionId + ": data ordered by datetime");
        fragmentsEntry.setDescription(description);
        entries.add(fragmentsEntry);
        
        SyndEntry snapshotsEntry;   
        snapshotsEntry = new SyndEntryImpl();
        snapshotsEntry.setTitle(collectionId + " snapshots");      
        snapshotsEntry.setLinks(sdsharefactory.createSnapshotsFeedLinks(collectionId));       
        try {
            fragmentsEntry.setPublishedDate(sdsharefactory.getDatetimeParser().parse("2017-06-08T00:00:00"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setValue(collectionId + ": data dumps");
        snapshotsEntry.setDescription(description);
        entries.add(snapshotsEntry);
        
        feed.setEntries(entries);
		return feed;
	}

}
