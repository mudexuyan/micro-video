package edu.ustc.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class QiniuOssUtil {

    private static String accessKey = "JHp93XBDkXcatVjjEpOpKLsfZSMVWobZtSL1AAzk";
    private static String secretKey = "Npfi1hfmKHP9uX9Blxgu1o6wmbk2Sc0QYZ1924tw";
    private static String bucket = "micro-video-3";

     // "http://rjgrxz94l.bkt.clouddn.com/videos/test3.mp4?vframe/jpg/offset/1";

    private static String url = "http://rri7sgufa.hd-bkt.clouddn.com";
//    private static String url = "http://rpjnwu1kx.hd-bkt.clouddn.com";




    public static String upload(File file, String path, String fileName) throws FileNotFoundException {
        return upload(new FileInputStream(file), path, fileName);
    }

    /**
     * 上传文件
     */
    public static String upload(InputStream inputStream, String path, String fileName) {

        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本

        UploadManager uploadManager = new UploadManager(cfg);
        String key = path + "/" + fileName;

//       ByteArrayInputStream byteInputStream=new ByteArrayInputStream(inputStream);

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(inputStream, key, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }

        return url+"/"+path+"/"+fileName;
    }

}
