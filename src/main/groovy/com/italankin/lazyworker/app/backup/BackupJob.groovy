package com.italankin.lazyworker.app.backup

import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.DateFormat
import java.text.SimpleDateFormat

class BackupJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(BackupJob.class)

    static final KEY_BACKUP_DIR = "backup_dir"
    static final KEY_DB = "db"

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    private static final int BUFFER_SIZE = 10 * 1024 * 1024

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap map = context.getMergedJobDataMap()
            File db = new File(map.getString(KEY_DB))
            if (!db.exists()) {
                throw new FileNotFoundException("file not found: $db")
            }
            File backupDir = new File(map.getString(KEY_BACKUP_DIR))
            if (!backupDir.exists()) {
                if (!backupDir.mkdirs()) {
                    throw new IllegalStateException("mkdirs() failed for '$backupDir'")
                }
            }
            FileInputStream fis = new FileInputStream(db)
            File backup = new File(backupDir, getBackupFileName())
            FileOutputStream fos = new FileOutputStream(backup)
            try {
                byte[] buffer = new byte[BUFFER_SIZE]
                int read
                while ((read = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read)
                }
                fos.flush()
            } finally {
                fis.close()
                fos.close()
            }
            LOG.info("Successfully created backup: $backup")
        } catch (Exception e) {
            LOG.error("Backup failed with the exception:", e)
        }
    }

    private static String getBackupFileName() {
        "backup_" + DATE_FORMAT.format(new Date()) + ".backup"
    }

}
