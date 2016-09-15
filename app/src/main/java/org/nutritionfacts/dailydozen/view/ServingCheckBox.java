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

    public void onCheckChange(boolean isChecked){
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
