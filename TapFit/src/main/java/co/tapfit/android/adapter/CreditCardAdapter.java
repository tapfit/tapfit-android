package co.tapfit.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.model.CreditCard;

/**
 * Created by zackmartinsek on 9/14/13.
 */
public class CreditCardAdapter extends BaseAdapter {

    private static final String TAG = CreditCardAdapter.class.getSimpleName();

    private ArrayList<CreditCard> mCreditCards = new ArrayList<CreditCard>();

    private Context mContext;
    private LayoutInflater mInflater;
    private Integer mResource = R.layout.payments_item;

    private ImageCache mImageCache = ImageCache.getInstance();

    public CreditCardAdapter(Context context, List<CreditCard> creditCards) {
        super();

        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        mCreditCards = new ArrayList<CreditCard>(creditCards);
    }

    @Override
    public int getCount() {
        return mCreditCards.size();
    }

    @Override
    public Object getItem(int i) {
        return mCreditCards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null)
        {
            view = mInflater.inflate(mResource, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        CreditCard creditCard = mCreditCards.get(i);

        mImageCache.loadImageForPlacePage(holder.card_icon, creditCard.image_url);
        holder.card_number.setText("*****" + creditCard.last_four);
        if (creditCard.default_card) {
            holder.default_icon.setVisibility(View.VISIBLE);
        }
        else {
            holder.default_icon.setVisibility(View.GONE);
        }

        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.normal_button_size)));

        return view;
    }

    private class ViewHolder {

        public ViewHolder(View view)
        {
            card_icon = (ImageView) view.findViewById(R.id.card_icon);
            card_number = (TextView) view.findViewById(R.id.card_number);
            default_icon = (ImageView) view.findViewById(R.id.default_icon);
        }

        public ImageView card_icon;
        public TextView card_number;
        public ImageView default_icon;

    }
}
