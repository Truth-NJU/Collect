package com.collect.util;

import com.collect.config.FileConfig;
import com.collect.exception.MyException;
import com.collect.util.strategy.PathEncryptStrategy;
import com.collect.vo.http.BaseHttpStatus;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class FileUtil {

    public static String TASK_DESC, TASK_PUBLISH, REPORT_IMAGE;

    private static PathEncryptStrategy pathEncryptStrategy;

    @Autowired
    FileConfig fileConfig;

    @PostConstruct
    void init() {
        TASK_DESC = fileConfig.getBase() + fileConfig.getTask_decr();
        TASK_PUBLISH = fileConfig.getBase() + fileConfig.getTask_publish();
        REPORT_IMAGE = fileConfig.getBase() + fileConfig.getReport_img();
        pathEncryptStrategy = fileConfig.getPathEncryptStrategy();
    }

    /**
     * 存储文件
     *
     * @param directoryPath 文件目录
     * @param file          文件
     * @return 路径+新的random的文件名
     */
    public static String save(MultipartFile file, String directoryPath, Object... extraPath) {
        // 拼接额外路径
        String extraSPath = pathEncryptStrategy.getPath(extraPath);
        // 获得完整路径
        String fullPath = directoryPath + extraSPath;
        // 检查路径是否存在，不存在则创建
        if (!checkDirectoryPath(fullPath)) {
            throw MyException.of(BaseHttpStatus.FILE_PATH_ERROR);
        }
        // 原文件名
        String originalName = file.getOriginalFilename();
        String newName;
        if (originalName != null && originalName.lastIndexOf(".") != -1) {
            // 取扩展名
            String ext = originalName.substring(originalName.lastIndexOf("."));
            // 构造新文件名
            newName = UUID.randomUUID() + ext;
        } else
            newName = UUID.randomUUID().toString();
        try {
            // 根据目标地址构造文件输出流
            FileOutputStream fileOutputStream = new FileOutputStream(fullPath + newName);
            // 将文件复制到映射的地址
            FileCopyUtils.copy(file.getInputStream(), fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extraSPath + newName;
    }

    /**
     * 检查文件类型
     *
     * @param file 文件
     * @param type 不同的文件类型
     * @return boolean
     */
    public static boolean checkFileType(MultipartFile file, String... type) {
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.lastIndexOf(".") != -1) {
            String ext = originalName.substring(originalName.lastIndexOf(".") + 1);
            for (String t : type) {
                if (ext.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param directoryPath 文件根路径
     * @param fileName      文件子路径+文件名
     * @return
     */
    public static boolean delete(String directoryPath, String fileName) {
        if (StringUtils.hasText(fileName)) {
            File file = new File(directoryPath + fileName);
            if (file.exists()) {
                // 当且仅当文件被成功删除后返回true
                if (file.delete()) {
                    File directory = file.getParentFile();
                    if (directory.isDirectory() && directory.list().length == 0) {
                        directory.delete();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 完全删除文件夹及其下文件
     *
     * @param path 路径
     */
    public static void heavyDelete(String path) {
        Path paths = Paths.get(path);
        try (Stream<Path> walk = Files.walk(paths)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(s -> {
                        try {
                            Files.delete(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException ignored) {
        }
    }

//    /**
//     * 将多个文件路径拼接为一个完整路径
//     *
//     * @param path
//     * @return
//     */
//    private static String getFullPath(Object... path) {
//        StringBuilder s = new StringBuilder();
//        for (Object p : path) {
//            s.append(p).append(File.separator);
//        }
//        return s.toString();
//    }

    /**
     * 检查该路径在系统中是否存在，若不存在则创建，
     * 创建失败则返回 false
     *
     * @param path
     * @return boolean
     */
    private static boolean checkDirectoryPath(String path) {
        File dir = new File(path);
        // 如果文件夹不存在则创建
        if (!dir.exists() && !dir.isDirectory()) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 将文件转化为MultipartFile
     * @param picPath，路径
     * @return 文件的MultipartFile格式
     */
    public static MultipartFile getMulFileByPath(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        MultipartFile mfile = new CommonsMultipartFile(fileItem);
        return mfile;
    }

    /**
     * 将文件转化为FileItem
     * @param filePath，路径
     * @return 文件的FileItem格式
     */
    private static FileItem createFileItem(String filePath) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem item = factory.createItem(textFieldName, "text/plain", true,
                "MyFileName" + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(newfile);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192))
                    != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

}
