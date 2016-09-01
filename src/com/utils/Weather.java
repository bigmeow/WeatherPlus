package com.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * 天气接口类
 * @author running@vip.163.com
 *
 */
public final class Weather {
	/**
	 * 密钥集合（单个密钥每天免费调用3000次，劳资多注册几个密钥，不就够用了，真TM机智）
	 * TODO　密钥调用次数超过异步自动发邮件告警；自动移除当天调用次数超过的密钥；每天24点时被移除的密钥自动恢复
	 */
	private static String[] KEYS=new String[]{"0dd8ae27c2c9497898c72d3568c79aa7"};
	
	/**
	 * api接口地址，文档：http://www.heweather.com/documents/api
	 */
	private static final String WEATHER_API="https://api.heweather.com/x3/weather";
	private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
	private static final Random random=new Random();
	public static void main(String[] args) {
		System.out.println(Weather.fetchInfo("双牌县"));
		
	}
	/**
	 * 传入城市名，返回天气信息
	 * @param cityName
	 * @return json字符串
	 */
	public static String fetchInfo(String cityName){
		if(null!=cityName&&cityName.length()!=0){
			//移除多余的后缀
			cityName=cityName.replaceAll("省|市|区|县","");
		}
		String result=null;
		Map<String, String> params = new HashMap<String, String>();// 请求参数
		params.put("city", cityName);
		params.put("key",KEYS[random.nextInt(KEYS.length)]);//随机取密钥集合中的一个
		try {
			result = net(WEATHER_API, params, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 *
	 * @param strUrl 请求地址
	 * @param params 请求参数
	 * @param method 请求方法
	 * @return 网络请求字符串
	 * @throws Exception
	 */
	private static String net(String strUrl, Map<String, String> params, String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if (method == null || method.equals("GET")) {
				strUrl += params == null ? "" : ((strUrl.indexOf("?") > -1 ? "&" : "?") + urlencode(params));
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				try {
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(urlencode(params));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	// 将map型转为请求参数型
	private static String urlencode(Map<String, String> data) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=")
						.append(URLEncoder.encode(i.getValue() + "", "UTF-8"))
						.append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
}
