package com.qxmd.qxrecyclerview;

/**
 * Created by chankruse on 15-01-06.
 */
public class QxRecyclerViewRowItemWrapper {

    public QxRecyclerViewRowItem rowItem;
    public QxIndexPath indexPath;
    public QxRecyclerViewRowItem.RowPosition rowPosition;

    public QxRecyclerViewRowItemWrapper(QxRecyclerViewRowItem rowItem) {
        super();
        this.rowItem = rowItem;
        indexPath = new QxIndexPath();
    }
}
