package study.sdshare.com;


import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import study.sdshare.server.configuration.Datacollection;

public class CollectionManager {
	private static CollectionManager instance = null;
	Map<String, Datacollection> collConfigs;
	private String configHome = "./";

	protected CollectionManager() {
		collConfigs = new HashMap<String, Datacollection>();
	}

	public static CollectionManager getCollectionManager() {
		if (instance == null) {
			instance = new CollectionManager();
		}
		return instance;
	}
	
	public void setConfigHome(String home) {
		configHome = home;
	}
	
	public Collection<Datacollection> getAllCollections() {
		return collConfigs.values();
	}
	
	public Datacollection getDatacollection(String collectionId) {
		return collConfigs.get(collectionId);
	}

	public void refresh() {
		System.out.println("Refreshing Configs");
		File home = new File(configHome);
		File[] fList = home.listFiles();
		for (File f : fList) {	
			if (f.isFile() && getFileExtension(f).toLowerCase().equals("xml")) {
				loadConfig(f);
			}
		}
	}
	
	private String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
	
	private void loadConfig(File file) {
		try {				 
			JAXBContext jaxbContext = JAXBContext.newInstance(Datacollection.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Datacollection config = (Datacollection) jaxbUnmarshaller.unmarshal(file);
			String id = config.getId();		
			try {
				Class.forName(config.getDatabase().getJdbcdriver());
				collConfigs.put(id, config);
			} catch (ClassNotFoundException e) {
				System.out.println(String.format("ERROR: Missing Driver %s...", config.getDatabase().getJdbcdriver()));
				e.printStackTrace();
			}
			
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
