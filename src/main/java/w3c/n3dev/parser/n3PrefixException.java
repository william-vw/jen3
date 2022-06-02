/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package w3c.n3dev.parser;

public class n3PrefixException extends Exception {

	private static final long serialVersionUID = 1L;

	public static enum PrefixErrors {

		/* REDEF_PREFIX("redefining existing prefix"), */ UNKNOWN_PREFIX("referencing unknown prefix");

		private String msg;

		private PrefixErrors(String msg) {
			this.msg = msg;
		}

		public String getMsg() {
			return msg;
		}
	}

	private PrefixErrors type;

	public n3PrefixException(PrefixErrors type) {
		super(type.getMsg());

		this.type = type;
	}

	public n3PrefixException(PrefixErrors type, String e) {
		super(e);

		this.type = type;
	}

	public PrefixErrors getType() {
		return type;
	}
}
