package com.redhat.smonkey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public interface Generator {
    String getName();
    JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon);
    String describe();
}
