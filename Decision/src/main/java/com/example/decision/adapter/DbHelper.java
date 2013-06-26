package com.example.decision.adapter;

/**
 * Created by haihong.xiahh on 13-6-25.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.decision.controllers.Controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by haihong.xiahh on 13-5-22.
 */
public class DbHelper extends SQLiteOpenHelper {
    String TAG = "TFDbHelper";
    public DbHelper(Context context) {
        super(context, CardDbAdapter.DB_NAME, null, CardDbAdapter.DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HashMap<String, String> tableSql= new HashMap<String, String>();
        try {
            tableSql.put(CardDbAdapter.DB_TABLE,
                    Controllers.Instance().getmDbAdapter().getCreateSql());
        } catch (SQLiteException e){
            e.printStackTrace();
            throw e;
        }
        Iterator<String> it = tableSql.keySet().iterator();
        while (it.hasNext()){
            String table = (String) it.next();
            String sql = (String)tableSql.get(table);

            // create db table
            Log.i(TAG, sql);
            db.execSQL(sql);

            // create index
            String indexSql = String.format("Create Index %s_%s_idx ON %s(%s);",
                    table,
                    CardDbAdapter.INDEX_COL,
                    table,
                    CardDbAdapter.INDEX_COL);
            Log.d(TAG, String.format("sql=%s", indexSql));
            db.execSQL(indexSql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        HashMap<String, List<String>> tableSql= new HashMap<String, List<String>>();
        try {
            tableSql.put(CardDbAdapter.DB_TABLE,
                    Controllers.Instance().getmDbAdapter().getAlterSqls(oldVersion, newVersion));

        } catch (SQLiteException e){
            e.printStackTrace();
            throw e;
        }
        Iterator<String> it = tableSql.keySet().iterator();
        while (it.hasNext()){
            String table = (String) it.next();
            List<String> sqls = (List<String>)tableSql.get(table);

            for(String sql : sqls){
                Log.i(TAG, sql);
                db.execSQL(sql);
            }
        }
    }
}

