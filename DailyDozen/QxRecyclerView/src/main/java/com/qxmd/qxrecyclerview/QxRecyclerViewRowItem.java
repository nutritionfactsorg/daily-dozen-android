package com.qxmd.qxrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chankruse on 15-01-05.
 */
public class QxRecyclerViewRowItem {

    public enum RowPosition {
        SINGLE,
        TOP,
        MIDDLE,
        BOTTOM,
    }

    public String id;
    public String title;

    public QxRecyclerViewRowItem parentItem;
    public List<QxRecyclerViewRowItem> children;

    public boolean isExpanded   = false;
    public int indentLevel      = 0;
    public int childIndex       = 0;

    public int sortingWeight = 0;

    public QxRecyclerViewRowItem() {
    }

    public QxRecyclerViewRowItem(String title) {
        this.title = title;
    }

    public boolean hasChildren() {
        if (children == null || children.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    public String getId() {
        return id;
    }

    //used for recyclerview ids
    public long getItemId() {
        return super.hashCode();
    }

    public void addChild(QxRecyclerViewRowItem childRowItem) {

        if (children == null) {
            children = new ArrayList<>();
        }

        while (childRowItem.indentLevel <= indentLevel) {
            childRowItem.increaseIndent();
        }

        childRowItem.childIndex = children.size();

        children.add(childRowItem);

        childRowItem.parentItem = this;
    }


    public void insertChildren(int location, List<QxRecyclerViewRowItem> childRowItem) {

    }

    public void insertChild(int location, QxRecyclerViewRowItem childRowItem) {

        if (children == null) {
            children = new ArrayList<>();
        }

        while (childRowItem.indentLevel <= indentLevel) {
            childRowItem.increaseIndent();
        }

        childRowItem.childIndex = location;

        if (location < children.size()) {
            for (int i = location; i < children.size(); i++) {
                children.get(i).childIndex++;
            }
        }

        children.add(location, childRowItem);

        childRowItem.parentItem = this;
    }

    public void deleteChild(QxRecyclerViewRowItem childRowItem) {
        int index = children.indexOf(childRowItem);
        children.remove(childRowItem);

        for (int i=index; i<children.size(); i++) {
            children.get(i).childIndex--;
        }
    }

    public void increaseIndent() {
        indentLevel++;

        if (children != null) {

            for(QxRecyclerViewRowItem childRowItem : children) {
                childRowItem.increaseIndent();
            }
        }
    }

    public int getResourceId() {

        return R.layout.list_item_default;
    }

    public Class<? extends  QxRecyclerRowItemViewHolder> getViewHolderClass() {
        return QxRecyclerRowItemViewHolderDefault.class;
    }

    public void onBindData(QxRecyclerRowItemViewHolder viewHolder, int position, QxIndexPath indexPath, RowPosition rowPosition) {

        QxRecyclerRowItemViewHolderDefault mViewHolder = (QxRecyclerRowItemViewHolderDefault)viewHolder;

        if(title != null) {
            mViewHolder.textView.setText(title);
        }
        else {
            mViewHolder.textView.setText("");
        }
    }

    public final static class QxRecyclerRowItemViewHolderDefault extends QxRecyclerRowItemViewHolder {
        TextView textView;

        public QxRecyclerRowItemViewHolderDefault(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
