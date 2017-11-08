package com.example;

import kr.asv.salary.TableDictionary;
import kr.asv.sqlite.SQLiteHandler;

public class MyClass {
    public static void main(String[] args)
    {
        System.out.println("[SHH]Start");

        SQLiteHandler dbHandler = new SQLiteHandler("salarytax_information.db");
        TableDictionary tableDictionary = new TableDictionary(dbHandler.getDatabase());
        tableDictionary.createWithInsert();

        dbHandler.writeHashFile();

        //tableDictionary.test();
        //System.out.println(tableDictionary.dataList.toString());



    }
}
