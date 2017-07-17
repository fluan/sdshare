package study.sdshare.com;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

public abstract class AbstractFeedServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2066818412537078518L;
	protected static final String DEFAULT_MIME_TYPE = "application/atom+xml; charset=UTF-8";
	protected static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";
	protected static final String FEED_TYPE = "atom_0.3";

	//protected static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AbstractFeedServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected String getCollectionId(HttpServletRequest req) {
		return req.getPathInfo().substring(1);
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			SyndFeed feed = getFeed(req);
			feed.setFeedType(FEED_TYPE);
			res.setContentType(DEFAULT_MIME_TYPE);
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, res.getWriter());
		} catch (FeedException ex) {
			String msg = COULD_NOT_GENERATE_FEED_ERROR;
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
		}
	}

	protected abstract SyndFeed getFeed(HttpServletRequest req) throws IOException, FeedException;
}
