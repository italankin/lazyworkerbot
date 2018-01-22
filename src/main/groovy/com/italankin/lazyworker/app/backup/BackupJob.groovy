package com.italankin.lazyworker.app.backup

import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BackupJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(BackupJob.class)

    private final List<Backup> backups = [
            new FileBackup()
            // add other implementations if needed
    ]

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap()
        backups.each { backup ->
            try {
                Backup.Info backupInfo = backup.create(map)
                if (backupInfo) {
                    LOG.info("Created backup: $backupInfo")
                }
            } catch (JobExecutionException e) {
                throw e
            } catch (Exception e) {
                LOG.error("${backup} execution failed with the exception:", e)
            }
        }
    }

}
