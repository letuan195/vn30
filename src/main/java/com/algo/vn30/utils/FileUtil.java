package com.algo.vn30.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static InputStream getInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> File2List(InputStream stream) {
        BufferedReader bufferedReader = null;
        List<String> lines = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(stream));
            boolean eof = false;
            while (!eof) {
                String line = bufferedReader.readLine();
                if (line != null)
                    lines.add(line.trim());
                else
                    eof = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static String readEmails() {
        String root = System.getProperty("user.dir");
        String path = root + "/data/emails.txt";
        File file = new File(path);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                return st;
        } catch (Exception e) {
            e.printStackTrace();
        }

       return "tuanlv.k57@gmail.com";
    }

    public static String readData() {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("emails.txt");
        List<String> rs = File2List(is);
        if (rs.size() > 0) {
            return rs.get(0);
        }
        return "tuanlv.k57@gmail.com";
    }
}

