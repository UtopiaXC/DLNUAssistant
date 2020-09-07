package com.utopiaxc.urpassistant.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLHelperGradesList extends SQLiteOpenHelper {

    private static Integer Version = 1;


    public SQLHelperGradesList(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists grades" +
                "(ClassId text," +
                "ClassName text, " +
                "Credit text," +
                "ClassAttribute text," +
                "Grade text)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + "grades";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}
