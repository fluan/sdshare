package study.sdshare.com;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzLoad {
    private static Scheduler sched; 
    private static int INTERVAL = 30;
    
    public static void run() throws Exception { 
        JobDetail jobDetail = JobBuilder.newJob(ConfigLoadJob.class)
                .withIdentity("myjob", "group1").build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("myTrigger", "group1").startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(INTERVAL).repeatForever()).build();
        /*CronTrigger trigger =(CronTrigger) TriggerBuilder.newTrigger()
                .withIdentity("trigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_EXP))
                .build();*/
        SchedulerFactory sfact = new StdSchedulerFactory();
        Scheduler sched = sfact.getScheduler();
        sched.start();
        sched.scheduleJob(jobDetail, trigger);
        System.out.println("Scheduler is running...");
    }
      
    public static void stop() throws Exception{  
    		System.out.println("Scheduler is shutdowned...");
           sched.shutdown();  
     } 
    
}
