/**
 * 
 */
package com.iie.util;

/**
 * @author hadoop
 *
 */
import java.io.BufferedWriter;

import java.io.File;
 
import java.io.FileOutputStream;
 
import java.io.IOException;
 
import java.io.OutputStreamWriter; 
 
public class TxtWriter { 
 
    public static void appendToFile(String content, File file) throws IOException {
        TxtWriter.appendToFile(content, file, System.getProperty("file.encoding"));
    }
    public static void appendToFile(String content, File file, String encoding) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), encoding));
            writer.write(content);
            writer.newLine();
        } finally {
         if (writer != null)
 
                writer.close();
 
        }
    } 
  
 
    public static void saveToFile(String content, File file) throws IOException {
 
        TxtWriter.saveToFile(content, file, System.getProperty("file.encoding"));
    } 
  
 
    public static void saveToFile(String content, File file, String encoding) throws IOException {
 
        BufferedWriter writer = null;
 
        try {
 
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encoding));
 
            writer.write(content);
            writer.write("\n");
 
        } finally {
 
            if (writer != null)
 
                writer.close();
 
        }
    }
}