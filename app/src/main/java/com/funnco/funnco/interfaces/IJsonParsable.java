package com.funnco.funnco.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJsonParsable {
	public void readFromJson(JSONObject object) throws JSONException;

}
