package com.matletik.juliacaller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.script.Script;

import javax.script.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestScriptEngine {

    public static ScriptEngineManager manager;
    public static ScriptEngine engine;

    @BeforeAll
    public static void init(){
        Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin/julia");
        Constants.setProperties(Constants.JULIA_PORT, "8001");

         manager = new ScriptEngineManager();
         engine = manager.getEngineByName("Julia");
    }

    @Test
    public void engineCreatedTest(){
        assertTrue(engine != null);
    }

    @Test
    public void getAsJSONObject() throws IOException, ScriptException {
        engine.eval("a = 3");
        assertEquals(3, engine.get("a"));
    }

    @Test
    public void getAsJSONArray() throws ScriptException {
        engine.eval("a = [1,2,3]");
        Object result = engine.get("a");

        assertTrue(result instanceof JSONArray);

        JSONArray arr = (JSONArray) result;
        assertEquals(1, arr.getInt(0));
        assertEquals(2, arr.getInt(1));
        assertEquals(3, arr.getInt(2));
    }

    @Test
    public void invokeFunctionTest() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable)engine;
        engine.eval("using Statistics");
        engine.eval("a = [1.0, 2.0, 3.0]");
        Object result = invocable.invokeFunction("mean", "a");
        assertEquals(2.0, result);
    }

    @Test
    public void invokeFunction2Test() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable)engine;
        engine.eval("using Statistics");
        engine.eval("a = [1.0, 2.0, 3.0, 4.0, 5.0]");
        engine.eval("f(x) = sum(x)/length(x)");
        Object result = invocable.invokeFunction("f", "a");
        assertEquals(3.0, result);
    }


}
