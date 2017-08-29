package com.zhongjiaxin.criminalintent.database;

/**
 * Created by zhaokaiwen on 2017/8/29.
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crime";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE= "title";
            public static final String DATE= "date";
            public static final String SOLVED= "solved";
        }
    }

}
