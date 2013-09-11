package co.tapfit.android.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class RoundedBackgroundDisplayer implements BitmapDisplayer {

    private int mRoundedEdges;

    public RoundedBackgroundDisplayer(float roundedEdges) {
        mRoundedEdges = (int) roundedEdges;
    }

    @Override
    public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
        Bitmap roundedBitmap = RoundedBitmapDisplayer.roundCorners(bitmap, imageView, mRoundedEdges);

        //GradientDrawable drawable = new GradientDrawable();
        //drawable.
        imageView.setImageBitmap(roundedBitmap);
        return roundedBitmap;
    }
}
