package study.sdshare.com;


import org.apache.jena.rdf.model.Model;

import com.rometools.rome.feed.synd.SyndFeed;




public interface IFeedData {
	public SyndFeed makeFragmentsFeed(String since, String before);
	public Model makeFragmentData(String entryId);
}
