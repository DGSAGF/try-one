package zhihunew;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpiderThread implements Runnable{
    private String baseUrl;
    private int limit;
    private int offset;
    private String title;
    private int num;
    private int pageSize;

    SpiderThread(String baseUrl,int limit,int offset){
        this.baseUrl = baseUrl;
        this.limit = limit;
        this.offset = offset;
    }
    public void run() {
    	String htmlStr=new String(sendGet(baseUrl,limit,offset));
        //System.out.println(delHTMLTag(new String(sendGet(baseUrl,limit,offset))));
    	String dir="D:/知乎爬虫/"+this.title;
    	try {
			downImgs(htmlStr,dir,String.valueOf(pageSize));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public  StringBuffer sendGet(String baseUrl,int limit,int offset) {
        //存放每次获取的返回结果
        String responseResult = "";
        BufferedReader bufferedReader = null;
        //存放多有的的回答内容
        StringBuffer stringBuffer = new StringBuffer();
        //每次返回的回答数
        //int num = 0;
        try {
            //更改链接的limit设置每次返回的回答条数, 更改offset设置查询的起始位置
            //即上一次的limit+offset是下一次的起始位置,经过试验,每次最多只能返回20条结果
            String urlToConnect = baseUrl + "&limit="+limit+"&offset="+offset;
            this.pageSize=offset/20;
            URL url = new URL(urlToConnect);
            // 打开和URL之间的连接
            URLConnection connection = url.openConnection();
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
            // 定义 BufferedReader输入流来读取URL的响应
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult += line;
            }
            //将返回结果转成map
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<String,Object>();
            map = gson.fromJson(responseResult, map.getClass());
            //获得包含回答的数组
            ArrayList<LinkedTreeMap<String,Object>> dataList = (ArrayList<LinkedTreeMap<String,Object>>)map.get("data");
            //追加每一条回答,用于返回
            // String str = null;
            if(this.title==null){
                this.title=(String) ((Map)dataList.get(0).get("question")).get("title");            	
            }

            for(LinkedTreeMap<String,Object> contentLink : dataList){
                stringBuffer.append(contentLink.get("content")+"\r\n\r\n");
                this.num++;//本次查询到多少条回答
            }
            System.out.println("回答条数====================================="+this.num);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        //返回本次查到的所有回答
        return stringBuffer;
    }

    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符
    /**
     * @param htmlStr
     * @return
     *  删除Html标签
     */
    public static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        return htmlStr.replaceAll(" ",""); // 返回文本字符串
    }
    
    public static void downImgs(String htmlStr,String dir,String pageSize) throws Exception {
    	ArrayList<String> zhihuPicUrl=new ArrayList<String>();
        // 匹配答案图片链接
    	Matcher m = Pattern.compile("</noscript><img.+?src=\"(https.+?)\".+?").matcher(htmlStr);
        boolean isFind;
        while (isFind= m.find()) {
            zhihuPicUrl.add(m.group(1));
        }
        DownLoad.downLoadPics(zhihuPicUrl,dir,pageSize);
    }

}
