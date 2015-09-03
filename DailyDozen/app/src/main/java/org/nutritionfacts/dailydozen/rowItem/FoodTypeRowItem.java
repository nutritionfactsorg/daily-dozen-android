package org.nutritionfacts.dailydozen.rowItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.qxmd.qxrecyclerview.QxIndexPath;
import com.qxmd.qxrecyclerview.QxRecyclerRowItemViewHolder;
import com.qxmd.qxrecyclerview.QxRecyclerViewRowItem;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.db.DBConsumption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chankruse on 15-09-03.
 */
public class FoodTypeRowItem extends QxRecyclerViewRowItem {

    public DBConsumption consumption;
    private Drawable iconDrawable;

    public FoodTypeRowItem(DBConsumption consumption, Context context) {
        super();

        this.consumption = consumption;
        iconDrawable = context.getResources().getDrawable(consumption.foodType.iconResourceId);
    }

    @Override
    public int getResourceId() {
        return R.layout.row_item_food_type;
    }

    @Override
    public Class<? extends QxRecyclerRowItemViewHolder> getViewHolderClass() {
        return FeaturedContentAdItemViewHolder.class;
    }

    @Override
    public void onBindData(QxRecyclerRowItemViewHolder viewHolder, int position, QxIndexPath indexPath, RowPosition rowPosition) {

        FeaturedContentAdItemViewHolder mViewHolder = (FeaturedContentAdItemViewHolder) viewHolder;

        mViewHolder.imageView.setImageDrawable(iconDrawable);

        mViewHolder.textView.setText(consumption.foodType.name);

        int i=0;

        for (CheckBox checkBox : mViewHolder.checkBoxes) {

            if (i < consumption.foodType.recommendedServingCount) {
                checkBox.setVisibility(View.VISIBLE);

                if (i < consumption.getConsumedServingCount()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            } else {
                checkBox.setVisibility(View.GONE);
            }

            i++;
        }
    }

    public final static class FeaturedContentAdItemViewHolder extends QxRecyclerRowItemViewHolder {

        ImageView imageView;
        TextView textView;

        List<CheckBox> checkBoxes;

        public FeaturedContentAdItemViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            textView = (TextView) itemView.findViewById(R.id.text_view);

            checkBoxes = new ArrayList<>(5);
            checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox_1));
            checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox_2));
            checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox_3));
            checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox_4));
            checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox_5));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setBackground(null);
                }
            }
        }
    }
}
