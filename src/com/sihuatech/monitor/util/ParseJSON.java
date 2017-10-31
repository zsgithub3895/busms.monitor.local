package com.sihuatech.monitor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onewaveinc.mip.log.Logger;

public class ParseJSON {
	private static final Logger log = Logger.getInstance(ParseJSON.class);
	public static String saxJSON(String node, String jsonStr) {
		JSONObject jo = null;
		try {
			jo = JSON.parseObject(jsonStr);
		} catch (Exception e) {
			log.error("json解析失败！！", e);
		}
		String[] partterns = node.split("/");
		JSONObject temp = jo;
		String res = null;
		for (int i = 0; i < partterns.length; i++) {
			Object j = temp.get(partterns[i]);
			if (j instanceof String) {
				if (i < partterns.length - 1) {
					break;
				} else {
					res = String.valueOf(j);
				}
			} else if (j instanceof JSONObject) {
				temp = (JSONObject) j;
			} else if (j instanceof JSONArray) {
				if (((JSONArray) j).size() == 0) {
					break;
				}
				temp = ((JSONArray) j).getJSONObject(0);
			}
		}
		if (null == res) {
			log.info("JSON报文中所匹配节点不存在或拼写错误!");
		} else {
			log.info("+++++++++++++++++++++++++++JSON中匹配节点的值" + res);
		}
		return res;
	}

	/*
	 * public static void main(String[] args){ String a=
	 * "{\"msgRetryIntvTime\":\"10\",\"pingCount\":\"3\",\"pingIntvTime\":\"60\",\"pingRespTime\":\"60\",\"retryCount\":\"3\",\"retryIntvTime\":\"180\",\"stbxmppId\":\"825010251180291@xmppa.njcatv.net\",\"s\":[{\"a\":\"12345669797\",\"b\":\"123456\"}]}"
	 * ; // saxJSON("s/a",a); System.out.println(a.trim().startsWith("<"));
	 * System.out.println(a.trim().startsWith("{")); }
	 */
}
