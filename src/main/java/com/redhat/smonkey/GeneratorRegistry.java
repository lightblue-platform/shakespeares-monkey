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
