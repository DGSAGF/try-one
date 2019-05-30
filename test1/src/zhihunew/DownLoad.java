package zhihunew;


import http.JsdxHTTPClient;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownLoad {
    public static boolean downLoadPics(ArrayList<String> zhihuPicUrls, String dir,String pageSize) throws Exception {
        // 创建
        File fileDir = new File(dir);
        fileDir.mkdirs();
        // 初始化一个变量，用来显示图片编号
        int i = 1;
        // 循环下载图片
        for (String zhiHuPic : zhihuPicUrls) {
        	zhiHuPic=zhiHuPic.replaceAll("_b", "");
        	String filename=zhiHuPic.substring(zhiHuPic.lastIndexOf("/")+1);
			byte[] bs = JsdxHTTPClient.get_zhihu(zhiHuPic,"utf-8");
            String newImageName = dir + "/" +pageSize+"_"+i+"_"+filename;
			//实例输出一个对象
			FileOutputStream fos = new FileOutputStream(new File(newImageName));
			fos.write(bs);
            fos.close();
            System.out.println("第 " + i + "张图片下载完毕......");
            i++;
        }
        return true;
    }
    
    public static boolean downLoadPics2(ArrayList<String> zhihuPicUrls, String dir) throws Exception {
        // 创建
        File fileDir = new File(dir);
        fileDir.mkdirs();
        // 初始化一个变量，用来显示图片编号
        int i = 1;
        // 循环下载图片
        for (String zhiHuPic : zhihuPicUrls) {
        	zhiHuPic=zhiHuPic.replaceAll("_b", "");
                       // 打开和URL之间的连接
        	URLConnection connection = null;
        	URL url = new URL(zhiHuPic);
            connection = url.openConnection();

	        // 设置通用的请求属性
	        connection.setRequestProperty("Referer","https://www.zhihu.com/question/62209505");
	        connection.setRequestProperty("origin","https://www.zhihu.com");
	        connection.setRequestProperty("x-udid","换成自己的udid值");
	        connection.setRequestProperty("Cookie","换成自己的cookie值");
	        connection.setRequestProperty("accept", "application/json, text/plain, */*");
	        connection.setRequestProperty("connection", "Keep-Alive");
	        connection.setRequestProperty("Host", "www.zhihu.com");
	        connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
	        // 建立实际的连接
	        connection.connect();
			InputStream dis = connection.getInputStream();

            String newImageName = dir + "/" + "图片" + i + ".jpg";
            // 建立一个新的文件
            FileOutputStream fos = new FileOutputStream(new File(newImageName));
			byte[] buffer = new byte[1024];
            int length;
            System.out.println("正在下载......第 " + i + "张图片......请稍后");
            // 开始写入
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            dis.close();
            fos.close();
            System.out.println("第 " + i + "张图片下载完毕......");
            i++;
        }
        return true;
    }
}