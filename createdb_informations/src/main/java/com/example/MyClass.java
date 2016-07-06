package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.asv.salary.TableDictionary;
import kr.asv.sqlite.SQLiteAdapter;
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
