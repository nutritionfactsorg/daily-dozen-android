package org.nutritionfacts.dailydozen.view;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class ServingCheckBox extends CheckBox {

    private ServingCheckBox scbNextServing;
    private ServingCheckBox scvPrevServing;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public ServingCheckBox(Context context) {
        super(context);
    }

    public void setNextServing(ServingCheckBox nextServing){
        this.scvPrevServing = nextServing;
        nextServing.scbNextServing = this;
    }

    public void setCheckChangeListener(){
        this.setOnCheckedChangeListener(getOnCheckedChangeListener());
    }

    private CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {
        if (onCheckedChangeListener == null) {
            onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCheckClick(isChecked);
                }
            };
        }
        return onCheckedChangeListener;
    }

    public void onCheckClick(boolean isChecked){
        if (isChecked)
            continueCheck();
        else
            continueUncheck();
    }

    private void continueCheck(){
        if (scbNextServing != null && !scbNextServing.isChecked())
            scbNextServing.setChecked(true);
    }

    private void continueUncheck(){
        if (scvPrevServing != null && scvPrevServing.isChecked())
            scvPrevServing.setChecked(false);
    }

}
