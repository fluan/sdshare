package study.sdshare.com;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;

import study.sdshare.server.configuration.Datacollection;

/**
 * Servlet implementation class FragmentDataServlet
 */
@WebServlet("/fragment/*")
public class FragmentDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private int BUFF_SIZE = 1024;  
	private String DEFAULT_FORMAT = "N-TRIPLES";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FragmentDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String collectionId = getCollectionId(req);
		String entityId = req.getParameter("id");
		
		String format = req.getParameter("format");
		
		if (format == null)
			format = "nt";
		format = format.toLowerCase();
		if (format == null) {
			format = DEFAULT_FORMAT;
		}
				
		CollectionManager manager = CollectionManager.getCollectionManager();
		Datacollection config = manager.getDatacollection(collectionId);
		if (config == null) {
			throw new RuntimeException(collectionId + "doesnt exist...");
		}
		JDBCCollection jdbcColl = new JDBCCollection(config);
		Model model = jdbcColl.makeFragmentData(entityId); 
		
		ServletOutputStream out = resp.getOutputStream();
		
		if ("nt".equals(format)) {
			resp.setContentType("text/plain");
			model.write(out, "N-TRIPLE");
		} else if ("rdfxml".equals(format)) {
			resp.setContentType("application/rdf+xml");
			model.write(out, "RDF/XML");	
		} else if ("turtle".equals(format)) {
			resp.setContentType("application/x-turtle");
			model.write(out, "TURTLE");	
		} else if ("n3".equals(format)) {
			resp.setContentType("text/rdf+n3");
			model.write(out, "N3");	
		}	
	}
	
	protected String getCollectionId(HttpServletRequest req) {
		return req.getPathInfo().substring(1);
	}
}
