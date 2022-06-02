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

package jena.test;

import java.util.HashSet;
import java.util.Set;

public abstract class GeneralTest {

	public static void main(String[] args) {
//		List<Integer> l1 = Arrays.asList(1, 2, 3);
//		List<Integer> l2 = Arrays.asList(3, 1, 2);
//		
//		System.out.println(l1.hashCode());
//		System.out.println(l2.hashCode());
		
		Set<String> s1 = new HashSet<>();
		s1.add("1"); s1.add("2"); s1.add("3");
		
		Set<String> s2 = new HashSet<>();
		s2.add("2"); s2.add("3"); s2.add("1");
		
		System.out.println(s1.hashCode());
		System.out.println(s2.hashCode());
		System.out.println();
		
		System.out.println(hashCode(s1));
		System.out.println(hashCode(s2));
	}
	
	private static int hashCode(Set<String> set) {
		int i = 0;
		for (String s : set)
			i += s.hashCode();
		
		return i;
	}
}
