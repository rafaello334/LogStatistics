package com.logstatistics.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import com.logstatistics.model.Log;
import com.logstatistics.model.LogException;
import com.logstatistics.model.LogType;

@Component
public class LogParser {

	private List<Log> logCollection;
	private DateTimeFormatter formatter;
	private StringBuilder currentLog;

	public List<Log> parseLog(InputStream inputStream, String logType) {
		String currentLine;
		logCollection = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			switch (logType) {
			case "Errors":
				formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
				while ((currentLine = br.readLine()) != null) {
					if (currentLine.contains("(self-tuning)")) {
						logCollection.add(new Log(formatter.parseDateTime(currentLine.substring(0, 19)), LogType.Error,
								currentLine.substring(currentLine.indexOf("(self-tuning)']") + 16 , currentLine.indexOf(")] -") + 2)));
					}
				}
				break;

			case "Information":
				currentLog = new StringBuilder();
				while ((currentLine = br.readLine()) != null) {
					if (currentLine.contains("<Log Information - not error>")) {
						currentLog.append(currentLine + "\n");
						while ((currentLine = br.readLine()) != null && !currentLine.equals("")) {
							currentLog.append(currentLine);
						}
						logCollection.add(new Log(LogType.Info, currentLog.toString()));
						currentLog.setLength(0);
					}
				}
				break;

			case "Exceptions":
				String exceptionType;
				currentLog = new StringBuilder();
				while ((currentLine = br.readLine()) != null) {
					if ((currentLine.contains("java.") || currentLine.contains("javax.")
							|| (currentLine.contains("com.scc") && currentLine.contains("Exception")))
							&& !currentLine.contains("ERROR")) {
						currentLog.append(currentLine);
						exceptionType = currentLine;
						while ((currentLine = br.readLine()) != null && !currentLine.contains("Thread.run")) {
							currentLog.append(currentLine + "\n");
						}
						currentLog.append(currentLine + "\n");
						logCollection.add(new LogException(LogType.Exception, currentLog.toString(), exceptionType));
						currentLog.setLength(0);
					}
				}
				break;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logCollection;
	}
}
