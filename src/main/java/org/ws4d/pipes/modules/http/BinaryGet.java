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

import java.util.logging.Level;

import org.apache.commons.httpclient.methods.GetMethod;
import org.ws4d.pipes.module.annotation.InPort;
import org.ws4d.pipes.module.annotation.Module;
import org.ws4d.pipes.module.annotation.OutPort;

@Module(//
label = "Fetch binary data (HTTP GET)", //
icon = "internet.png", //
inPorts = { //
		@InPort(name = BaseModule.PORT_TRIGGER, label = "Trigger", isOptional = true), //
		@InPort(name = BaseModule.PORT_URL, label = "Url") }, //
outPorts = {//
		@OutPort(name = BaseModule.PORT_RESPONSE, label = "Response"), //
		@OutPort(name = BaseModule.PORT_STATUSCODE, label = "Status Code"), //
		@OutPort(name = BaseModule.PORT_STATUSLINE, label = "Status Line")} //
)
public class BinaryGet extends BaseModule {

	public void doWork() {

		// execute base module doWork()
		super.doWork();
		
		// check if we can terminate
		if (canClose()) {
			closeAllPorts();
			return;
		}
		
		if (getUrl() != null) {
			final GetMethod method = new GetMethod(getUrl());

			try {
				getClient().executeMethod(method);
				
				setOutData(PORT_RESPONSE, method.getResponseBody());
				setOutData(PORT_STATUSCODE, method.getStatusCode());
				setOutData(PORT_STATUSLINE, method.getStatusLine());

			} catch (Exception e) {
				getLogger().log(Level.SEVERE,
						"Can't execute http get request: ", e);
			} finally {
				method.releaseConnection();
			}
		}
	}
}
