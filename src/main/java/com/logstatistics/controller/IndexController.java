package com.logstatistics.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.logstatistics.model.Log;
import com.logstatistics.model.LogException;
import com.logstatistics.tools.CollectionsConvertion;
import com.logstatistics.tools.LogParser;
import com.logstatistics.tools.MapUtil;

@Controller
public class IndexController {

	@Autowired
	private LogParser logParser;

	@Autowired
	private CollectionsConvertion collecionConvertion;
	
	private List<Log> logList;
	private List<LogException> logExceptionList;
	private Map<Log,Integer> logMap;
	private Map<LogException,Integer> logExceptionMap;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PostMapping("/parseLogFile")
	public ModelAndView parseLogFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
			@RequestParam(value = "logType", defaultValue = "Errors") String logType) {

		if (!file.getOriginalFilename().contains(".txt")) {
			ModelAndView model = new ModelAndView("index");
			model.addObject("errorFileType", true);
			return model;
		}
		
		ModelAndView model = new ModelAndView("parsePage");
		try {
			switch (logType) {
			case "Errors":
				logList = logParser.parseLog(file.getInputStream(), logType);
				logMap = MapUtil.sortByValue(collecionConvertion.convertErrorsListToMap(logList));
				model.addObject("size", logList.size());
				model.addObject("logMap", logMap);
				model.addObject("logType", logType);
				break;
			case "Information":
				logList = logParser.parseLog(file.getInputStream(), logType);
				model.addObject("size", logList.size());
				model.addObject("logList", logList);
				model.addObject("logType", logType);
				break;
			case "Exceptions":
				logExceptionList = (List<LogException>) ((List<?>) logParser.parseLog(file.getInputStream(), logType));
				logExceptionMap = MapUtil.sortByValue(collecionConvertion.convertExceptionsListToMap(logExceptionList));
				model.addObject("size", logExceptionList.size());
				model.addObject("logExceptionMap", logExceptionMap);
				model.addObject("logType", logType);
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping("/logDetails")
	public ModelAndView logDetails(@RequestParam("totalNumber") Integer totalNumber, @RequestParam("logMessage") String logMessage) {
		ModelAndView model = new ModelAndView("logDetails");
	
		Map<String, Integer> chartMap = new HashMap<>();
		
		for(Log log : logList)
		{
			if(log.getMessage().contains(logMessage))
				if(chartMap.containsKey(log.getDate().toString("dd-MM-yyyy HH:mm")))
					chartMap.put(log.getDate().toString("dd-MM-yyyy HH:mm"), chartMap.get(log.getDate().toString("dd-MM-yyyy HH:mm")) + 1);
				else
					chartMap.put(log.getDate().toString("dd-MM-yyyy HH:mm"), 1);
		}
		
		model.addObject("logMessage", logMessage);
		model.addObject("chartMap", chartMap);
		model.addObject("totalNumber", totalNumber);
		return model;
	}
	
	@RequestMapping("/logExceptionDetails")
	public ModelAndView logExceptionDetails(@RequestParam("totalNumber") Integer totalNumber, @RequestParam("exceptionType") String exceptionType) {
		ModelAndView model = new ModelAndView("logExceptionDetails");
		
		List<LogException> list = new ArrayList<>(logExceptionList);
		Iterator<LogException> iter = list.iterator();
		while (iter.hasNext()) {
		   LogException tempLog = iter.next();
		   if(!tempLog.getExceptionType().equals(exceptionType))
			   iter.remove();
		}
		
		model.addObject("logExceptionList", list);
		model.addObject("totalNumber", totalNumber);
		return model;
	}
}