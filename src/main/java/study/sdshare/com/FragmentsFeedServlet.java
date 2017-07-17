package study.sdshare.com;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;

import study.sdshare.server.configuration.Datacollection;

/**
 * Servlet implementation class fragmentsFeed
 * It is processing the request './fragments/collectionID'
 */
@WebServlet("/fragments/*")
public class FragmentsFeedServlet extends AbstractFeedServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see AbstractFeedServlet#AbstractFeedServlet()
     */
    public FragmentsFeedServlet() {
        super();
    }

	@Override
	protected SyndFeed getFeed(HttpServletRequest req) throws IOException, FeedException {
		String collectionId = getCollectionId(req);		
		String since = req.getParameter("since");
		String before = req.getParameter("before");
		
		CollectionManager manager = CollectionManager.getCollectionManager();
		//manager.setConfigHome(getServletContext().getRealPath("/WEB-INF/datasources"));
		//manager.refresh();
		Datacollection config = manager.getDatacollection(collectionId);
		if (config == null) {
			throw new RuntimeException(collectionId + " doesnt exist");
		}
		//TODO: support more
		JDBCCollection jdbcColl = new JDBCCollection(config);
		
		if (since == null) {
			since = "1900-01-01T00:00:00";
		}
		if (before == null) {
			before = "2999-01-01T00:00:00";
		}
		
		return jdbcColl.makeFragmentsFeed(since, before);	
	}

}
