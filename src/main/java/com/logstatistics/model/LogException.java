package com.logstatistics.model;

public class LogException extends Log {

	private String exceptionType;

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public LogException() {
	}

	public LogException(LogType logType, String message, String exceptionType) {
		super(logType, message);
		this.exceptionType = exceptionType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exceptionType == null) ? 0 : exceptionType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogException other = (LogException) obj;
		if (exceptionType == null) {
			if (other.exceptionType != null)
				return false;
		} else if (!exceptionType.equals(other.exceptionType))
			return false;
		return true;
	}

	
}
