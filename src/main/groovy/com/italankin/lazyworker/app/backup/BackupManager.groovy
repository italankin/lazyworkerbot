package com.italankin.lazyworker.app.backup

import com.italankin.lazyworker.app.App
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

import java.util.Calendar

class BackupManager {

    private final Scheduler scheduler

    BackupManager(App.Config appConfig) {
        scheduler = StdSchedulerFactory.getDefaultScheduler()

        JobDataMap data = new JobDataMap()
        data.put(BackupJob.KEY_DB, appConfig.db)
        data.put(BackupJob.KEY_BACKUP_DIR, appConfig.backupDir)
        JobDetail job = JobBuilder.newJob(BackupJob.class)
                .withIdentity("backup")
                .setJobData(data)
                .build()

        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("backup")
                .startAt(calendar.getTime())
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(24))
                .build()

        scheduler.scheduleJob(job, trigger)
    }

    void start() throws Exception {
        scheduler.start()
    }

    void stop() throws Exception {
        scheduler.shutdown()
    }

}
