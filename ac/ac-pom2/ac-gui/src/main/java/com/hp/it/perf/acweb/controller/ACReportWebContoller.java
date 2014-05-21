package com.hp.it.perf.acweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.it.perf.acweb.ReportForm;
import com.hp.it.perf.acweb.service.PropertyLoadService;
import com.hp.it.perf.acweb.service.WebTemplateService;
import com.hp.it.perf.acweb.util.Constant;

@Controller
@RequestMapping("/report")
public class ACReportWebContoller {

	private int linkout_value_1 = 1;
	private int linkout_value_2 = 2;
	private int linkout_value_all = -1;
	
	private String linkout_parameter_searchstr = "name=";
	private String linkout_parameter_category = "category=";
	private String linkout_parameter_type = "type=";
	
	private String linkout_parameter_part = "part=";
	
	private String service_parameter_starttime = "starttime=";
	private String service_parameter_endtime = "endtime=";
	private String service_parameter_estimated = "estimateNinety=";
	private char paramter_char = '?';
	private char paramter_link_char = '&';

	private String[] reportTableTemplates = new String[]{"consumerreport","consumerdetailreport","producerdetailreport","wsrpreport"};
	
	private PropertyLoadService propertyLoadService = new PropertyLoadService();
	private WebTemplateService webTemplateService = new WebTemplateService();

	protected String modelview = "acreportIndex";
	protected String modelname = "report";

	private void loadModelValue(Model model) {

		model.addAttribute(Constant.ACWEB_URL_LOGREPORT,
				propertyLoadService.loadProperty(Constant.ACWEB_URL_LOGREPORT));

		for(String table: reportTableTemplates){
			model.addAttribute(Constant.ACWEB_TABLE_PROFIX+table,
					webTemplateService.queryReportTemplate(table));
		}
		

	}

	private String initGet(ReportForm reportForm, Model model) {
		String serviceParamters = paramter_char + service_parameter_estimated + reportForm.getEstimateNinety()
				+ paramter_link_char + service_parameter_starttime + reportForm.getStarttime()
				+ paramter_link_char + service_parameter_endtime + reportForm.getEndtime();

		model.addAttribute(Constant.ACSERVICE_URL_CONSUMERREPORT,
				propertyLoadService.loadProperty(Constant.ACSERVICE_URL_CONSUMERREPORT) + serviceParamters);
		model.addAttribute(Constant.ACSERVICE_URL_WSRPREPORT,
				propertyLoadService.loadProperty(Constant.ACSERVICE_URL_WSRPREPORT) + serviceParamters);
		model.addAttribute(Constant.ACSERVICE_URL_CONSUMERDETAILREPORT,
				propertyLoadService.loadProperty(Constant.ACSERVICE_URL_CONSUMERDETAILREPORT) + serviceParamters);
		model.addAttribute(Constant.ACSERVICE_URL_PRODUCERDETAILREPORT,
				propertyLoadService.loadProperty(Constant.ACSERVICE_URL_PRODUCERDETAILREPORT) + serviceParamters + paramter_link_char + linkout_parameter_part);
		
		model.addAttribute(modelname, reportForm);

		String consumerParameters = paramter_char + linkout_parameter_category + linkout_value_1 +
				paramter_link_char + linkout_parameter_type + linkout_value_1 +
				paramter_link_char + service_parameter_starttime + reportForm.getStarttime() +
				paramter_link_char + service_parameter_endtime + reportForm.getEndtime() +
				paramter_link_char + linkout_parameter_searchstr;
				
		String producerParameters = paramter_char + linkout_parameter_category + linkout_value_2 +
				paramter_link_char + linkout_parameter_type + linkout_value_all +
				paramter_link_char + service_parameter_starttime + reportForm.getStarttime() +
				paramter_link_char + service_parameter_endtime + reportForm.getEndtime() +
				paramter_link_char + linkout_parameter_searchstr;
		
		model.addAttribute(Constant.ACWEB_URL_LOGLIST_CONSUMER, propertyLoadService.loadProperty(Constant.ACWEB_URL_LOGLIST)+consumerParameters);
		model.addAttribute(Constant.ACWEB_URL_LOGLIST_PRODUCER, propertyLoadService.loadProperty(Constant.ACWEB_URL_LOGLIST)+producerParameters);
			
		return modelview;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(@RequestParam(value = "starttime", required = false) String starttime,
			@RequestParam(value = "endtime", required = false) String endtime, @RequestParam(value= "type", required = false) String type, 
			@RequestParam(value= "estimateNinety", required = false) String estimateNinety, Model model) {
		loadModelValue(model);

		ReportForm reportForm = new ReportForm();
		reportForm.setStarttime(starttime);
		reportForm.setEndtime(endtime);
		if(type != null && !type.trim().isEmpty()){
			reportForm.setType(type);
		}
		if(estimateNinety == null || estimateNinety.equals("")){
			reportForm.setEstimateNinety(true);
		}else{
			boolean estimate = Boolean.valueOf(estimateNinety).booleanValue();
			reportForm.setEstimateNinety(estimate);
		}
		return initGet(reportForm, model);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("reportForm") ReportForm reportForm, Model model) {
		
		loadModelValue(model);
		reportForm.setType("0");
		
		return initGet(reportForm, model);
	}
}
