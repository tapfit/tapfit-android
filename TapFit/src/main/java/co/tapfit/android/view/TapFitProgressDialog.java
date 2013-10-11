package co.tapfit.android.view;

import android.app.ProgressDialog;
import android.content.Context;

import co.tapfit.android.R;

/**
 * Created by zackmartinsek on 10/11/13.
 */
public class TapFitProgressDialog extends ProgressDialog {

    public TapFitProgressDialog(Context context) {
        super(context);

        this.setIndeterminate(true);
        this.setIndeterminateDrawable(context.getResources().getDrawable(R.anim.spinning_logo_animation));
    }
}
