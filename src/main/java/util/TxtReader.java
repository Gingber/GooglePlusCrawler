/**
 * 
 */
package util;

/**
 * @author Gingber
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader; 
import java.util.ArrayList;
import java.util.List;

  
 
public class TxtReader { 
  
    /**
     * 从文件中读取文本内容, 读取时使用平台默认编码解码文件中的字节序列
     * @param file 目标文件
     * @return
     * @throws IOException
     */
    public static String loadStringFromFile(File file) throws IOException {
        return TxtReader.loadStringFromFile(file, System.getProperty("file.encoding"));
    } 
  
 
    /**
     * 从文件中读取文本内容
    * @param file 目标文件
     * @param encoding 目标文件的文本编码格式
     * @return
     * @throws IOException
     */
    public static String loadStringFromFile(File file, String encoding) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            StringBuilder builder = new StringBuilder();
            char[] chars = new char[4096];
 
            int length = 0;
 
            while (0 < (length = reader.read(chars))) {
 
                builder.append(chars, 0, length);
 
            }
 
            return builder.toString();
 
        } finally {
 
            try {
 
                if (reader != null) reader.close();
 
            } catch (IOException e) {
 
                throw new RuntimeException(e);
 
            }
 
        }
    } 
    
    public static ArrayList<String> loadVectorFromFile(File file, String encoding) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            ArrayList<String> list = new ArrayList<String>();
            String row = null;
            while((row = reader.readLine())!=null){
                //System.out.println(row);
            	list.add(row);
            }
 
            return list;
 
        } finally {
 
            try {
 
                if (reader != null) reader.close();
 
            } catch (IOException e) {
 
                throw new RuntimeException(e);
 
            }
 
        }
    } 
}
