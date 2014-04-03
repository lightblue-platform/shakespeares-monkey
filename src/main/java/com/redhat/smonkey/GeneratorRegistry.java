/*
 Copyright 2013 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.smonkey;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class GeneratorRegistry {
    private final Map<String,Generator> generators=new HashMap<>();

    private static GeneratorRegistry defaultInstance;

    public static GeneratorRegistry getDefaultInstance() {
        if(defaultInstance==null) {
            defaultInstance=new GeneratorRegistry();
            defaultInstance.register(new RndString());
            defaultInstance.register(new Choose());
            defaultInstance.register(new PropertyFile());
            defaultInstance.register(new RndDate());
            defaultInstance.register(new RndInt());
            defaultInstance.register(new RndLong());
            defaultInstance.register(new RndArr());
            defaultInstance.register(new Optional());
       }
        return defaultInstance;
    }

    public void register(Generator g) {
        generators.put(g.getName(),g);
    }

    public Generator get(String name)  {
        return generators.get(name);
    }

    public Set<String> getNames() {
        return generators.keySet();
    }
}
