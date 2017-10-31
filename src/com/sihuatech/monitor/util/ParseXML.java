package com.sihuatech.monitor.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.digester.Digester;

import com.onewaveinc.mip.log.Logger;

public class ParseXML {
	/**
	 * 解析xml
	 */
	private static final Logger log = Logger.getInstance(ParseXML.class);

	public static String saxXML(String node, String xml) {
		Digester digester = new Digester();
		digester.addObjectCreate(node, XMLObject.class);
		digester.addBeanPropertySetter(node, "value");
		InputStream is;
		try {
			is = new ByteArrayInputStream(xml.getBytes());
			digester.parse(is);
			XMLObject o = (XMLObject) digester.getRoot();
			if (null == o) {
				log.info("所匹配节点不存在或拼写错误!");
			} else {
				log.info("+++++++++++++++++++++++++++节点的值" + o.getValue());
				return o.getValue();
			}
		} catch (Exception e) {
			log.error("解析失败！！", e);
		}
		return null;
	}

}
