package co.tapfit.android.helper;

import android.content.Context;
import android.content.Intent;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class ShareToFriends {

    public static void shareAppToFriends(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("plain/text");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Check Out TapFit");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "You've got to check out this new app, TapFit! http://www.tapfit.co");
        context.startActivity(Intent.createChooser(sharingIntent,"Share TapFit via..."));
    }


}
