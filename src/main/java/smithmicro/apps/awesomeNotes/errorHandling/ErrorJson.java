package smithmicro.apps.awesomeNotes.errorHandling;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PROTECTED)
@Component
public class ErrorJson {


	private Integer status;
	private String error;

	private String message;
	private String timeStamp;
	private String trace;


	public ErrorJson(){

	}

	public ErrorJson(int status, Map<String, Object> errorAttributes) {
		this.status = status;
		this.error = (String) errorAttributes.get("error");
		this.message = (String) errorAttributes.get("message");
		this.timeStamp = errorAttributes.get("timestamp").toString();
		this.trace = (String) errorAttributes.get("trace");
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getTrace() {
		return trace;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

}
