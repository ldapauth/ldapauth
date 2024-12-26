package com.ldapauth.synchronizer.autoconfigure;


import java.util.List;
import java.util.stream.Collectors;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.synchronizer.job.SynchronizerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.naming.directory.DirContext;

/**
 * 任务调度配置管理
 */
@AutoConfiguration
@Slf4j
public class SynchronizerAutoConfiguration   implements InitializingBean {

	@Bean(name = "schedulerSynchronizerJobs")
	public String schedulerSynchronizerJobs(
			SynchronizersService synchronizersService,
			SchedulerFactoryBean schedulerFactoryBean
	) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<Synchronizers> synchronizerList = querySynchronizers(synchronizersService);
		for (Synchronizers synchronizer : synchronizerList) {
			if(synchronizer.getCron()!=null
					&& !synchronizer.getCron().equals("")
					&& CronExpression.isValidExpression(synchronizer.getCron())) {
				log.debug("synchronizer details : " + synchronizer);
				buildJob(scheduler,synchronizer);
			}
		}
		return "schedulerSynchronizerJobs";
	}

	private void buildJob(Scheduler scheduler ,
						  Synchronizers synchronizer) throws SchedulerException {
		JobDetail jobDetail =
				JobBuilder.newJob(SynchronizerJob.class)
						.withIdentity(synchronizer.getId()+"Job", "SynchronizerGroups")
						.build();

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("synchronizer", synchronizer);
		log.debug("synchronizer : " + synchronizer.getClassify()+"("+synchronizer.getId()+")");
		log.debug("synchronizer Classify : " + synchronizer.getClassify());
		log.debug("synchronizer Scheduler : " + synchronizer.getCron());
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(synchronizer.getCron());
		CronTrigger cronTrigger =
				TriggerBuilder.newTrigger()
						.withIdentity("trigger"+synchronizer.getId(), "SynchronizerGroups")
						.usingJobData(jobDataMap)
						.withSchedule(scheduleBuilder)
						.build();
		scheduler.scheduleJob(jobDetail,cronTrigger);
	}



	public List<Synchronizers> querySynchronizers(SynchronizersService  synchronizersService) {
		List<Synchronizers> synchronizersList =  synchronizersService.list();
		//过滤禁用的任务
		synchronizersList = synchronizersList.stream().filter(a->a.getStatus().intValue() == 0).collect(Collectors.toList());
		return synchronizersList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}
}
