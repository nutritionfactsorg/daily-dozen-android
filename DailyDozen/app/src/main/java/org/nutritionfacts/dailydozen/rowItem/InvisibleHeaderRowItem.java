package org.nutritionfacts.dailydozen.rowItem;

import android.view.View;

import com.qxmd.qxrecyclerview.QxIndexPath;
import com.qxmd.qxrecyclerview.QxRecyclerRowItemViewHolder;
import com.qxmd.qxrecyclerview.QxRecyclerViewHeaderItem;

import org.nutritionfacts.dailydozen.R;

/**
 * Created by chankruse on 15-09-03.
 */
public class InvisibleHeaderRowItem extends QxRecyclerViewHeaderItem {

    public InvisibleHeaderRowItem() {
        super();
    }

    @Override
    public int getResourceId() {
        return R.layout.header_row_item_invisible;
    }

    @Override
    public Class<? extends QxRecyclerRowItemViewHolder> getViewHolderClass() {
        return InvisibleHeaderViewHolder.class;
    }

    @Override
    public void onBindData(QxRecyclerRowItemViewHolder viewHolder, int position, QxIndexPath indexPath, RowPosition rowPosition) {

    }

    public final static class InvisibleHeaderViewHolder extends QxRecyclerRowItemViewHolder {

        public InvisibleHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
