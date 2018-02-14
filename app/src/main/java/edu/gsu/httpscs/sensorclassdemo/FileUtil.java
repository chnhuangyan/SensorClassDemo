package edu.gsu.httpscs.sensorclassdemo;

/**
 * Created by Yan on 2/3/18.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    public static String createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return path;
    }

    public static boolean writeBytes(String filePath, byte[] data) {
        createIfNotExist(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static boolean writeString(String file, String content) {
        try {
            byte[] data = content.getBytes("UTF-8");
            return writeBytes(file, data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static String readString(String file, String charset) {
        byte[] data = readBytes(file);
        String ret = null;

        try {
            ret = new String(data, charset);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }
}