package com.qxmd.qxrecyclerview;

import android.view.View;
import android.widget.TextView;

/**
 * Created by chankruse on 15-01-05.
 */
public class QxRecyclerViewHeaderItem extends QxRecyclerViewRowItem {

    public String title;

    public QxRecyclerViewHeaderItem() {
        super();
    }

    public QxRecyclerViewHeaderItem(String title) {
        super();
        this.title = title;
    }

    @Override
    public int getResourceId() {

        return R.layout.header_item_default;
    }

    @Override
    public Class<? extends  QxRecyclerRowItemViewHolder> getViewHolderClass() {
        return QxRecyclerHeaderItemViewHolderDefault.class;
    }

    @Override
    public void onBindData(QxRecyclerRowItemViewHolder viewHolder, int position, QxIndexPath indexPath, RowPosition rowPosition) {

        QxRecyclerHeaderItemViewHolderDefault mViewHolder = (QxRecyclerHeaderItemViewHolderDefault)viewHolder;

        if(title != null) {
            mViewHolder.textView.setText(title);
        }
        else {
            mViewHolder.textView.setText("");
        }
    }

    public final static class QxRecyclerHeaderItemViewHolderDefault extends QxRecyclerRowItemViewHolder {
        TextView textView;

        public QxRecyclerHeaderItemViewHolderDefault(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
