package org.nutritionfacts.dailydozen.event;

public class BackupCompleteEvent extends BaseTaskEvent {
    public BackupCompleteEvent(boolean success) {
        super(success);
    }
}
