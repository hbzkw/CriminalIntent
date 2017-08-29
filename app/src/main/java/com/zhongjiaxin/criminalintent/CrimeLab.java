package com.zhongjiaxin.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zhongjiaxin.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhaokaiwen on 2017/8/24.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
//    private List<Crime> mCrimes;  //注释掉 使用sqlite数据库
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
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
     * @return mCrimes
     */
    public List<Crime> getCrimes(){
        return new ArrayList<>();
    }

    /**
     * 获取指定id的陋习
     * @param id
     * @return crime
     */
    public Crime getCrime(UUID id){
//        for (Crime crime : mCrimes){
//            if (crime.getmId().equals(id)){
//                return crime;
//            }
//        }
        return null;
    }

    /**
     * 手动生成crime
     * @param c
     */
    public void addCrime(Crime c){
//        mCrimes.add(c);
    }
}
