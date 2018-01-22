package com.italankin.lazyworker.app.backup

import org.quartz.JobDataMap

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Simple backup to file.
 */
class FileBackup implements Backup {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    private static final int BUFFER_SIZE = 10 * 1024 * 1024

    @Override
    Backup.Info create(JobDataMap map) throws Exception {
        File db = new File(map.getString(BackupManager.KEY_DB))
        if (!db.exists()) {
            throw new FileNotFoundException("file not found: $db")
        }
        File backupDir = new File(map.getString(BackupManager.KEY_BACKUP_DIR))
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
        return new Info(backup)
    }

    private static String getBackupFileName() {
        "backup_" + DATE_FORMAT.format(new Date()) + ".backup"
    }

    class Info implements Backup.Info {
        File createdBackup

        Info(File createdBackup) {
            this.createdBackup = createdBackup
        }

        @Override
        String toString() {
            return createdBackup.toString()
        }
    }
}
