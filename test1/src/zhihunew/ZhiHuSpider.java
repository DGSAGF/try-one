package zhihunew;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ZhiHuSpider {
    public static void main(String[] args){
        String baseUrl= "https://www.zhihu.com/api/v4/questions/28586345/answers?" +
                "include=data[*].is_normal,admin_closed_comment,reward_info," +
                "is_collapsed,annotation_action,annotation_detail,collapse_reason," +
                "is_sticky,collapsed_by,suggest_edit,comment_count,can_comment,content," +
                "editable_content,voteup_count,reshipment_settings,comment_permission," +
                "created_time,updated_time,review_info,relevant_info,question,excerpt," +
                "relationship.is_authorized,is_author,voting,is_thanked,is_nothelp;data[*].mark_infos[*].url;" +
                "data[*].author.follower_count,badge[?(type=best_answerer)].topics&sort_by=default";
        //存放每次获取的返回结果
        String responseResult = "";
        BufferedReader bufferedReader = null;
        //存放多有的的回答内容
        StringBuffer stringBuffer = new StringBuffer();
        //每次返回的回答数
        URLConnection connection = null;
        try {
            String urlToConnect = baseUrl+ "&limit="+20+"&offset="+0;
            URL url = new URL(urlToConnect);
            // 打开和URL之间的连接
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
	        // 定义 BufferedReader输入流来读取URL的响应
	        bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            responseResult += line;
	        }
	        System.out.println(responseResult);
	        //将返回结果转成map
	        Gson gson = new Gson();
	        Map<String, Object> map = new HashMap<String,Object>();
	        map = gson.fromJson(responseResult, map.getClass());
	        //获取page信息
	        LinkedTreeMap<String,Object> pageList = (LinkedTreeMap<String,Object>)map.get("paging");
	        //得到总条数
	        double totals = (Double)pageList.get("totals");
	        for(int offset = 0 ;offset < totals; offset += 20){
	            SpiderThread spiderThread = new SpiderThread(baseUrl,20,offset);
	            new Thread(spiderThread).start();
	        }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
                try {
                    if(bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
