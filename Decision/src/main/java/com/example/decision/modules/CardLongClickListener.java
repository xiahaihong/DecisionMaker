package com.example.decision.modules;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import com.example.decision.R;
import com.example.decision.controllers.Controllers;

/**
 * Created by haihong.xiahh on 13-6-26.
 */
public class CardLongClickListener implements View.OnLongClickListener {
    Context mContext;
    OptionsCard mCard;
    public CardLongClickListener(Context context, OptionsCard card){
        mContext = context;
        mCard = card;
    }
    @Override
    public boolean onLongClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Controllers.Instance().getmDbAdapter().deleteCard(mCard.getmID());
                ((IOperationManager) mContext).onOperationFinished();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }
}
