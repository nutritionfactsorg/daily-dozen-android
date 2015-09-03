package com.qxmd.qxrecyclerview;

/**
 * Created by chankruse on 15-01-06.
 */
public class QxIndexPath {

    public int section;
    public int row;

    public QxIndexPath() {
        super();
    }

    public QxIndexPath(int row, int section) {
        super();

        this.row = row;
        this.section = section;
    }
}
