/*
 *     Copyright (c) 2016 Meituan Inc.
 *
 *     The right to copy, distribute, modify, or otherwise make use
 *     of this software may be licensed only pursuant to the terms
 *     of an applicable Meituan license agreement.
 *
 */

package com.marsthink.fsdemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by zhoumao on 2017/6/22.
 */

public class FileUtil {

    /**
     * 如设备有sd卡，则返回sd卡路径，否则返回/data/data/packageName/路径；
     */
    public static String getStoragePath(Context context) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  // 获取sd卡目录
        } else {
            sdDir = context.getFilesDir();    //获取/data/data/packageName/
        }
        return sdDir.toString();
    }

    /**
     * 将字符串写入文件中；
     */
    public static void writeFile(String fileName, String content) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 按行读取文件；
     */
    public static String readFileByLine(String fileName) {
        String content = "";
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String readLine = in.readLine();
            while (readLine != null) {
                content += readLine;
                readLine = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return content;
    }

    /**
     * 一次读取出文件中所有内容
     * code 文件的编码，如"GB2312"
     */
    public static String readFileAll(String fileName, String code) {
        String content = "";
        File file = new File(fileName);

        try {
            if (!file.exists()) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String str = "";
//        FileInputStream in;
        char[] chars = new char[30];
        InputStreamReader in;
        try {
            in = new InputStreamReader(new FileInputStream(fileName));
            while (in.read(chars) != -1) {
                content = content + new String(chars);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return content;
    }

    /**
     * 按字节【读】取文件的内容；
     *
     * @param Offset   读取内容的开始出
     * @param length   内容的长度；
     * @param filePath 文件的路径；
     * @param code     编码；
     * @return 返回相应的内容；
     * @throws Exception
     */
    public String readFileByByte(int Offset, int length, String filePath,
                                 String code) {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fis.skip(Offset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = new byte[length];
        try {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new String(bytes, code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文件夹；
     */
    public static void delDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                delDir(new File(dir, children[i]));
            }
        }
        // 目录此时为空，可以删除
        dir.delete();
    }

    /**
     * 删除单个文件；
     */
    public static void delFile(File file) {
        Log.d("tag", "delete" + file.getAbsolutePath());
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    delFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

    /**
     * 复制单个文件；
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @param overlay      如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName,
                                   boolean overlay) {
        String MESSAGE;
        File srcFile = new File(srcFileName);

        // 判断源文件是否存在
        if (!srcFile.exists()) {
            MESSAGE = "源文件：" + srcFileName + "不存在！";
            Log.d("tag", MESSAGE);
            return false;
        } else if (!srcFile.isFile()) {
            MESSAGE = "复制文件失败，源文件：" + srcFileName + "不是一个文件！";
            Log.d("tag", MESSAGE);
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件夹
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/ " + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    Log.d("tag", "[COPY_FILE:" + temp.getPath() + "复制文件成功!]");
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/ " + file[i], newPath + "/ "
                            + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错 ");
            e.printStackTrace();
        }
    }

    // 判断文件是否存在
    public static void isFileExists(File file) {

        if (file.exists()) {
            Log.d("tag", "file exists");
        } else {
            Log.d("tag", "file not exists, create it ...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // 判断文件夹是否存在
    public static void isDirExists(File file) {

        if (file.exists()) {
            if (file.isDirectory()) {
                Log.d("tag", "dir exists");
            } else {
                Log.d("tag", "the same name file exists, can not create dir");
            }
        } else {
            Log.d("tag", "dir not exists, create it ...");
            file.mkdir();
        }
    }

    public static void wirteBinary(String filename) {
        try {
            DataOutputStream os = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filename)));
            os.writeInt(1001);
            os.writeByte(520);
            os.writeBoolean(true);
            os.writeFloat(10.0f);
            os.writeLong(100l);
            os.writeUTF("读写二进制文件");
            os.flush();
            os.close();
        } catch (IOException e) {

        }
    }

    public static void readBinary(String filename) {
        try {
            DataInputStream is = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(filename)));
            System.out.println(is.readInt());
            System.out.println(is.readByte());
            System.out.println(is.readBoolean());
            System.out.println(is.readFloat());
            System.out.println(is.readLong());
            System.out.println(is.readUTF());

            is.close();
        } catch (IOException e) {

        }
    }

    public static void writeobj(String filename)throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(new Person("lisi",20));
        oos.close();
    }
    public static Person readobj(String filename)throws Exception
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        Person p = (Person)ois.readObject();
        ois.close();
        return p;
    }

}
