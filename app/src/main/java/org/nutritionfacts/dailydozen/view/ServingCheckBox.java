package org.nutritionfacts.dailydozen.view;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.appcompat.widget.AppCompatCheckBox;

import org.nutritionfacts.dailydozen.R;

public class ServingCheckBox extends AppCompatCheckBox {
    private ServingCheckBox scbNextServing;
    private ServingCheckBox scvPrevServing;

    public ServingCheckBox(Context context) {
        super(new ContextThemeWrapper(context, R.style.ThemeOverlay_ServingCheckBox), null, androidx.appcompat.R.attr.checkboxStyle);
        setMinHeight(0);
        setMinWidth(0);
        setPaddingRelative(0, 0, 0, 0);
        setBackground(null);
    }

    public void setNextServing(ServingCheckBox nextServing) {
        this.scvPrevServing = nextServing;
        nextServing.scbNextServing = this;
    }

    public void onCheckChange(boolean isChecked) {
        if (isChecked) {
            continueCheck();
        } else {
            continueUncheck();
        }
    }

    private void continueCheck() {
        if (scbNextServing != null && !scbNextServing.isChecked()) {
            scbNextServing.setChecked(true);
        }
    }

    private void continueUncheck() {
        if (scvPrevServing != null && scvPrevServing.isChecked()) {
            scvPrevServing.setChecked(false);
        }
    }
}
