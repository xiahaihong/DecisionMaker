package com.example.decision.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.decision.R;
import com.example.decision.adapter.CardDbAdapter;
import com.example.decision.adapter.OptionsListViewAdapter;
import com.example.decision.controllers.Controllers;
import com.example.decision.modules.OptionsCard;
import com.example.decision.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    ListView mOptionsListView;
    OptionsListViewAdapter mOptionsAdapter;
    LinearLayout mAddLayout;
    ArrayList<OptionsCard> mCardList = new ArrayList<OptionsCard>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListView();
        initAddView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCardList();
        mOptionsAdapter.notifyDataSetChanged();
    }

    private void initAddView(){
        mAddLayout = (LinearLayout)this.findViewById(R.id.add_subject_layout);
        mAddLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddSubjectActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initListView(){
/*        for (int i = 0; i < 10; i++){
            OptionsCard card = new OptionsCard();
            mCardList.add(card);
        }*/
        // get from sql
        initCardList();
        mOptionsListView = (ListView) this.findViewById(R.id.options_listview);
        mOptionsAdapter = new OptionsListViewAdapter(MainActivity.this, mCardList);
        mOptionsListView.setAdapter(mOptionsAdapter);
    }

    private void initCardList() {
        mCardList.clear();
        ArrayList<ContentValues> cvList = Controllers.Instance().getmDbAdapter().getCards(0, -1);
        for (ContentValues cv : cvList){
            String title = cv.getAsString(CardDbAdapter.COL_TITLE_TEXT_1);
            String content = cv.getAsString(CardDbAdapter.COL_CONTENT_TEXT_1);
            String item = cv.getAsString(CardDbAdapter.COL_ITEMS_TEXT_1);
            String[] itemArray  = item.split(AddSubjectActivity.SEPARATOR);
            List<String> itemList = Arrays.asList(itemArray);
            OptionsCard card = new OptionsCard(title, content, itemList);
            mCardList.add(card);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
