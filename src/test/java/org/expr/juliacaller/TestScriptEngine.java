package org.expr.juliacaller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.script.*;
import java.io.IOException;
import static org.expr.juliacaller.TestBasics.caller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestScriptEngine {

    public static ScriptEngineManager manager;
    public static ScriptEngine engine;

    @BeforeAll
    public static void init() {
        // Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin/julia");
        // Constants.setProperties(Constants.JULIA_PATH, "/usr/bin/julia");
        Constants.setProperties(Constants.JULIA_PATH, Utilities.TryFindingJuliaExecutable());
        Constants.setProperties(Constants.JULIA_PORT, "8002");

        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("Julia");
    }

    @Test
    public void engineCreatedTest() {
        assertTrue(engine != null);
    }

    @Test
    public void getAsJSONObject() throws IOException, ScriptException {
        engine.eval("a = 3");
        assertEquals(3, engine.get("a"));
    }

    @Test
    public void getAsJSONArray() throws ScriptException {
        engine.eval("arr7 = [1,2,3]");
        Object result = engine.get("arr7");

        assertTrue(result instanceof JSONArray);

        JSONArray arr = (JSONArray) result;
        assertEquals(1, arr.getInt(0));
        assertEquals(2, arr.getInt(1));
        assertEquals(3, arr.getInt(2));
    }

    @Test
    public void invokeFunctionTest() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable) engine;
        engine.eval("using Statistics");
        engine.eval("arr5 = [1.0, 2.0, 3.0]");
        Object result = invocable.invokeFunction("mean", "arr5");
        assertEquals(2.0, result);
    }

    @Test
    public void invokeFunction2Test() throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable) engine;
        engine.eval("using Statistics");
        engine.eval("arr2 = [1.0, 2.0, 3.0, 4.0, 5.0]");
        engine.eval("f(x) = sum(x)/length(x)");
        Object result = invocable.invokeFunction("f", "arr2");
        assertEquals(3.0, result);

    }

    @Test
    public void getDict() throws ScriptException {
        engine.eval("d1 = Dict(\"a\" => [10,20,30], \"b\" => 3.14159265)");
        Object result = engine.get("d1");
        Object a = ((JSONObject) result).getJSONArray("a");
        Object b = ((JSONObject) result).getDouble("b");

        assertEquals(3.14159265, b);

        JSONArray arr = (JSONArray) a;
        assertEquals(10, arr.getInt(0));
        assertEquals(20, arr.getInt(1));
        assertEquals(30, arr.getInt(2));

    }

}
