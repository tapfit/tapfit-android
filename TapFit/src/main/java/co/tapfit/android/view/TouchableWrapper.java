package co.tapfit.android.view;

import android.content.Context;
import android.os.SystemClock;
import android.renderscript.ScriptC;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.fragment.PlaceMapFragment;
import co.tapfit.android.helper.Log;
import pl.mg6.android.maps.extensions.SupportMapFragment;

/**
 * Created by zackmartinsek on 9/24/13.
 */
public class TouchableWrapper extends FrameLayout {
    private long lastTouched = 0;
    private static final long SCROLL_TIME = 50L; // 200 Milliseconds, but you can adjust that to your liking
    private UpdateMapAfterUserInterection updateMapAfterUserInterection;

    private static final String TAG = TouchableWrapper.class.getSimpleName();

    public TouchableWrapper(Context context, PlaceMapFragment fragment) {
        super(context);
        // Force the host activity to implement the UpdateMapAfterUserInterection Interface
        try {
            updateMapAfterUserInterection = fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement UpdateMapAfterUserInterection");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "Received disptachTouchEvent: " + ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouched = SystemClock.uptimeMillis();
                Log.d(TAG, "Action Down: " + lastTouched);
                break;
            case MotionEvent.ACTION_UP:
                final long now = SystemClock.uptimeMillis();
                Log.d(TAG, "Action Up: now: " + now + ", lastTouched: " + lastTouched + ", SCROLL_TIME: " + SCROLL_TIME);
                if (now - lastTouched > SCROLL_TIME) {
                    // Update the map
                    updateMapAfterUserInterection.onUpdateMapAfterUserInterection();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // Map Activity must implement this interface
    public interface UpdateMapAfterUserInterection {
        public void onUpdateMapAfterUserInterection();
    }
}
