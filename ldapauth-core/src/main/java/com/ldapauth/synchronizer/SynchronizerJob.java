package com.ldapauth.synchronizer;

import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.web.WebContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class SynchronizerJob implements Job {
	final static Logger _logger = LoggerFactory.getLogger(SynchronizerJob.class);

	SynchronizersService synchronizersService;

    public static class JOBSTATUS{
        public static int STOP = 0;
        public static int RUNNING = 1;
        public static int FINISHED = 2;
    }

    private static HashMap<Long,Integer> jobStatus = new HashMap<>();

    @Override
    public void execute(JobExecutionContext context){
    	Synchronizers synchronizer = readSynchronizer(context);
    	if(jobStatus.get(synchronizer.getId()) ==null ) {
    		//init
    		jobStatus.put(synchronizer.getId(),  JOBSTATUS.STOP) ;
    	}else if(jobStatus.get(synchronizer.getId())== JOBSTATUS.RUNNING) {
            _logger.info("SynchronizerJob is in running . " );
            return;
        }
        _logger.debug("SynchronizerJob is running ... " );
        jobStatus.put(synchronizer.getId(),  JOBSTATUS.RUNNING) ;
        try {
        	_logger.debug("synchronizer : {}",synchronizer.getId() +"("+synchronizer.getClassify()+")");
    		_logger.debug("synchronizer service : " + synchronizer.getClassify());
    		_logger.debug("synchronizer Scheduler : " + synchronizer.getCron());
        	ISynchronizerService service = (ISynchronizerService) WebContext.getBean(synchronizer.getCron());
        	service.setSynchronizer(synchronizer);
        	service.sync();
        	jobStatus.put(synchronizer.getId(),   JOBSTATUS.FINISHED);
            _logger.debug("SynchronizerJob is success  " );
        }catch(Exception e) {
            _logger.error("Exception " ,e);
            jobStatus.put(synchronizer.getId(),  JOBSTATUS.STOP);
        }
        _logger.debug("SynchronizerJob is finished . " );
    }


    public Synchronizers readSynchronizer(JobExecutionContext context) {
    	Synchronizers jobSynchronizer = (Synchronizers)context.getMergedJobDataMap().get("synchronizer");
    	if(synchronizersService == null) {
    		synchronizersService = (SynchronizersService) WebContext.getBean("synchronizersService");
    	}
    	//read synchronizer by id from database
    	Synchronizers synchronizer = synchronizersService.getById(jobSynchronizer.getId());
    	_logger.trace("synchronizer " + synchronizer);
    	return synchronizer;
    }


}
