package com.example.punit.stockacer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by punit on 21/12/17.
 */

public class Account extends SQLiteOpenHelper {
    final static String TABLENAME="UserIstocks";
    final static String ID="_ID";
    final static String NAME="CName";
    final static String AGE="age";
    //SQLiteDatabase db;
    Context context;
    static String DBNAME="";
    //String user;
    Account(Context context, String name, int version){
        super(context,name,null,version);
        DBNAME=name;
        this.context=context;
        //this.user=user;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1="create table UserIstocks("+ID+" Integer auto_increment ,CName TEXT(20) , BTarget Real Default(-1) , STarget Real Default(-1));";
        db.execSQL(query1);
        String query2="create table Portfolio( CNAME TEXT(20) , Units Integer Default(0) , CPrice Real Default(0) , SPrice Real Default(0) , PL Real Default(0));";
        db.execSQL(query2);
        String query3="create table Balance( Balance Real );";
        db.execSQL(query3);
        ContentValues values=new ContentValues();
        values.put("Balance",100000);
        db.insert("Balance",null,values);
        Log.e("Table status","Tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table "+TABLENAME);
        onCreate(db);

    }

    public float getBTarget(String company){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.rawQuery("select * from UserIstocks where CName='"+company+"'",null);
        if(c.moveToFirst()){

            return c.getFloat(c.getColumnIndex("BTarget"));
        }
        return -1;
    }

    public void setBTarget(String company,float value){
        SQLiteDatabase db=getWritableDatabase();
        Boolean status=true;
        String query="Select * from UserIstocks where CName='"+company+"';";
        Cursor c=db.rawQuery(query,null);
        ContentValues values=new ContentValues();
        values.put("BTarget",value);
        if(!c.moveToFirst()){
            values.put("CName",company);
            db.insert(TABLENAME,null,values);
        }
        else {
            db.update("UserIstocks", values, "CName = '" + company + "'", null);
        }
            }

    public float getSTarget(String company){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.rawQuery("select * from UserIstocks where CName='"+company+"'",null);
        if(c.moveToFirst()){

            return c.getFloat(c.getColumnIndex("STarget"));
        }
        return -1;
    }

    public void setSTarget(String company,float value){
        SQLiteDatabase db=getWritableDatabase();
        Boolean status=true;
        String query="Select * from UserIstocks where CName='"+company+"';";
        Cursor c=db.rawQuery(query,null);
        ContentValues values=new ContentValues();
        values.put("STarget",value);
        if(!c.moveToFirst()){
            values.put("CName",company);
            db.insert(TABLENAME,null,values);
        }
        else {
            db.update("UserIstocks", values, "CName = '" + company + "'", null);
        }
    }



public double getBalance(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.rawQuery("select * from Balance ",null);
        if(c.moveToFirst()){

            return Math.round(c.getDouble(c.getColumnIndex("Balance"))*100)/100;
        }
        return -1;
}

    public void buy(int no_of_shares, String company, float cprice) {
        SQLiteDatabase db=getWritableDatabase();
        Boolean status=true;
        String query="Select * from Portfolio where CName='"+company+"';";
        Cursor c=db.rawQuery(query,null);
        if(!c.moveToFirst()){
            ContentValues values=new ContentValues();
            values.put("Units",no_of_shares);
            values.put("CPrice",cprice);
            values.put("CName",company);
            db.insert("Portfolio",null,values);
        }
        else {
            db.execSQL("Update Portfolio set Units=Units+'"+no_of_shares+"' , CPrice=CPrice+'"+cprice+"' where CName='"+company+"' ;");

        }



        Log.e("update query status","success");
    }

    public void setBalance(double balance) {
    SQLiteDatabase db=getWritableDatabase();
    db.execSQL("update Balance set Balance='"+balance+"' ;");
    Log.e("Balance updation status","success");
    }

    public int getUnits(String company) {
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.rawQuery("select * from Portfolio where CName='"+company+"'",null);
        if(c.moveToFirst()){

            return c.getInt(c.getColumnIndex("Units"));
        }
        return -1;
    }

    public float getCost(String company) {
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.rawQuery("select * from Portfolio where CName='"+company+"'",null);
        if(c.moveToFirst()){

            return c.getFloat(c.getColumnIndex("CPrice"));
        }
        return -1;
    }

    public void sell(Integer no_of_shares, String company, float sprice, float profit, float updateCost) {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Update Portfolio set Units=Units-'"+no_of_shares+"' , SPrice=SPrice+'"+sprice+"' , PL=PL+'"+profit+"', CPrice=CPrice-'"+updateCost+"' where CName='"+company+"' ;");

    }

    public ArrayList<HashMap<String,String>> getPortfolio() {
        ArrayList<HashMap<String,String>> list=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.rawQuery("select * from Portfolio",null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            HashMap<String,String> map=new HashMap<>();
            map.put("CName",c.getString((0)));
            map.put("Units",c.getInt(c.getColumnIndex("Units"))+"");
            map.put("CPrice",(Math.round(c.getFloat(c.getColumnIndex("CPrice")))*100)/100+"");
            map.put("SPrice",(Math.round(c.getFloat(c.getColumnIndex("SPrice")))*100)/100+"");
            map.put("PL",(Math.round(c.getFloat(c.getColumnIndex("PL")))*100)/100+"");
            list.add(map);
            c.moveToNext();
        }
        return list;

    }
}



