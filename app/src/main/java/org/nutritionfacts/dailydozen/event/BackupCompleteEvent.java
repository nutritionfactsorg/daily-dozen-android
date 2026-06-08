package org.nutritionfacts.dailydozen.event;

import java.io.File;

public class BackupCompleteEvent extends BaseTaskEvent {
    private final File backupFile;

    public BackupCompleteEvent(final boolean success, final File backupFile) {
        super(success);
        this.backupFile = backupFile;
    }

    public File getBackupFile() {
        return backupFile;
    }
}
