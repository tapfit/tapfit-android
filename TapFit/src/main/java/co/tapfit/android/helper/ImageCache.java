package co.tapfit.android.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import co.tapfit.android.R;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class ImageCache {

    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static Resources mResources;
    private static ImageCache ourInstance = new ImageCache();

    private static DisplayImageOptions placePageOpts;

    public static ImageCache getInstance() {
        return ourInstance;
    }

    private ImageCache() {

    }

    public static String getCoverPhotoUrl(String category) {

        if (category == null) {
            return mResources.getString(R.string.stock_gym_url);
        }
        else if (category.equals("Yoga"))
        {
            return mResources.getString(R.string.stock_yoga_url);
        }
        else if (category.equals("Pilates Barre"))
        {
            return mResources.getString(R.string.stock_pilates_url);
        } else if (category.equals("Dance"))
        {
            return mResources.getString(R.string.stock_dance_url);
        }
        else if (category.equals("Cardio"))
        {
            return mResources.getString(R.string.stock_cardio_url);
        }
        else {
            return mResources.getString(R.string.stock_gym_url);
        }
    }

    public static int convertDpToPixels(int dp) {
        DisplayMetrics metrics = mResources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi/160f);
        return Math.round(px);
    }

    public static void loadImageForPlacePage(ImageView imageView, String url) {

        imageLoader.displayImage(url, imageView, getPlacePageOpts());
    }

    private static DisplayImageOptions getPlacePageOpts() {
        if (placePageOpts == null) {
            placePageOpts = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .build();
        }
        return placePageOpts;
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
                .memoryCacheSize(3 * 1024)// You can pass your own memory cache implementation
                .discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new HttpClientImageDownloader(context, setUpHttpClient()))
                .build();

        imageLoader.init(config);
    }

    public static HttpClient setUpHttpClient()
    {
        DefaultHttpClient ret = null;

        //SETS UP PARAMETERS
        HttpParams params = new BasicHttpParams();
        //REGISTERS SCHEMES FOR BOTH HTTP AND HTTPS
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        ret = new DefaultHttpClient(manager, params);
        return ret;
    }
}
