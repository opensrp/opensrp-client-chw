package org.smartregister.chw.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static boolean hasExternalDisk() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean canWriteToExternalDisk() {
        if (!hasExternalDisk())
            return false;

        return !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    public static File createDirectory(String directoryPath, boolean onSdCard) throws Exception {
        File location = onSdCard ? Environment.getExternalStorageDirectory() : Environment.getDataDirectory();
        File dir = new File(location + File.separator + directoryPath);
        if (dir.exists())
            return dir;

        if (!dir.mkdirs())
            throw new FileException("Directory was not created successfully " + dir.getAbsolutePath());

        return dir;
    }

    public static String getStringFromFile(String folder, String name) throws Exception {
        File fl = new File(folder, name);
        if (!fl.exists()) return null;

        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean writeToExternalDisk(String directoryPath, byte[] bytes, String fileName) throws Exception {
        File dir = createDirectory(directoryPath, canWriteToExternalDisk());
        boolean created = dir.exists();
        if (created) {
            File file = new File(dir, fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        } else {
            throw new FileException("cannot write to sd card");
        }

        return true;
    }

    public static File[] getFiles(String folder) {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + folder);
        return directory.listFiles();
    }

    public static class FileException extends Exception {
        public FileException(String message) {
            super(message);
        }
    }
}
