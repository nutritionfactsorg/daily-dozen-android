package com.qxmd.qxrecyclerview;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chankruse on 15-01-05.
 */
public class QxRecyclerViewAdapter extends RecyclerView.Adapter<QxRecyclerRowItemViewHolder> implements View.OnClickListener {

    public interface OnRecyclerViewRowItemClickedListener {
        void onRecyclerViewRowItemClicked(QxRecyclerViewRowItem rowItem, QxRecyclerViewAdapter adapter, View view, int position);
    }

    public interface OnRecyclerViewRowItemExpandCollapseListener {
        void onRecyclerViewRowItemExpanded(QxRecyclerViewRowItem rowItem, QxRecyclerViewAdapter adapter, View view, int position);
        void onRecyclerViewRowItemCollapsed(QxRecyclerViewRowItem rowItem, QxRecyclerViewAdapter adapter, View view, int position);
    }

    private List<QxRecyclerViewRowItemWrapper> wrappedRowItems = new ArrayList<>();
    private List<QxRecyclerViewHeaderItem> headerItems = new ArrayList<>();
    private List<Integer> sectionsCounts = new ArrayList<>();

    private Map<Integer, Class<? extends QxRecyclerRowItemViewHolder>> viewTypeMap = new HashMap<>();

    private boolean hasBeenInitialized = false;
    private RecyclerView recyclerView;
    public boolean shouldTrackRowPositions = false;
    public boolean shouldDelaySelection = true;

    public QxRecyclerViewAdapter() {
        super.setHasStableIds(true);
    }

    private OnRecyclerViewRowItemClickedListener onRecyclerViewRowItemClickedListener;
    private OnRecyclerViewRowItemExpandCollapseListener onRecyclerViewRowItemExpandCollapseListener;

    public void setOnClickListener(OnRecyclerViewRowItemClickedListener listener) {
        onRecyclerViewRowItemClickedListener = listener;
    }

    public void setOnExpandCollapseListener(OnRecyclerViewRowItemExpandCollapseListener listener) {
        onRecyclerViewRowItemExpandCollapseListener = listener;
    }

    public void setAdapterData(Map<String, List<? extends QxRecyclerViewRowItem>> adapterDataMap) {
        hasBeenInitialized = true;

        wrappedRowItems = new ArrayList<>();
        headerItems = new ArrayList<>();
        sectionsCounts = new ArrayList<Integer>();

        viewTypeMap = new HashMap<>();

        for (Map.Entry<String, List<? extends QxRecyclerViewRowItem>> entry : adapterDataMap.entrySet()) {

            addSectionWithTitle(entry.getKey(), entry.getValue());
        }
    }

    public void reset() {
        hasBeenInitialized = false;

        wrappedRowItems = new ArrayList<>();
        headerItems = new ArrayList<>();
        sectionsCounts = new ArrayList<>();

        viewTypeMap = new HashMap<>();
    }

    public boolean getHasBeenInitialized() {
        return hasBeenInitialized;
    }

    public void addSectionWithTitle(String title, List<? extends QxRecyclerViewRowItem> sectionItems) {

        QxRecyclerViewHeaderItem headerItem = new QxRecyclerViewHeaderItem(title);

        addSectionWithHeaderItem(headerItem, sectionItems);
    }

    public List<QxRecyclerViewRowItem> getRowItems() {
        List<QxRecyclerViewRowItem> rowItems = new ArrayList<>();

        for (QxRecyclerViewRowItemWrapper wrappedItem : wrappedRowItems) {
            rowItems.add(wrappedItem.rowItem);
        }

        return rowItems;
    }

    public List<QxRecyclerViewRowItem> getTopLevelRowItems() {
        List<QxRecyclerViewRowItem> rowItems = new ArrayList<>();

        for (QxRecyclerViewRowItemWrapper wrappedItem : wrappedRowItems) {
            if (!(wrappedItem.rowItem instanceof QxRecyclerViewHeaderItem) &&
                    wrappedItem.rowItem.indentLevel == 0) {
                rowItems.add(wrappedItem.rowItem);
            }
        }

        return rowItems;
    }

    public void replaceRowItems(int position, int length, List<? extends QxRecyclerViewRowItem> sectionItems) {

        //first let's delete the previous items
    }

    public void removeItems(int position, int length) {

    }

    public int getSectionCount() {
        return sectionsCounts.size();
    }

    public void removeDataFromSection(int section) {

        int updateSectionsStartingAtIndex = section + 1;
        int positionOfFirstItemInSection = 0;
        int sectionCount = getItemCountForSection(section);

        QxRecyclerViewRowItemWrapper rowItemWrapper;

        for (int i=0; i<wrappedRowItems.size(); i++) {
            rowItemWrapper = wrappedRowItems.get(i);

            if(rowItemWrapper.indexPath.section == section) {
                positionOfFirstItemInSection = i;

                break;
            }
        }

        for (int i=0; i<sectionCount; i++) {
            wrappedRowItems.remove(positionOfFirstItemInSection);
        }

        sectionsCounts.remove(section);

        if (updateSectionsStartingAtIndex < sectionsCounts.size()) {

            for (int i=positionOfFirstItemInSection; i<wrappedRowItems.size();i++) {

                rowItemWrapper = wrappedRowItems.get(i);
                rowItemWrapper.indexPath.section--;
            }

        }
    }

    public int getItemCountForSection(int section) {
        return sectionsCounts.get(section)+1;
    }

    public void addSectionWithHeaderItem(QxRecyclerViewHeaderItem headerItem, List<? extends QxRecyclerViewRowItem> sectionItems) {

        hasBeenInitialized = true;

        int viewType;

        List<QxRecyclerViewRowItemWrapper> sectionWrappedRowItems = new ArrayList<>(sectionItems.size());

        QxRecyclerViewRowItemWrapper wrapper;

        /*
        TODO: proper implementation of sticky headers. this is just a hack to give us non-sticky headers
        note: getItemCountForSection currently adds an additional count for the header
        */

        wrapper = new QxRecyclerViewRowItemWrapper(headerItem);
        wrapper.indexPath.section = sectionsCounts.size();
        wrapper.indexPath.row = -1;
        wrappedRowItems.add(wrapper);
        sectionWrappedRowItems.add(wrapper);

        viewType = headerItem.getResourceId();

        if (!viewTypeMap.containsKey(viewType)) {
            viewTypeMap.put(viewType, headerItem.getViewHolderClass());
        }

        //TODO: end


        int row = 0;

        for (QxRecyclerViewRowItem rowItem : sectionItems) {

            wrapper = new QxRecyclerViewRowItemWrapper(rowItem);
            wrapper.indexPath.section = sectionsCounts.size();
            wrapper.indexPath.row = row;

            wrappedRowItems.add(wrapper);
            sectionWrappedRowItems.add(wrapper);

            viewType = rowItem.getResourceId();

            if (!viewTypeMap.containsKey(viewType)) {
                viewTypeMap.put(viewType, rowItem.getViewHolderClass());
            }

            row++;
        }

        updateRowPositionsForSectionItems(sectionWrappedRowItems);

        sectionsCounts.add(sectionItems.size());
    }

    private void updateRowPositionsForSectionItems(List<QxRecyclerViewRowItemWrapper> sectionWrappedRowItems) {
        int row = 0;

        for (QxRecyclerViewRowItemWrapper wrapper : sectionWrappedRowItems) {

            if (sectionWrappedRowItems.size() == 2) { // 2 because the header is the first item
                wrapper.rowPosition = QxRecyclerViewRowItem.RowPosition.SINGLE;
            } else if (row == sectionWrappedRowItems.size() - 1) {
                wrapper.rowPosition = QxRecyclerViewRowItem.RowPosition.BOTTOM;
            } else if (row == 1) { //1 beceause 0 is the header
                wrapper.rowPosition = QxRecyclerViewRowItem.RowPosition.TOP;
            } else {
                wrapper.rowPosition = QxRecyclerViewRowItem.RowPosition.MIDDLE;
            }

            row++;
        }
    }

    //we need to pass in the section in the case that an empty section wants rows to be inserted
    public List<QxRecyclerViewRowItemWrapper> insertRowsAtIndex(List<QxRecyclerViewRowItem> rowItems, int index, int section) {

        //let's make sure that the passed in section matches up with the index
        int sectionOfIndex;

        if (index < wrappedRowItems.size()) {
            sectionOfIndex = wrappedRowItems.get(index).indexPath.section;

            if (sectionOfIndex != section) {

                //maybe we are trying to insert at very end of 1 section, so the index is actually
                //at the beginning of the next section, or in an empty section
                //
                //let's find the section for the previous row
                if (index > 0) {
                    int sectionOfPreviousIndex = wrappedRowItems.get(index - 1).indexPath.section;

                    if (!(section < sectionOfIndex && section >= sectionOfPreviousIndex)) {
                        throw new IllegalArgumentException("Incorrect section in insertRowsAtIndex");
                    }
                } else {
                    throw new IllegalArgumentException("Incorrect section in insertRowsAtIndex");
                }
            }
        }
        else if (index == wrappedRowItems.size()) {
            if (section >= sectionsCounts.size()) {
                throw new IllegalArgumentException("Incorrect section in insertRowsAtIndex: cannot insert to section beyond section count");
            }
        }
        else {
            throw new IllegalArgumentException("Insertion index cannot be greater than list size");
        }

        QxRecyclerViewRowItemWrapper wrapper = null;

        List<QxRecyclerViewRowItemWrapper> wrappedRowItemsToInsert = new ArrayList<>(rowItems.size());

        int viewType;

        for (QxRecyclerViewRowItem rowItem : rowItems) {

            wrapper = new QxRecyclerViewRowItemWrapper(rowItem);
            wrapper.indexPath.section = section;

            viewType = rowItem.getResourceId();

            if (!viewTypeMap.containsKey(viewType)) {
                viewTypeMap.put(viewType, rowItem.getViewHolderClass());
            }

            wrappedRowItemsToInsert.add(wrapper);
        }

        int oldSectionCount = sectionsCounts.get(section);
        int updatedSectionCount = oldSectionCount + rowItems.size();

        wrappedRowItems.addAll(index, wrappedRowItemsToInsert);

        sectionsCounts.set(section, updatedSectionCount);

        List<QxRecyclerViewRowItemWrapper> sectionWrappedRowItems = getWrappedRowItemsForSection(section);
        updateRowPositionsForSectionItems(sectionWrappedRowItems);

        notifyItemRangeInserted(index, wrappedRowItemsToInsert.size());

        return wrappedRowItemsToInsert;
    }

    public void deleteRowsAtIndex(int index, int length, int section) {

        if ( index+length > wrappedRowItems.size() ) {
            throw new IllegalArgumentException("length of items to delete exceeds rowItems size");
        }

        int indexOfLastItemToDelete = index + length - 1;

        //let's make sure that the passed in section matches up with the index
        int sectionOfFirstItemToDelete = wrappedRowItems.get(index).indexPath.section;
        int sectionOfLastItemToDelete = wrappedRowItems.get(indexOfLastItemToDelete).indexPath.section;

        if (sectionOfFirstItemToDelete != sectionOfLastItemToDelete) {
            throw new IllegalArgumentException("Can't delete rows across multiple sections");
        }

        for(int i=0; i<length; i++) {
            wrappedRowItems.remove(index);
        }

        int oldSectionCount = sectionsCounts.get(section);
        int updatedSectionCount = oldSectionCount - length;

        sectionsCounts.set(section, updatedSectionCount);

        List<QxRecyclerViewRowItemWrapper> sectionWrappedRowItems = getWrappedRowItemsForSection(section);
        updateRowPositionsForSectionItems(sectionWrappedRowItems);

        notifyItemRangeRemoved(index, length);
    }

    public void expandChildrenContainedInExpandedIds(List<QxRecyclerViewRowItemWrapper> wrappedRowItemsToCheck, List<String> expandedIds) {

        if (expandedIds == null || expandedIds.isEmpty()) {
            return;
        }

        if (wrappedRowItemsToCheck == null) {
            wrappedRowItemsToCheck = new ArrayList<>(wrappedRowItems);
        }

        for (QxRecyclerViewRowItemWrapper wrappedRowItem : wrappedRowItemsToCheck) {
            if (expandedIds.contains(wrappedRowItem.rowItem.getId())) {

                // if the rowItems are still in memory, then we just need to make sure that we
                // expand all rowItems where 'isExpanded' is true (we need to reset this boolean
                // to false for the top level items, since the 'expand...' method checks this
                // value to make sure the item isn't expanded before expanding
                if (wrappedRowItem.rowItem.isExpanded) {
                    wrappedRowItem.rowItem.isExpanded = false;

                    expandRowItemAtPosition(wrappedRowItems.indexOf(wrappedRowItem));
                }
                else {

                    // if, if the app was force closed in the bkg state, then when it re-opens and
                    // tries to restore savedInstanceState, then the rowItems array will be rebuilt from
                    // scratch, and so no top-level or child rowItem will be expanded so we need
                    // to recursively call this method on all children to expand each one manually as needed
                    List<QxRecyclerViewRowItemWrapper> expandedRowItems = expandRowItemAtPosition(wrappedRowItems.indexOf(wrappedRowItem));

                    expandChildrenContainedInExpandedIds(expandedRowItems, expandedIds);
                }
            }
        }
    }

    public List<QxRecyclerViewRowItemWrapper> expandRowItemAtPosition(int position) {
        QxRecyclerViewRowItemWrapper wrappedRowItem = wrappedRowItems.get(position);

        if(wrappedRowItem.rowItem.isExpanded) {
            return null;
        }

        if(!wrappedRowItem.rowItem.hasChildren()) {
            return null;
        }

        List<QxRecyclerViewRowItem> rowItemsToInsert = findRowsToBeInsertedForExpandingRowItem(wrappedRowItem.rowItem);

        List<QxRecyclerViewRowItemWrapper> wrappedRowItemsToInsert = insertRowsAtIndex(rowItemsToInsert, position + 1, wrappedRowItem.indexPath.section);

        wrappedRowItem.rowItem.isExpanded = true;

        return wrappedRowItemsToInsert;
    }

    public List<QxRecyclerViewRowItem> findRowsToBeInsertedForExpandingRowItem(QxRecyclerViewRowItem rowItem) {

        List<QxRecyclerViewRowItem> rowItemsToAdd = new ArrayList<QxRecyclerViewRowItem>();
        QxRecyclerViewRowItem childRowItem;

        for (int i=0; i<rowItem.children.size(); i++) {

            childRowItem = rowItem.children.get(i);

            rowItemsToAdd.add(childRowItem);

            if(childRowItem.hasChildren() && childRowItem.isExpanded) {
                rowItemsToAdd.addAll(findRowsToBeInsertedForExpandingRowItem(childRowItem));
            }
        }

        return rowItemsToAdd;
    }

    public void collapseRowItemAtPosition(int position) {
        QxRecyclerViewRowItem rowItem = (QxRecyclerViewRowItem)getItem(position);

        if(!rowItem.isExpanded) {
            return;
        }

        int numberOfRowsToDelete = calculateNumberOfRowsToBeDeletedForCollapsingRowItem(rowItem);

        deleteRowsAtIndex(position + 1, numberOfRowsToDelete, getSectionForRowItemAtPosition(position));

        rowItem.isExpanded = false;
    }

    public int calculateNumberOfRowsToBeDeletedForCollapsingRowItem(QxRecyclerViewRowItem rowItem) {

        int count = 0;

        QxRecyclerViewRowItem childRowItem;

        for(int i = 0; i<rowItem.children.size(); i++)
        {
            count++;

            childRowItem = rowItem.children.get(i);

            if(childRowItem.hasChildren() && childRowItem.isExpanded) {
                count += calculateNumberOfRowsToBeDeletedForCollapsingRowItem(childRowItem);
            }
        }

        return count;
    }

    public int getPositionOfNextRowItemAtSameIndent(QxRecyclerViewRowItem rowItem) {
        int indentLevel = rowItem.indentLevel;

        int positionOfRowItem = getPositionForRowItem(rowItem);
        int positionOfNextRowItemAtSameIndent = -1;

        QxRecyclerViewRowItem nextRowItem;

        for (int i=positionOfRowItem + 1; i < wrappedRowItems.size(); i++) {
            nextRowItem = wrappedRowItems.get(i).rowItem;

            if (nextRowItem instanceof QxRecyclerViewHeaderItem) {
                continue;
            }

            if (nextRowItem.indentLevel == indentLevel) {
                positionOfNextRowItemAtSameIndent = i;
                break;
            }
        }

        if (positionOfNextRowItemAtSameIndent == -1) {
            positionOfNextRowItemAtSameIndent = getItemCount();
        }

        return positionOfNextRowItemAtSameIndent;
    }

    public int getPositionForRowItem(QxRecyclerViewRowItem rowItem) {

        int i = 0;
        for (QxRecyclerViewRowItemWrapper wrappedRowItem : wrappedRowItems) {

            if (wrappedRowItem.rowItem == rowItem) {
                return i;
            }

            i++;
        }

        return -1;
    }

    public int getSectionForRowItemAtPosition(int position) {
        return wrappedRowItems.get(position).indexPath.section;
    }
    
    public QxRecyclerViewRowItem getItem(int position) {
        return wrappedRowItems.get(position).rowItem;
    }

    private List<QxRecyclerViewRowItemWrapper> getWrappedRowItemsForSection(int section) {
        List<QxRecyclerViewRowItemWrapper> sectionWrappedRowItems = new ArrayList<>(wrappedRowItems.size());

        for (QxRecyclerViewRowItemWrapper wrapper : wrappedRowItems) {
            if (wrapper.indexPath.section == section) {
                sectionWrappedRowItems.add(wrapper);
            }
        }

        return sectionWrappedRowItems;
    }

    @Override
    public QxRecyclerRowItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(viewType,viewGroup,false);

        itemView.setOnClickListener(this);

        return constructViewHolderClass(itemView, viewType);
    }

    private QxRecyclerRowItemViewHolder constructViewHolderClass(View itemView, int viewType) {

        try {
            Class<? extends QxRecyclerRowItemViewHolder> viewTypeClass = viewTypeMap.get(viewType);

            Constructor<? extends QxRecyclerRowItemViewHolder> ctor;

            ctor = viewTypeClass.getConstructor(View.class);

            return (QxRecyclerRowItemViewHolder) ctor.newInstance(new Object[]{itemView});
        } catch (Exception exception) {
            throw new IllegalArgumentException("Can't find ViewHolder class ");
        }
    }

    @Override
    public void onBindViewHolder(QxRecyclerRowItemViewHolder viewHolder, int position) {

        QxRecyclerViewRowItemWrapper wrappedRowItem = wrappedRowItems.get(position);

        wrappedRowItem.rowItem.onBindData(viewHolder, position, wrappedRowItem.indexPath, wrappedRowItem.rowPosition);
    }

    @Override
    public void onViewAttachedToWindow(QxRecyclerRowItemViewHolder holder) {

        holder.onViewAttachedToWindow();
    }

    @Override
    public void onAttachedToRecyclerView (RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView (RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public void onClick(final View v) {

        final int position = recyclerView.getChildAdapterPosition(v);

        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        final QxRecyclerViewRowItem rowItem = getItem(position);

        //TODO: once sticky headers is implemented properly, get rid of this
        if (rowItem instanceof QxRecyclerViewHeaderItem) {
            return;
        }

        if (rowItem.children != null && !rowItem.children.isEmpty()) { //it has children, so collapse or expand as needed

            if (rowItem.isExpanded) {
                QxRecyclerViewAdapter.this.collapseRowItemAtPosition(position);
                if (onRecyclerViewRowItemExpandCollapseListener != null) {
                    onRecyclerViewRowItemExpandCollapseListener.onRecyclerViewRowItemCollapsed(rowItem, QxRecyclerViewAdapter.this, v, position);
                }
            } else {
                QxRecyclerViewAdapter.this.expandRowItemAtPosition(position);

                if (v.getTop() > Math.round((recyclerView).getHeight() / 2.f)) {
                    recyclerView.smoothScrollBy(0, v.getTop() - Math.round((recyclerView).getHeight() / 2.f));
                }

                if (onRecyclerViewRowItemExpandCollapseListener != null) {
                    onRecyclerViewRowItemExpandCollapseListener.onRecyclerViewRowItemExpanded(rowItem, QxRecyclerViewAdapter.this, v, position);
                }
            }
        } else {

            if (onRecyclerViewRowItemClickedListener != null) {
                //let's delay the actual selection of the row, otherwise we don't see the row
                //being highlighted for some reason
                if (shouldDelaySelection) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onRecyclerViewRowItemClickedListener.onRecyclerViewRowItemClicked(rowItem, QxRecyclerViewAdapter.this, v, position);
                        }
                    }, 150);
                } else {
                    onRecyclerViewRowItemClickedListener.onRecyclerViewRowItemClicked(rowItem, QxRecyclerViewAdapter.this, v, position);
                }
            }
        }
    }

    @Override
    public long getItemId(int position) {
        if(wrappedRowItems == null) {
            return 0;
        }
        else {
            return wrappedRowItems.get(position).rowItem.getItemId();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return wrappedRowItems.get(position).rowItem.getResourceId();
    }

    @Override
    public int getItemCount() {

        if (wrappedRowItems != null) {
            return wrappedRowItems.size();
        }
        else {
            return 0;
        }
    }


}
