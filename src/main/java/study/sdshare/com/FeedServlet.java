package study.sdshare.com;

import java.io.IOException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class FeedServlet
 */
@WebServlet(
		urlPatterns = { "/feed" }, 
		initParams = { 
				@WebInitParam(name = "default.feed.type", value = "atom_0.3")
		})
public class FeedServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_FEED_TYPE = "default.feed.type";
    private static final String FEED_TYPE = "type";
    private static final String MIME_TYPE = "application/xml; charset=UTF-8";
    private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";

    private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

    private String _defaultFeedType;

    public void init() {
        _defaultFeedType = getServletConfig().getInitParameter(DEFAULT_FEED_TYPE);
        _defaultFeedType = (_defaultFeedType!=null) ? _defaultFeedType : "atom_0.3";
    }

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
        try {
            SyndFeed feed = getFeed(req);

            String feedType = req.getParameter(FEED_TYPE);
            feedType = (feedType!=null) ? feedType : _defaultFeedType;
            feed.setFeedType(feedType);

            res.setContentType(MIME_TYPE);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,res.getWriter());
        }
        catch (FeedException ex) {
            String msg = COULD_NOT_GENERATE_FEED_ERROR;
            log(msg,ex);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
        }
    }

    protected SyndFeed getFeed(HttpServletRequest req) throws IOException,FeedException {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Sample Feed (created with ROME)");
        feed.setLink("http://rome.dev.java.net");
        feed.setDescription("This feed has been created using ROME (Java syndication utilities");

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v0.1");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/rome01");
        try {
            entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial release of ROME");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("Rome v0.2");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/rome02");
        try {
            entry.setPublishedDate(DATE_PARSER.parse("2004-06-16"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Bug fixes, minor API changes and some new features"+
                             "<p>For details check the <a href=\"https://rometools.jira.com/wiki/display/ROME/Change+Log#ChangeLog-Changesmadefromv0.2tov0.3\">Changes Log for 0.2</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v0.3");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/rome03");
        try {
            entry.setPublishedDate(DATE_PARSER.parse("2004-07-27"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>Bug fixes, API changes, some new features and some Unit testing</p>"+
                             "<p>For details check the <a href=\"https://rometools.jira.com/wiki/display/ROME/Change+Log#ChangeLog-Changesmadefromv0.3tov0.4\">Changes Log for 0.3</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v0.4");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/rome04");
        try {
            entry.setPublishedDate(DATE_PARSER.parse("2004-09-24"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>Bug fixes, API changes, some new features, Unit testing completed</p>"+
                             "<p>For details check the <a href=\"https://rometools.jira.com/wiki/display/ROME/Change+Log#ChangeLog-Changesmadefromv0.4tov0.5\">Changes Log for 0.4</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);

        return feed;
    }

}
