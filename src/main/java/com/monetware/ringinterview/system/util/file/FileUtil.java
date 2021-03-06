package com.monetware.ringinterview.system.util.file;

import lombok.Cleanup;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class FileUtil {


    /**
     * 上传文件并且返回文件名集合
     *
     * @param files
     * @param path
     * @return
     */
    public static List<Map<String, Object>> uploadFiles(MultipartFile[] files, String path) {
        List<Map<String, Object>> result = new ArrayList<>();
        File filePath = new File(path);
        OutputStream out = null;
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        //判断file数组不能为空并且长度大于0
        if (files != null && files.length > 0) {
            //循环获取file数组中得文件
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                //保存文件
                if (!file.isEmpty()) {
                    try {
                        //转存文件 file.getOriginalFilename();文件原名称包括后缀名
                        String originName = file.getOriginalFilename();
                        Date now = new Date();
                        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(now) + file.getOriginalFilename().trim().substring(file.getOriginalFilename().trim().lastIndexOf("."));
//                        file.transferTo(new File(path + fileName));
                        byte [] bytes = file.getBytes();
                        out = new FileOutputStream(new File(path, fileName));
                        out.write(bytes);
                        Map<String, Object> res = new HashMap<>();
                        res.put("fileName", fileName);
                        res.put("fileSize", file.getSize());
                        res.put("originName", originName);
                        result.add(res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param path
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String path, String fileName) {
        File filePath = new File(path);
        if (filePath.exists()) {
            try {
                File[] files = filePath.listFiles();
                if (files.length > 0) {
                    for (File tempFile : files) {
                        if (tempFile.getName().equals(fileName)) {
                            tempFile.delete();
                            break;
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 下载文件到客户端
     *
     * @param path
     * @param response
     * @throws Exception
     */
    public static void downloadFileToClient(String path, HttpServletResponse response) throws Exception {
        File filePath = new File(path);
        @Cleanup
        InputStream is = new FileInputStream(filePath);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(filePath.getName(), "UTF-8") + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        @Cleanup
        BufferedInputStream bis = null;
        @Cleanup
        BufferedOutputStream bos = null;
        bis = new BufferedInputStream(is);
        bos = new BufferedOutputStream(out);
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
    }

    /**
     * @param bytes 转换得字节
     * @param si    是否需要单位
     * @return
     */
    public static String byteFormat(Long bytes, Boolean si) {
        if (bytes == null || bytes == 0L) return "0B";
        String[] units = new String[]{" B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB"};
        int unit = 1024;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        double pre = 0;
        if (bytes > 1024) {
            pre = bytes / Math.pow(unit, exp);
        } else {
            pre = (double) bytes / (double) unit;
        }
        if (si) {
            return String.format(Locale.ENGLISH, "%.1f%s", pre, units[(int) exp]);
        }
        return String.format(Locale.ENGLISH, "%.1f", pre);
    }



}
