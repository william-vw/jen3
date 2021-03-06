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

package org.apache.jen3.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList ;
import java.util.InvalidPropertiesFormatException;
import java.util.List ;
import java.util.Properties;

import org.apache.jen3.shared.JenaException;
import org.apache.jena.atlas.AtlasException;
import org.apache.jena.atlas.RuntimeIOException;
import org.apache.jena.atlas.lib.SystemUtils;
import org.apache.jena.atlas.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pluck data out of the ether - or failing that, read it from a properties file. Assumes
 * the properties file is in the "right place" through configuration of the build or
 * compile processes.
 */
public class Metadata {
    private static Logger log        = LoggerFactory.getLogger(Metadata.class);
    List<String>          resources  = new ArrayList<>();
    Properties            properties = new Properties();

    public Metadata() {}

    public Metadata(String resourceName) {
        this();
        addMetadata(resourceName);
    }

    public void addMetadata(String resourceName) {
        resources.add(resourceName);
        read(properties, resourceName);
    }

    // Protect all classloader choosing -- sometimes systems mess with even the system
    // class loader.
    private static void read(Properties properties, String resourceName) {
        // Armour-plate this - classloaders and using them can be blocked by some
        // environments.
        try {
            ClassLoader classLoader = null;

            try { classLoader = SystemUtils.chooseClassLoader(); } catch (AtlasException ex) {}

            if ( classLoader == null ) {
                try { classLoader = Metadata.class.getClassLoader(); } catch (AtlasException ex) {}
            }

            if ( classLoader == null ) {
                Log.error(Metadata.class, "No classloader");
                return;
            }

            InputStream in = classLoader.getResourceAsStream(resourceName);
            if ( in == null )
                return;

            try {
                properties.loadFromXML(in);
            } catch (InvalidPropertiesFormatException ex) {
                throw new JenaException("Invalid properties file", ex);
            } catch (IOException ex) {
                throw new RuntimeIOException("Metadata ==> IOException", ex);
            }
        } catch (Throwable ex) {
            Log.error(Metadata.class, "Unexpected Throwable", ex);
            return;
        }
    }

    public String get(String name) {
        return get(name, null);
    }

    public String get(String name, String defaultValue) {
        if ( properties == null )
            return defaultValue;
        return properties.getProperty(name, defaultValue);
    }
}
