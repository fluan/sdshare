package study.sdshare.com;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class DataCollectionManager
 *
 */
@WebListener
public class SchedulerListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public SchedulerListener() {
    	 
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	try {
            QuartzLoad.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent)  { 
    	 try {
    			CollectionManager manager = CollectionManager.getCollectionManager();
    			manager.setConfigHome(servletContextEvent.getServletContext().getRealPath("/WEB-INF/datasources"));
            QuartzLoad.run();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
	
}
