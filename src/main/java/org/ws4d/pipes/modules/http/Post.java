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

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ws4d.pipes.module.PortValue;
import org.ws4d.pipes.module.annotation.InPort;
import org.ws4d.pipes.module.annotation.Module;
import org.ws4d.pipes.module.annotation.OutPort;

@Module(//
label = "Post data (HTTP POST)", //
icon = "internet.png", //
inPorts = { //
		@InPort(name = BaseModule.PORT_TRIGGER, label = "Trigger", isOptional = true), //
		@InPort(name = BaseModule.PORT_URL, label = "Url"), //
		@InPort(name = Post.PORT_BODY, label = "Request Body"), //
		@InPort(name = Post.PORT_CONTENTTYPE, label = "Content Type", isOptional = true) }, //
outPorts = {//
		@OutPort(name = BaseModule.PORT_RESPONSE, label = "Response"), //
		@OutPort(name = BaseModule.PORT_STATUSCODE, label = "Status Code"), //
		@OutPort(name = BaseModule.PORT_STATUSLINE, label = "Status Line") } //
)
public class Post extends BaseModule {

	public static final String PORT_BODY = "body";
	public static final String PORT_CONTENTTYPE = "contentType";

	private byte[] body = null;
	private String contentType = "text/xml";

	public void doWork() {

		// execute base module doWork()
		super.doWork();

		final PortValue bodyV = getWiredOrConfiguredValue(PORT_BODY);
		final PortValue contentTypeV = getWiredOrConfiguredValue(PORT_CONTENTTYPE);

		// check if we can terminate
		if (canClose()) {
			closeAllPorts();
			return;
		}

		// if body is string
		if (bodyV.isString()) {
			try {
				body = bodyV.getString().getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				getLogger().log(Level.SEVERE, "Can't convert string", e);
				body = null;
			}
		}

		// if body is byte array
		if (bodyV.isByteArray()) {
			body = bodyV.getByteArray();
		}
		
		// get content type
		if (contentTypeV.isString()) {
			contentType = contentTypeV.getString();
		}

		if ((getUrl() != null) && (body != null)) {
			final PostMethod method = new PostMethod(getUrl());
			try {
				method.setRequestEntity(new ByteArrayRequestEntity(body,
						contentType));
			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Can't create request entity", e);
				return;
			}

			try {
				getClient().executeMethod(method);
				
				setOutData(PORT_RESPONSE, method.getResponseBodyAsString());
				setOutData(PORT_STATUSCODE, method.getStatusCode());
				setOutData(PORT_STATUSLINE, method.getStatusLine());
				
			} catch (Exception e) {
				getLogger().log(Level.SEVERE,
						"Can't execute http post request", e);
			}
			method.releaseConnection();
		}
	}
}
