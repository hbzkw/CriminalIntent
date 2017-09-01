package com.zhongjiaxin.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhongjiaxin.criminalintent.database.CrimeBaseHelper;
import com.zhongjiaxin.criminalintent.database.CrimeCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.zhongjiaxin.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by zhaokaiwen on 2017/8/24.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    //    private List<Crime> mCrimes;  //注释掉 使用sqlite数据库
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();//获取可以写入的数据库对象
//        mCrimes = new ArrayList<>();
//        for (int i = 0 ; i <100 ; i++){
//            Crime crime = new Crime();
//            crime.setmTitle("Crime #" + i);
//            crime.setmSolved(i%2 == 0);
//            mCrimes.add(crime);
//
//        }
    }

    /**
     * 获取陋习列表
     *
     * @return mCrimes
     */
    public List<Crime> getCrimes() {
//        return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }

        return crimes;
    }

    /**
     * 获取指定id的陋习
     *
     * @param id
     * @return crime
     */
    public Crime getCrime(UUID id) {
//        for (Crime crime : mCrimes){
//            if (crime.getmId().equals(id)){
//                return crime;
//            }
//        }
//        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + "= ?", new String[]{id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    /**
     * 手动生成crime
     *
     * @param c
     */
    public void addCrime(Crime c) {
//        mCrimes.add(c);
        ContentValues values = getContentValues(c);//获取contentValues
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    /**
     * 删除crime
     *
     * @param c
     */
    public void delCrime(Crime c) {
        String uuidString = c.getmId().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "= ?", new String[]{uuidString});
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getmId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + "= ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getmId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getmTitle());
        values.put(CrimeTable.Cols.DATE, crime.getmDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.ismSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getmSuspect());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
//    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,//Columns = null selects all columns
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having
                null//orderBy
        );
//        return cursor;
        return new CrimeCursorWrapper(cursor);
    }
}
