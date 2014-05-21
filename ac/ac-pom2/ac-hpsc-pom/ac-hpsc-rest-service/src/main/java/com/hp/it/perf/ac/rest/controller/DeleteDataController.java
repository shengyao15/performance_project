package com.hp.it.perf.ac.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.it.perf.ac.rest.util.QueryConstants;
import com.hp.it.perf.ac.rest.util.Utils;
import com.hp.it.perf.ac.service.spfchain.SpfChainService;

@Controller
@RequestMapping({ "/admin" })
public class DeleteDataController {

	private static final Logger log = LoggerFactory
			.getLogger(HomeServiceController.class);

	@Autowired
	@Qualifier("spfchainService")
	private SpfChainService spfchainService;

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public void deleteChainData(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime,
			@RequestParam(value = "deleteAll", required = false) Boolean deleteAll) {

		String logPrefix = this.getClass().getName()
				+ ".deleteChainData(...): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}.", startTime, endTime);
		log.debug(logPrefix + "deleteAll: {} ", deleteAll);

		if (deleteAll == null) {
			deleteAll = false;
		}

		/*spfchainService.deleteChainData(
				Utils.getQueryCondition(startTime, endTime), deleteAll);*/
	}
}
