package template;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA
 * User yugai
 * Time 17/2/24.
 */
public class FileUtil {

    public static boolean pathDirExists(String path) {
        return pathDirExists(new File(path));
    }

    // 判断文件夹是否存在
    public static boolean pathDirExists(File file) {
        boolean isExists = false;
        if (file.exists()) {
            if (file.isDirectory()) {
                System.out.println("dir exists");
                isExists = true;
            } else {
                System.out.println("the same name file exists, can not create dir");
                isExists = false;
            }
        }
        return isExists;
    }

    public static String readFile(Class clas, boolean isKotlin, String filename) {
        String name = "code/" + filename;
        if (isKotlin) {
            name = "kt/" + filename;
        }
        InputStream in = null;
        in = clas.getResourceAsStream(name);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    public static void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

    public static String traverseFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    System.out.println("文件夹:" + file2.getAbsolutePath());
                    if (file2.getName().endsWith("mvp")) {
                        return file2.getAbsolutePath();
                    }
                    list.add(file2);
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        if (file2.getName().endsWith("mvp")) {
                            return file2.getAbsolutePath();
                        }
                        list.add(file2);

                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        System.out.println("没有发现文件");
        return "";
    }
}
