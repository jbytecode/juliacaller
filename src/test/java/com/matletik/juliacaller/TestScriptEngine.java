package com.matletik.juliacaller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestScriptEngine {

    @Test
    public void getAsJSONObject() throws IOException, ScriptException {
        Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin(julia");
        Constants.setProperties(Constants.JULIA_PORT, "8000");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("julia");

        engine.eval("a = 3");
        assertEquals(3, engine.get("a"));
    }

}
