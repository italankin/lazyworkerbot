package com.italankin.lazyworker.app.backup

import org.quartz.JobDataMap

/**
 * Backup interface is used to create backups.
 */
interface Backup {

    /**
     * Create backup.
     * @param map parameters, see {@link BackupManager}
     * @return usedful info about created backup
     * @throws Exception when backup process went nuts
     */
    Info create(JobDataMap map) throws Exception

    /**
     * Result of the backup.
     */
    interface Info {
        /**
         * @return describe created backup
         */
        String toString()
    }
}