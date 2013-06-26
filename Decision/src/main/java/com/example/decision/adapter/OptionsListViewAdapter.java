package com.example.decision.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.decision.R;
import com.example.decision.modules.CardClickListener;
import com.example.decision.modules.CardLongClickListener;
import com.example.decision.modules.OptionsCard;
import com.example.decision.modules.OptionsCardHolder;

import java.util.ArrayList;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class OptionsListViewAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<OptionsCard> mCardList;
    LayoutInflater mInflater;
    static final String TAG = "OptionsListViewAdapter";

    public OptionsListViewAdapter(Context context, ArrayList<OptionsCard> cardList){
        mContext = context;
        mCardList = cardList;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return null == mCardList ? 0 : mCardList.size();
    }

    @Override
    public Object getItem(int i) {
        return null == mCardList ? null : mCardList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d(TAG, "potision : " + i);
        OptionsCardHolder cardHolder;
        if (view == null){
            Log.d(TAG, "create view first time");
            cardHolder = new OptionsCardHolder();
            view = mInflater.inflate(R.layout.main_listview_card_item_layout, null);
            cardHolder.mTitleView = (TextView) view.findViewById(R.id.main_card_title);
            cardHolder.mContentView = (TextView) view.findViewById(R.id.main_card_content);
            view.setTag(cardHolder);
        } else {
            Log.d(TAG, "view already used");
            cardHolder = (OptionsCardHolder)view.getTag();
        }
        cardHolder.mTitleView.setText(mCardList.get(i).getmTitle());
        cardHolder.mContentView.setText(mCardList.get(i).getmContent());
        bindCardClickListener(cardHolder, mCardList.get(i));
        return view;
    }
    public void bindCardClickListener(OptionsCardHolder holder, OptionsCard card){
        CardClickListener listener = new CardClickListener(mContext, card);
        holder.mTitleView.setOnClickListener(listener);
        holder.mContentView.setOnClickListener(listener);

        CardLongClickListener longClickListener = new CardLongClickListener(mContext, card);
        holder.mTitleView.setOnLongClickListener(longClickListener);
        holder.mContentView.setOnLongClickListener(longClickListener);
    }
}
