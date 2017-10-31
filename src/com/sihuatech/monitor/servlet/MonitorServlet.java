package com.sihuatech.monitor.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onewaveinc.mip.log.Logger;
import com.sihuatech.datarecord.entity.DataRecordHistory;
import com.sihuatech.datarecord.manager.DataRecordHistoryManager;
import com.sihuatech.datastatus.entity.DataRecordStatus;
import com.sihuatech.datastatus.manager.DataRecordStatusManager;
import com.sihuatech.interfaceall.check.entity.CheckEntity;
import com.sihuatech.interfaceall.check.manage.CheckManage;
import com.sihuatech.interfaceall.interfaceconfig.entity.InterfaceConfig;
import com.sihuatech.interfaceinfo.manager.InterfaceInfoManager;
import com.sihuatech.monitor.entity.ResponseDataBean;
import com.sihuatech.monitor.service.AccessWebserviceMethod;
import com.sihuatech.monitor.util.ParseJSON;
import com.sihuatech.monitor.util.ParseXML;
import com.sihuatech.monitor.util.ServiceLocator;

import edu.emory.mathcs.backport.java.util.Arrays;

public class MonitorServlet extends HttpServlet {
	/**
	 * 
	 */
	private Logger logger = Logger.getInstance(MonitorServlet.class);
	private static final long serialVersionUID = 1260139475053643903L;
	InterfaceInfoManager interfaceInfoManager = (InterfaceInfoManager) ServiceLocator.getBean("com.sihuatech.InterfaceInfoManager");
	CheckManage checkmanage = (CheckManage) ServiceLocator.getBean("checkManage");
	DataRecordHistoryManager history=(DataRecordHistoryManager) ServiceLocator.getBean("com.sihuatech.DataRecordHistoryManager");
	DataRecordStatusManager monnitor_status=(DataRecordStatusManager) ServiceLocator.getBean("com.sihuatech.DataRecordStatusManager");

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<DataRecordHistory> dhlist = new ArrayList<DataRecordHistory>();
		List<DataRecordStatus> statuslist = new ArrayList<DataRecordStatus>();
		String interfaceCodeString = req.getParameter("interfaceCode");
		String[] interfaceCodes = interfaceCodeString.trim().substring(0, interfaceCodeString.length() - 1)
				.split("\\,");
		System.out.println("++++++++++++++++++++++++++++++++++++" + Arrays.asList(interfaceCodes));
		for (String interfaceCode : interfaceCodes) {
			boolean flag = getCheckResult(interfaceCode, dhlist, statuslist);
		}
		insertHistoryAndStatus(dhlist, statuslist);
	}
	
	public boolean getCheckResult(String interfaceCode, List<DataRecordHistory> dhlist,
			List<DataRecordStatus> statuslist) {
		List<InterfaceConfig> lists = interfaceInfoManager.findByInterfaceCode(interfaceCode);
		for (InterfaceConfig icb : lists) {
			DataRecordHistory dh = new DataRecordHistory();
			DataRecordStatus dstatus = new DataRecordStatus();
			String requestExam = icb.getRequestExam();
			String acessType = icb.getProtocol();
			String strategycode = icb.getStrategyCode();
			String interfaceURL = icb.getUrl();
			String interfaceType = icb.getType();
			String citycode = icb.getCityCode();
			String name = icb.getName();
			String monitorGroup = icb.getMonitorGroup();
			ResponseDataBean responseData = null;
			String xml = null;
			Long delay = null;
			Date current = null;
			try {
				current = Calendar.getInstance().getTime();
				responseData = AccessWebserviceMethod.sendXmlToWebservice(interfaceURL, requestExam, acessType);
				xml = responseData.getResponseXMLData();
				delay = responseData.getDelayTime();
				logger.info("++++++++++++++响应的文件：" + xml);
			} catch (IOException e) {
				logger.error("解析报文失败！");
			}
			if (null != xml) {
				List<CheckEntity> checklists = checkmanage.findByCode(strategycode);
				for (CheckEntity ce : checklists) {
					String node = ce.getCheckCode();
					String checkrule = ce.getCheckRule();
					String valueRule = ce.getRuleValue();
					String value = null;
					if (xml.trim().startsWith("<")) {
						value = ParseXML.saxXML(node, xml);
					} else if (xml.trim().startsWith("{")) {
						value = ParseJSON.saxJSON(node, xml);
					}
					int resCorrect = 0;
					if (CheckEntity.TYPE_ALL.equals(checkrule)) {
						if (valueRule.equals(value)) {
							resCorrect = 1;
						}
					}
					if (CheckEntity.TYPE_REG.equals(checkrule)) {
						Pattern p = Pattern.compile(checkrule);
						if (p.matcher(value).find()) {
							resCorrect = 1;
						}
					}
					dh.setCityCode(citycode);
					dh.setInterfaceCode(interfaceCode);
					dh.setInterfaceType(interfaceType);
					dh.setRespCorrect(resCorrect);
					dh.setSentTime(current);
					dh.setDelay(delay);
					dh.setInterfaceName(name);
					dhlist.add(dh);

					dstatus.setCityCode(citycode);
					dstatus.setDelay(delay);
					dstatus.setInterfaceType(interfaceType);
					dstatus.setRespCorrect(resCorrect);
					dstatus.setInterfaceName(name);
					dstatus.setMonitorGroup(monitorGroup);
					dstatus.setInterfaceCode(interfaceCode);
					statuslist.add(dstatus);
					return true;
				}
			} else {
				dh.setCityCode(citycode);
				dh.setInterfaceCode(interfaceCode);
				dh.setInterfaceType(interfaceType);
				dh.setRespCorrect(0);
				dh.setSentTime(current);
				dh.setDelay(-1);
				dh.setInterfaceName(name);
				dhlist.add(dh);

				dstatus.setCityCode(citycode);
				dstatus.setDelay(-1);
				dstatus.setInterfaceType(interfaceType);
				dstatus.setRespCorrect(0);
				dstatus.setInterfaceName(name);
				dstatus.setMonitorGroup(monitorGroup);
				dstatus.setInterfaceCode(interfaceCode);
				statuslist.add(dstatus);
				return true;
			}

		}
		return false;
	}
	
	public synchronized void insertHistoryAndStatus(List<DataRecordHistory> dhlist,List<DataRecordStatus> statuslist) {
		 history.insertHistoty(dhlist);
		 monnitor_status.updateAndSave(statuslist);
	}
}
