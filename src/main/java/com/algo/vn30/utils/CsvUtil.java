package com.algo.vn30.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvUtil {

    public static void writeData(String filePath, List<String[]> data)
    {
        try {
            File file = new File(filePath);
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            for (String [] data1: data) {
                writer.writeNext(data1);
            }
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static List<String[]> readData(String filePath)
    {
        File file = new File(filePath);
        List<String []> results = new ArrayList<>();
        try {
            FileReader outputfile = new FileReader(file);
            CSVReader reader = new CSVReader(outputfile);
            results = reader.readAll();
            reader.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return results;
    }
}
