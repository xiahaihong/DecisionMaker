package com.example.decision.adapter;

/**
 * Created by haihong.xiahh on 13-6-25.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by haihong.xiahh on 13-5-22.
 */
public abstract class DbAdapter {
    private final static String TAG = "DbAdapter";
    protected final static String DB_NAME = "Decision";
    protected final static int DB_VER = 1;
    protected static final String COMMON_FIELD_PREFIX = "COL_";

    //private Context mContext;
    private SoftReference<Context> mContext;
    protected DbHelper mDbHelper;
    protected String mTableName;

    protected ArrayList<String> mFieldsColDef;
    protected  ArrayList<String> mDbColumns;
    protected  ArrayList<String> mDbColumnTypes;

    // records the columns are introduced from which db version
    // use for alter table
    protected HashMap<Integer, String[]> mDbColumnsVerDict = new HashMap<Integer, String[]>();

    protected DbAdapter(Context context, String tableName) {
        mContext = new SoftReference<Context>(context);
        mDbHelper = new DbHelper(mContext.get());
        mTableName = tableName;
        initColumns();
    }

    protected abstract void initColumns();

    public ContentValues getContentValuesFromCursor(Cursor cursor){
        ContentValues cv = new ContentValues();
        for(int i = 0; i < mDbColumns.size(); i++){
            String column = mDbColumns.get(i);
            String columnType = mDbColumnTypes.get(i);
            int columnIndex = cursor.getColumnIndex(column);
            if (columnType.equals("TEXT")){
                String value = cursor.getString(columnIndex);
                cv.put(column, value);
            } else if (columnType.equals("INTEGER")){
                int value = cursor.getInt(columnIndex);
                cv.put(column, value);
            } else if (columnType.equals("LONG")){
                long value = cursor.getLong(columnIndex);
                cv.put(column, value);
            } else {
            }
        }
        return cv;
    }

    public String getCreateSql() throws SQLiteException {
        String sql = "CREATE TABLE " + mTableName + " " +
                "(_id INTEGER DEFAULT '0' NOT NULL PRIMARY KEY AUTOINCREMENT";
        if (mDbColumnTypes.size() != mDbColumns.size()){
            throw new SQLiteException("Columns of " + mTableName + " may have some wrong definition");
        }
        for (int i = 0; i < mDbColumns.size(); i++) {
            String fieldValue = mDbColumns.get(i);
            String dbType = mDbColumnTypes.get(i);
            sql += ",";
            sql += String.format("%s %s DEFAULT ''", fieldValue, dbType);
        }
        sql += ")";
        Log.d(TAG, String.format("sql=%s", sql));
        return sql;
    }

    public List<String> getAlterSqls(int oldVer, int newVer) throws SQLiteException{
        List<String> retAlterSqls = new ArrayList<String>();
        String sqlTemplate = String.format(Locale.US, "ALTER TABLE %s ADD COLUMN %%s %%s DEFAULT ''", mTableName);
        for(Integer ver : mDbColumnsVerDict.keySet()){
            if(ver.compareTo(Integer.valueOf(oldVer)) > 0 &&
                    ver.compareTo(Integer.valueOf(newVer)) <= 0){
                String colName = mDbColumnsVerDict.get(ver)[0];
                String colType = mDbColumnsVerDict.get(ver)[1];
                retAlterSqls.add(String.format(Locale.US,
                        sqlTemplate, colName, colType));
            }
        }
        return retAlterSqls;
    }

    void setColumns(Field[] fields) {
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            if (fieldName.startsWith(COMMON_FIELD_PREFIX)) {
                mFieldsColDef.add(fieldName);
                String[] sects = fieldName.split("_");
                Integer dbVer = Integer.valueOf(sects[sects.length - 1]);
                String dbType = sects[sects.length - 2];
                String dbColName = null;
                try {
                    dbColName = (String) fields[i].get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new SQLiteException("TFDbAdapter DB column definition error");
                }
                if(null != dbColName){
                    mDbColumns.add(dbColName);
                    mDbColumnTypes.add(dbType);
                    mDbColumnsVerDict.put(dbVer, new String[]{dbColName, dbType});
                }else{
                    throw new SQLiteException("TFDbAdapter DB column parsing error");
                }
            }
        }
    }

    public void close() {
        mDbHelper.close();
    }
    protected Cursor __select(String[] columns, ContentValues cv, int[] limits, String indexColumn) throws IllegalArgumentException {
        // generate SQL WHERE clause and selectArgs
        String whereClause = "";
        ArrayList<String> selectArgs = new ArrayList<String>();
        if(null == cv || cv.size() <= 0){
            whereClause = null;
        }else{
            for(Map.Entry<String, Object> entry : cv.valueSet()){
                String key = entry.getKey();
                if(mDbColumns.contains(key)){
                    String clause = String.format("%s=?", key);
                    if(whereClause.length() > 0){
                        whereClause += String.format(" AND %s", clause);
                    }else{
                        whereClause += clause;
                    }
                    String value = "";
                    Object object = entry.getValue();
                    try {
                        value = (String)object;
                    } catch (Exception e){
                        e.printStackTrace();
                        value = String.valueOf(object);
                    }
                    selectArgs.add(value);
                }else{
                    throw new IllegalArgumentException(String.format("'%s' is not a db column", key));
                }
            }
        }
        // generate LIMIT clause
        String limitClause;
        if(null == limits || limits.length <= 0){
            limitClause = null;
        }else if(limits.length == 1){
            limitClause = String.format("%d", limits[0]);
        }else{
            limitClause = String.format("%d, %d", limits[0], limits[1]);
        }

        SQLiteDatabase db = null;
        try{
            db = mDbHelper.getReadableDatabase();
            return db.query(this.mTableName,
                    columns,
                    whereClause,
                    (null != whereClause? selectArgs.toArray(new String[selectArgs.size()]):null),
                    null,
                    null,
                    indexColumn + " DESC",
                    limitClause);
        }catch(SQLiteException exp){
            Log.e(TAG, exp.toString());
            throw exp;
        }finally{
            // no need to close SQLiteDatabase as it is cached by Helper
            // if close it here, later use of the SQLiteDatabase obj will throw NullPointerException
        }
    }

    public boolean insert(ContentValues cv){
        SQLiteDatabase sqliteDb = null;
        try {
            sqliteDb = mDbHelper.getWritableDatabase();
            sqliteDb.beginTransaction();
            long ret = sqliteDb.insert(this.mTableName, null, cv);
            if(ret != -1L){
                sqliteDb.setTransactionSuccessful();
            }
            return (-1L != ret ? true : false);
        }catch(SQLiteException ex){
            Log.e(TAG, ex.toString());
            throw ex;
        }finally{
            if(null != sqliteDb){
                sqliteDb.endTransaction();
            }
        }
    }

    public boolean update(ContentValues cv, String whereClause, String[] whereArgs){
        SQLiteDatabase sqliteDb = null;
        try {
            sqliteDb = mDbHelper.getWritableDatabase();
            sqliteDb.beginTransaction();
            int ret = sqliteDb.update(this.mTableName, cv, whereClause, whereArgs);
            if(ret > 0){
                sqliteDb.setTransactionSuccessful();
            }
            return (0 < ret ? true : false);
        }catch (SQLiteException ex) {
            Log.e(TAG, ex.toString());
            throw ex;
        }finally{
            if(null != sqliteDb){
                sqliteDb.endTransaction();
            }
        }
    }

    public boolean delete(String whereClause, String[] whereArgs ){
        SQLiteDatabase sqliteDb = null;
        try {
            sqliteDb = mDbHelper.getWritableDatabase();
            sqliteDb.beginTransaction();
            int ret = sqliteDb.delete(this.mTableName, whereClause, whereArgs);
            if(ret > 0){
                sqliteDb.setTransactionSuccessful();
            }
            return (0 < ret ? true : false);
        }catch (SQLiteException ex) {
            Log.e(TAG, ex.toString());
            throw ex;
        }finally{
            if(null != sqliteDb){
                sqliteDb.endTransaction();
            }
        }
    }
    public ArrayList<ContentValues> query(int[] limits, ContentValues conditions, String indexColumn, String[] columns){
        ArrayList<ContentValues> results = new ArrayList<ContentValues>();
        Cursor cur = this.__select(columns, conditions, limits, indexColumn);
        try{
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                results.add(getContentValuesFromCursor(cur));
            }
        }finally{
            if(null != cur){
                cur.close();
            }
        }
        return results;
    }

    public ArrayList<ContentValues> query(int[] limits, ContentValues conditions, String indexColumn){
        String[] columns = mDbColumns.toArray(new String[mDbColumns.size()]);
        return query(limits, conditions, indexColumn, columns);
    }

    public int count(ContentValues conditions, String indexColumn){
        int ret = 0;
        String[] columns =  new String[]{"count(*)"};
        Cursor cur = this.__select(columns, conditions, null, indexColumn);
        try{
            cur.moveToFirst();
            if (!cur.isAfterLast()){
                ret = cur.getInt(0);
            }
        }finally{
            if(null != cur){
                cur.close();
            }
        }
        return ret;
    }

    private boolean contentValuesContainsKeys(ContentValues cv, ArrayList<String> keys){
        for(String key : keys){
            if(!cv.containsKey(key)){
                return false;
            }
        }
        return true;
    }

    private ContentValues setDefaultValueForContentValues(ContentValues cv, ContentValues defaultKeyValues){
        ContentValues keyValues = new ContentValues(defaultKeyValues);
        Iterator iterator = defaultKeyValues.keySet().iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            if (cv.containsKey(key)){
                keyValues.remove(key);
            }
        }
        if (keyValues.size() > 0){
            cv.putAll(keyValues);
        }
        return  cv;
    }


    public boolean batchInsert(ArrayList<ContentValues> cvList, ArrayList<String> requiredKeys, ContentValues defaultKeyValues){
        SQLiteDatabase sqliteDb = null;
        try{
            sqliteDb = this.mDbHelper.getWritableDatabase();
        }catch(SQLiteException ex){
            Log.e(TAG, ex.toString());
            throw ex;
        }

        int failedNum = 0;
        sqliteDb.beginTransaction();
        try{
            for(ContentValues cv : cvList){
                if(null == cv || cv.size() < 1){
                    throw new SQLException("Unable to insert empty values");
                }else if(!contentValuesContainsKeys(cv, requiredKeys)){
                    throw new SQLException("Compulsory column values are missing");
                }

                ContentValues values = new ContentValues(cv);
                values = setDefaultValueForContentValues(values, defaultKeyValues);
                long ret = sqliteDb.insert(this.mTableName, null, values);
                if(ret == -1L){
                    failedNum ++;
                }
            }
            if(failedNum > 0){
                return false;
            }else{
                sqliteDb.setTransactionSuccessful();
                return true;
            }
        }finally{
            if(null != sqliteDb){
                sqliteDb.endTransaction();
            }
        }

    }

    protected boolean batchDelete(String whereClause, ArrayList<String> argList){
        SQLiteDatabase sqliteDb = null;
        try{
            sqliteDb = this.mDbHelper.getWritableDatabase();
        }catch(SQLiteException ex){
            Log.e(TAG, ex.toString());
            throw ex;
        }

        int failedNum = 0;
        sqliteDb.beginTransaction();
        try{
            for(String arg : argList){
                if("".equals(arg)){
                    throw new SQLException("Unable to delete card with empty cardId");
                }
                String[] whereArgs = new String[]{arg};
                int ret = sqliteDb.delete(this.mTableName, whereClause, whereArgs);
                if(ret == -1L){
                    failedNum ++;
                }
            }
            if(failedNum > 0){
                return false;
            }else{
                sqliteDb.setTransactionSuccessful();
                return true;
            }
        }finally{
            if(null != sqliteDb){
                sqliteDb.endTransaction();
            }
        }
    }

    protected int getCurrentTime(){
        int currentTime = (int)(System.currentTimeMillis() / 1000);
        return currentTime;
    }
}

