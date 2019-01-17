package com.logstatistics.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.logstatistics.model.Log;
import com.logstatistics.model.LogException;

@Component
public class CollectionsConvertion {

	public Map<Log, Integer> convertErrorsListToMap(List<Log> logList) {
		Set<Log> logSet = new HashSet<>();
		Map<Log, Integer> logMap = new HashMap<>();
		for (Log log : logList) {
			logSet.add(log);
		}

		for (Log key : logSet) {
			logMap.put(key, Collections.frequency(logList, key));
		}
		return logMap;
	}
	
	public Map<LogException, Integer> convertExceptionsListToMap(List<LogException> logList) {
		Set<LogException> logSet = new HashSet<>();
		Map<LogException, Integer> logMap = new HashMap<>();
		for (LogException log : logList) {
			logSet.add(log);
		}

		for (LogException key : logSet) {
			logMap.put(key, Collections.frequency(logList, key));
		}
		return logMap;
	}

	
}
