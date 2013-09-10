package co.tapfit.android.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class ImageCache {

    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static Resources mResources;
    private static ImageCache ourInstance = new ImageCache();

    public static ImageCache getInstance() {
        return ourInstance;
    }

    private ImageCache() {

    }

    public static int convertDpToPixels(int dp) {
        DisplayMetrics metrics = mResources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi/160f);
        return Math.round(px);
    }

    public static void initImageLoader(Context context) {

        mResources = context.getResources();

        File cacheDir = StorageUtils.getCacheDirectory(context);

        ImageLoaderConfiguration config = new
                ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(800, 800)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 3)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(30) // You can pass your own memory cache implementation
                .discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        imageLoader.init(config);
    }
}
