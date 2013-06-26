package com.example.decision.adapter;

import android.content.ContentValues;
import android.content.Context;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class CardDbAdapter extends DbAdapter {
    public static String DB_TABLE = "card";
    private static CardDbAdapter mInstance;
    public static final String COL_ID_TEXT_1 = "id";
    public static final String COL_TITLE_TEXT_1 = "title";
    public static final String COL_CONTENT_TEXT_1 = "content";
    public static final String COL_ITEMS_TEXT_1 = "items";
    public static final String COL_MTIME_INTEGER_1 = "updateTime";
    public static final String INDEX_COL = COL_ID_TEXT_1;

    protected void initColumns(){
        mFieldsColDef = new ArrayList<String>();
        mDbColumns = new ArrayList<String>();
        mDbColumnTypes = new ArrayList<String>();
        Field[] fields = CardDbAdapter.class.getFields();
        setColumns(fields);
    }
    private CardDbAdapter(Context context, String tableName){
        super(context, tableName);
    }
    public static CardDbAdapter Instance(Context context){
        if (mInstance == null){
            mInstance = new CardDbAdapter(context, DB_TABLE);
        }
        return  mInstance;
    }
        public int getNum(){
            ContentValues conditions = new ContentValues();
            return count(conditions, INDEX_COL);
        }

        public ArrayList<ContentValues> getCards(int start, int num) {
            ArrayList<ContentValues> cards;

            int[] limits;
            if(num < 0){
                limits = null;
            }else{
                limits = new int[] {start, num};
            }

            ContentValues conditions = new ContentValues();
            cards = query(limits, conditions, this.INDEX_COL);
            return cards;
        }


        public ContentValues getCard(String cardId){
            ContentValues retCv = new ContentValues();
            ArrayList<ContentValues> cards;

            int[] limits = null;
            ContentValues conditions = new ContentValues();
            conditions.put(COL_ID_TEXT_1, cardId);
            cards = query(limits, conditions, this.INDEX_COL);
            if (cards.size() == 1){
                retCv = cards.get(0);
            }
            return retCv;
        }

        public boolean cardExists(String cardId){
            ContentValues cv = this.getCard(cardId);
            return cv.size() > 0;
        }

        public boolean addCard(String cardId, String json, String title, String content) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ID_TEXT_1, cardId);
            cv.put(COL_ITEMS_TEXT_1, json);
            cv.put(COL_MTIME_INTEGER_1, getCurrentTime());
            cv.put(COL_TITLE_TEXT_1, title);
            cv.put(COL_CONTENT_TEXT_1, content);
            return insert(cv);
        }

        public boolean deleteCard(String cardId) {
            String whereClause = COL_ID_TEXT_1 + "=?";
            String[] whereArgs = new String[]{cardId};
            return delete(whereClause, whereArgs);
        }

        public boolean deleteCards(){
            return delete(null, null);
        }

        public boolean deleteCards(ArrayList<String> cardIdList){
            String whereClause = COL_ID_TEXT_1 + "=?";
            return batchDelete(whereClause, cardIdList);
        }

        public boolean updateCard(String cardId, String json, boolean isUpdateMtime) {
            ContentValues cv = new ContentValues();
            if(null != json){
                cv.put(COL_ITEMS_TEXT_1, json);
            }
            if(isUpdateMtime){
                cv.put(COL_MTIME_INTEGER_1, getCurrentTime());
            }
            String whereClause = COL_ID_TEXT_1 + "=?";
            String[] whereArgs = new String[]{cardId};
            return update(cv, whereClause, whereArgs);
        }

        public boolean addCards(ArrayList<ContentValues> cvList){
            ArrayList<String> requiredKeys = new ArrayList<String>();
            requiredKeys.add(COL_ID_TEXT_1);
            requiredKeys.add(COL_TITLE_TEXT_1);
            requiredKeys.add(COL_CONTENT_TEXT_1);
            requiredKeys.add(COL_ITEMS_TEXT_1);

            ContentValues defaultKeyValues = new ContentValues();
            defaultKeyValues.put(COL_MTIME_INTEGER_1, getCurrentTime());
            return  batchInsert(cvList, requiredKeys, defaultKeyValues);
        }
    }
