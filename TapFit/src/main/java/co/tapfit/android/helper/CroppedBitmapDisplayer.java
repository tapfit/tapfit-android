package co.tapfit.android.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class CroppedBitmapDisplayer implements BitmapDisplayer {

    @Override
    public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {

        int targetWidth = imageView.getWidth();
        int targetHeight = imageView.getHeight();

        Bitmap target = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.RGB_565);

        Rect rect = new Rect(0, 0, targetWidth, targetHeight);
        Rect orig = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Canvas canvas = new Canvas(target);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(bitmap, orig, rect, paint);

        imageView.setImageBitmap(bitmap);
        return bitmap;
    }
}
