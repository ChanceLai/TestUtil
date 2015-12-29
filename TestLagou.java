

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.http.client.config.CookieSpecs;
/**
 * Created by chance on 2015/12/25.
 */
public class TestLagou {

    public static void main(String[] args) throws IOException, InterruptedException {
        //创建一个HttpClient
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        try {
            //创建一个get请求用来接收_xsrf信息
            HttpGet get = new HttpGet("https://passport.lagou.com/login/login.html");
            //获取_xsrf
            CloseableHttpResponse response = httpClient.execute(get);
            String homeCookie = setCookie(response);
////            String responseHtml = EntityUtils.toString(response.getEntity());
////            String xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
//            System.out.println("xsrfValue:" + xsrfValue);
            response.close();

            //构造post数据
            List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("username", "******"));
            valuePairs.add(new BasicNameValuePair("password", "4b994424ac42642be3761d98e7b27580"));
            valuePairs.add(new BasicNameValuePair("request_form_verifyCode", null));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);

            //创建一个post请求
            HttpPost loginRequest = new HttpPost("https://passport.lagou.com/login/login.json");
            //post.setHeader("Cookie", " cap_id=\"YjA5MjE0YzYyNGQ2NDY5NWJhMmFhN2YyY2EwODIwZjQ=|1437610072|e7cc307c0d2fe2ee84fd3ceb7f83d298156e37e0\"; ");

            //注入post数据
            loginRequest.addHeader("Cookie", homeCookie);
            loginRequest.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(loginRequest);
            //打印登录是否成功信息
            printResponse(httpResponse);
            String loginCookie = setCookie(httpResponse);


            HttpGet accountRequest = new HttpGet("http://account.lagou.com/account/accountBind.html");

            accountRequest.setHeader("Cookie", loginCookie);
            CloseableHttpResponse accountRp = httpClient.execute(accountRequest);
            String accountCookie = setCookie(httpResponse);
            String accountContent = EntityUtils.toString(accountRp.getEntity());
            System.out.println(accountContent);
            accountRp.close();

            for(int per = 10;per <90 ; per++) {
                HttpGet smsRequest = new HttpGet("http://account.lagou.com/account/sendBindPhoneVerifyCode.json?phone=156769501"+per);
                smsRequest.setHeader("Cookie", accountCookie);
                CloseableHttpResponse smsResponse = httpClient.execute(smsRequest);
                String content = EntityUtils.toString(smsResponse.getEntity());
                System.out.println(content);
                smsResponse.close();
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        System.out.println("status:" + httpResponse.getStatusLine());
        System.out.println("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            System.out.println("response length:" + responseString.length());
            System.out.println("response content:"
                    + responseString.replace("\r\n", ""));
        }
    }

    public static Map<String, String> cookieMap = new HashMap<String, String>(64);

    //从响应信息中获取cookie
    public static String setCookie(HttpResponse httpResponse) {
        System.out.println("----setCookieStore");
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length == 0) {
            System.out.println("----there are no cookies");
            return null;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if (i != headers.length - 1) {
                cookie += ";";
            }
        }

        String cookies[] = cookie.split(";");
        for (String c : cookies) {
            c = c.trim();
            if (cookieMap.containsKey(c.split("=")[0])) {
                cookieMap.remove(c.split("=")[0]);
            }
            cookieMap.put(c.split("=")[0], c.split("=").length == 1 ? "" : (c.split("=").length == 2 ? c.split("=")[1] : c.split("=", 2)[1]));
        }
        System.out.println("----setCookieStore success");
        String cookiesTmp = "";
        for (String key : cookieMap.keySet()) {
            cookiesTmp += key + "=" + cookieMap.get(key) + ";";
        }

        return cookiesTmp.substring(0, cookiesTmp.length() - 2);


    }

}
