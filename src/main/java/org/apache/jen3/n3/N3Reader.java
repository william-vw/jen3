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

package org.apache.jen3.n3;

import java.io.Reader;

import org.apache.jen3.n3.io.JenaReaderBase;
import org.apache.jen3.rdf.model.Model;

public class N3Reader extends JenaReaderBase {

	public N3Reader() {
	}

	@Override
	protected void readWorker(Model model, Reader reader, String base) {
		N3Model n3Model = (N3Model) model;
		
		ParserN3 p = new ParserN3();
		p.parse(n3Model, base, reader);
	}
}
