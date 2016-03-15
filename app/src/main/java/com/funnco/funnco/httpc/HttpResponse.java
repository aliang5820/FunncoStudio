package com.funnco.funnco.httpc;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpResponse extends JSONObject {

	public static final int SUCCESS = 1;

	public static final int FAIL = 0;

	public static final String KEY_STATUS = "Status";

	public static final String KEY_ERRCODE = "ErrorCode";

	public static final String KEY_ERRINFO = "ErrorInfo";

	public HttpResponse(String raw) throws JSONException {
		super(raw);
	}

	/**
	 * Get status of the response
	 * 
	 * @return 1 for success, 0 for fail
	 */
	public int getStatus() {
		return optInt(KEY_STATUS, FAIL);
	}

	/**
	 * Get ErrCode of the response
	 * 
	 * @return -1 or error code
	 */
	public int getErrCode() {
		return optInt(KEY_ERRCODE, -1);
	}

	/**
	 * Get error info
	 * 
	 * @return null or error info
	 */
	public String getErrInfo() {
		return optString(KEY_ERRINFO, null);
	}

}
