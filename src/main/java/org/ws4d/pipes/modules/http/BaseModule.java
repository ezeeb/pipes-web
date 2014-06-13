/**
 * Copyright (C) 2014 PipesBox UG (haftungsbeschränkt) (elmar.zeeb@pipesbox.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ws4d.pipes.modules.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.ws4d.pipes.module.DefaultModuleWorker;
import org.ws4d.pipes.module.PortValue;

public class BaseModule extends DefaultModuleWorker {
	
	public static final String PORT_TRIGGER = "trigger";
    public static final String PORT_URL = "url";
	public static final String PORT_RESPONSE = "response";
	public static final String PORT_STATUSCODE = "statusCode";
	public static final String PORT_STATUSLINE = "statusLine";
    
	final private HttpClient httpClient = new HttpClient();
	final private Map<String, PortValue> inPorts = new HashMap<String, PortValue>();
	private String url = null;
	
	protected HttpClient getClient() {
		return httpClient;
	}
	
	protected String getUrl() {
		return url;
	}
	
	@Override
	protected PortValue getWiredOrConfiguredValue(String portName) {
		PortValue result = super.getWiredOrConfiguredValue(portName);
		if (!result.isNull()) {
			inPorts.put(portName, result);
		} else {
			inPorts.remove(portName);
		}
		return result;
	}
	
	protected boolean canClose() {
		return inPorts.isEmpty();
	}
	
	@Override
	protected void doWork() {
		
		// get trigger value
		getWiredOrConfiguredValue(PORT_TRIGGER);
		
		// get url value
		final PortValue urlV = getWiredOrConfiguredValue(PORT_URL);

		if (urlV.isString()) {
			url = urlV.getString();
		}
	}

}
