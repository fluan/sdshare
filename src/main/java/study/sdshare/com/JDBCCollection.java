package study.sdshare.com;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndLink;

import study.sdshare.server.configuration.Datacollection;
import study.sdshare.server.configuration.Datacollection.Database;
import study.sdshare.server.configuration.Datacollection.Rdf.Triple;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class JDBCCollection implements IFeedData {
	private static final String SINCE = ":since";
	private static final String BEFORE = ":before";
	private static final String ID = ":id";
	
	private Database dbConfig;
	private String subNamespace;
	private String preNamespace;
	private String collectionId;
	private String idColumn;
	private String fragmentsQuery;
	private String fragmentQuery;
	private String snapshotQuery;
	private List<Triple> rdfStmts;
	
	public JDBCCollection(Datacollection collectionConfig) {
		collectionId = collectionConfig.getId();
		dbConfig = collectionConfig.getDatabase();
		idColumn = collectionConfig.getIdColumn();
		subNamespace = collectionConfig.getRdf().getSNs();
		preNamespace = collectionConfig.getRdf().getPNs();
		rdfStmts = collectionConfig.getRdf().getTriple();
		fragmentsQuery = collectionConfig.getFragmentsQuery();
		fragmentQuery = collectionConfig.getFragmentQuery();
		snapshotQuery = collectionConfig.getSnapshotQuery();
	}

	@Override
	public SyndFeed makeFragmentsFeed(String since, String before) {
		SyndFeed feed = new SyndFeedImpl();
		SdshareUtil util = new SdshareUtil();
		feed.setTitle(collectionId + ": Fragments Collection");
		feed.setLink("./fragments/" + collectionId);

		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		try {
			Class.forName(dbConfig.getJdbcdriver());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(dbConfig.getConnectionstring(), dbConfig.getUser(),
					dbConfig.getPassword());

			Statement st = conn.createStatement();
			String fragments_sql = fragmentsQuery.replaceAll(SINCE, since).replaceAll(BEFORE,
					before);
			ResultSet rs = st.executeQuery(fragments_sql);

			while (rs.next()) {
				String id = rs.getString("id");
				// String title = rs.getString("title");
				String updated = rs.getString("updated");
				// String desc = rs.getString("permalink");
				List<SyndLink> links = util.createFragmentFeedLinks(collectionId, id);

				SyndEntry entry = util.createFragmentsEntry(id, id, links, id, updated);

				entries.add(entry);
			}
			feed.setEntries(entries);
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return feed;
	}

	@Override
	public Model makeFragmentData(String entryId) {
		
		Connection conn = null;
		Model model = ModelFactory.createDefaultModel();
		try {
			conn = DriverManager.getConnection(dbConfig.getConnectionstring(), dbConfig.getUser(),
					dbConfig.getPassword());

			Statement st = conn.createStatement();
			String fragment_sql = fragmentQuery.replaceAll(ID, entryId);
			ResultSet rs = st.executeQuery(fragment_sql);
			Map<String, Integer> dataTypes = getColumnInfo(rs);
			Map<String, String> data;
			while (rs.next()) {
				data = getStringData(rs, dataTypes);
				Resource resource = model.createResource(subNamespace + data.get(idColumn));
				addBasicProperties(resource, data, dataTypes);
				addDefinedProperties(resource, data);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return model;
	}

	private void addDefinedProperties(Resource resource, Map<String, String> data) {
		for (Triple t : rdfStmts) {
			String predicate = t.getPredicate();
			predicate = transform(predicate, data);
			Property p = null; 
			if (isValidUri(predicate)) {
				p = ResourceFactory.createProperty(predicate);
			} else {
				p = ResourceFactory.createProperty(preNamespace + predicate);
			}
			
			String object = t.getObject().getValue();
			object = transform(object, data);
			String oType = t.getObject().getType();
			if (oType == null) {
				resource.addProperty(p, ResourceFactory.createTypedLiteral(object, XSDDatatype.XSDstring));
			} else if (oType.equalsIgnoreCase("iri")) {
				resource.addProperty(p, ResourceFactory.createResource(object));
			} else {
				resource.addProperty(p, ResourceFactory.createTypedLiteral(object, getXSDType(oType)));
			}

		}
	}

	private boolean isValidUri(String uri) {
		URL url;
		try {
			url = new URL(uri);
		} catch (Exception e1) {
			return false;
		}
		return "http".equals(url.getProtocol()) || "https".equals(url.getProtocol());
	}

	private String transform(String str, Map<String, String> data) {
		Pattern pattern = Pattern.compile("(\\{\\{ )(.*?)( \\}\\})");
		Matcher matcher = pattern.matcher(str);
		List<String> properties = new ArrayList<String>();

		while (matcher.find()) {
			String key = matcher.group(2);
			if (!properties.contains(key))
				properties.add(key);
		}

		for (String p : properties) {
			if (!data.containsKey(p))
				return null;
			str = str.replaceAll(String.format("\\{\\{ %s \\}\\}", p), data.get(p));
		}
		return str;
	}

	private void addBasicProperties(Resource resource, Map<String, String> data,
			Map<String, Integer> types) {
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String p_name = entry.getKey();
			String value = entry.getValue();
			XSDDatatype dType = getXSDType(types.get(p_name));
			Property p = ResourceFactory.createProperty(preNamespace, p_name);
			Literal o = ResourceFactory.createTypedLiteral(value, dType);
			resource.addProperty(p, o);
		}
	}

	private XSDDatatype getXSDType(Integer type) {
		XSDDatatype dtype = null;
		switch (type) {
		case Types.SMALLINT:
		case Types.INTEGER:
			dtype = XSDDatatype.XSDinteger;
			break;
		case Types.FLOAT:
		case Types.REAL:
			dtype = XSDDatatype.XSDfloat;
			break;
		case Types.DECIMAL:
			dtype = XSDDatatype.XSDdecimal;
			break;
		case Types.BOOLEAN:
			dtype = XSDDatatype.XSDboolean;
			break;
		case Types.DATE:
			dtype = XSDDatatype.XSDdate;
			break;
		case Types.TIME:
			dtype = XSDDatatype.XSDtime;
			break;
		case Types.TIMESTAMP:
			dtype = XSDDatatype.XSDdateTime;
			break;
		case Types.BIGINT:
			dtype = XSDDatatype.XSDlong;
			break;
		default:
			dtype = XSDDatatype.XSDstring;
		}
		return dtype;
	}

	private XSDDatatype getXSDType(String type) {
		XSDDatatype dtype = null;
		if (type.equalsIgnoreCase("xsd:anyURI"))
			dtype = XSDDatatype.XSDanyURI;
		else if (type.equalsIgnoreCase("xsd:integer"))
			dtype = XSDDatatype.XSDinteger;
		else if (type.equalsIgnoreCase("xsd:float"))
			dtype = XSDDatatype.XSDfloat;
		else if (type.equalsIgnoreCase("xsd:decimal"))
			dtype = XSDDatatype.XSDdecimal;
		else if (type.equalsIgnoreCase("xsd:boolean"))
			dtype = XSDDatatype.XSDboolean;
		else if (type.equalsIgnoreCase("xsd:date"))
			dtype = XSDDatatype.XSDdate;
		else if (type.equalsIgnoreCase("xsd:time"))
			dtype = XSDDatatype.XSDtime;
		else if (type.equalsIgnoreCase("xsd:dateTime"))
			dtype = XSDDatatype.XSDdateTime;
		else if (type.equalsIgnoreCase("xsd:long"))
			dtype = XSDDatatype.XSDlong;
		else
			dtype = XSDDatatype.XSDstring;

		return dtype;
	}

	private Map<String, String> getStringData(ResultSet rs, Map<String, Integer> columnInfo) {
		Map<String, String> data = new HashMap<String, String>(columnInfo.size());
		columnInfo.forEach((k, t) -> {
			try {
				data.put(k, getStringValue(rs, k, t));
			} catch (SQLException e) {
				throw new RuntimeException(String.format("Column %s: Faild to get its value...", k));
			}
		});
		return data;
	}

	private String getStringValue(ResultSet rs, String column, Integer t) throws SQLException {
		// TODO: do StringFromat
		return rs.getString(column);
	}

	private Map<String, Integer> getColumnInfo(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int size = rsmd.getColumnCount();
		Map<String, Integer> columnInfo = new HashMap<String, Integer>(size);
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			columnInfo.put(rsmd.getColumnName(i), rsmd.getColumnType(i));
		}
		return columnInfo;
	}
}
