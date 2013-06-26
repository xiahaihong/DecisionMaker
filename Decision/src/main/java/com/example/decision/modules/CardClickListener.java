package com.example.decision.modules;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.example.decision.activities.CardActivity;
import com.example.decision.utils.Constants;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class CardClickListener implements View.OnClickListener {
    Context mContext;
    OptionsCard mCard;
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, CardActivity.class);
        intent.putExtra(Constants.INTENT_MSG, mCard);
        mContext.startActivity(intent);
    }
    public CardClickListener(Context context, OptionsCard card){
        mContext = context;
        mCard = card;
    }

}
