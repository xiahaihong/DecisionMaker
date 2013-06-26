package com.example.decision.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.decision.R;
import com.example.decision.controllers.Controllers;

import java.util.UUID;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class AddSubjectActivity extends Activity {
    EditText mTitleText;
    EditText mContentText;
    EditText mItemText;
    LinearLayout mAddItemLayout;
    Button mAddSubjectBtn;
    Button mAddItemBtn;
    public static final String SEPARATOR = "ITEM_SPLIT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject_layout);
        mTitleText = (EditText) this.findViewById(R.id.add_title);
        mContentText = (EditText) this.findViewById(R.id.add_content);
        mAddItemLayout = (LinearLayout) this.findViewById(R.id.add_item_layout);
        mAddItemBtn = (Button) this.findViewById(R.id.add_item_btn);
        mItemText = (EditText) this.findViewById(R.id.add_item);
        mAddItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newView = new EditText(AddSubjectActivity.this);
                newView.requestFocus();
                mAddItemLayout.addView(newView);
            }
        });
        mAddSubjectBtn = (Button) this.findViewById(R.id.add_subject_btn);
        mAddSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSubjectToSql();
            }
        });
    }
    private void  saveSubjectToSql(){
        String title = mTitleText.getText().toString().trim();
        String content = mContentText.getText().toString().trim();
        String item = mItemText.getText().toString().trim();
        int itemCount = mAddItemLayout.getChildCount();
        if (itemCount > 1){
            item += SEPARATOR;
        }
        for (int i = 1; i < itemCount; i++){
            try{
                EditText v = (EditText)mAddItemLayout.getChildAt(i);
                String text = v.getText().toString().trim();
                if (!"".equals(text)){
                    item += text;
                    if (i != itemCount -1){
                        item += SEPARATOR;
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }

        }
        String id = UUID.randomUUID().toString();
        Controllers.Instance().getmDbAdapter().addCard(
                id, item, title, content);
        Toast.makeText(AddSubjectActivity.this, "success", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
