/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-8
 */
package com.hp.ucmdb.report.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
	private static String convertStreamToString(InputStream is) {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            return sb.toString();
        }
        return "";
    }
	
	/**
     * Read to string from a file
     * 
     * @param file
     *            file to read
     * @return file contents in String
     */
    public static String readFileToString(File file) {
        InputStream is;
        try {
            is = new FileInputStream(file);
            return convertStreamToString(is);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    
	/**
     * 
     * Read file content from an absolute path
     * 
     * @param fileAbsolutePath
     *            fileAbsolutePath
     * @return file content
     */
    public static String readFileToString(String fileAbsolutePath) {
        File file = new File(fileAbsolutePath);
        return readFileToString(file);
    }
}
