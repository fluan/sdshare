package study.sdshare.com;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ConfigLoadJob implements Job {
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Scheduled Job is running...");
		CollectionManager manager = CollectionManager.getCollectionManager();
	    manager.refresh();
	}
}
