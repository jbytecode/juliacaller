package com.matletik.juliacaller;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

public class TestBasics {

    static JuliaCaller caller;

    @BeforeAll
    public static void init() throws IOException {
        System.out.println("* Initializing tests");
        //caller = new JuliaCaller("/usr/local/bin/julia", 8000);
        caller = new JuliaCaller("/usr/bin/julia", 8000);
        caller.startServer();
        caller.Connect();
    }

    @AfterAll
    public static void finish() throws IOException {
        System.out.println("* Shuting down the server");
        caller.ShutdownServer();
    }

    @Test
    public void AssignmentTest() throws IOException {
        caller.Execute("a = 10");
        String s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":10}", s);
    }

    @Test
    public void getArrayAsStringTest() throws IOException {
        caller.Execute("a = [1,2,3,4]");
        String s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":[1,2,3,4]}", s);
    }

    @Test
    public void getDictAsStringTest() throws IOException {
        caller.Execute("a = [1,2,3,4]");
        caller.Execute("b = 6.5");
        caller.Execute("d = Dict(\"first\" => b, \"second\" => a)");
        String s = caller.GetAsJSONString("d");
        assertEquals("{\"d\":{\"second\":[1,2,3,4],\"first\":6.5}}", s);
    }

    @Test
    public void requiringPackageTest() throws IOException {
        caller.Execute("using Statistics");
        caller.Execute("d = [1,2,3]");
        caller.Execute("ave = mean(d)");
        String s = caller.GetAsJSONString("ave");
        assertEquals("{\"ave\":2.0}", s);
    }

    @Test
    public void getAsJSONObject() throws IOException {
        caller.Execute("mypi = 3.14159265");
        JSONObject obj = caller.GetAsJSONObject("mypi");
        assertEquals(3.14159265, obj.getDouble("mypi"));
    }

    @Test
    public void getAsJSONArray() throws IOException {
        caller.Execute("myarray = [1,2,3,4,5]");
        JSONArray obj = caller.GetAsJSONArray("myarray");
        assertEquals(obj.length(), 5);
        assertEquals(obj.getInt(0), 1);
        assertEquals(obj.getInt(1), 2);
        assertEquals(obj.getInt(2), 3);
        assertEquals(obj.getInt(3), 4);
        assertEquals(obj.getInt(4), 5);
    }

    @Test
    public void createRealVariableTest() throws IOException {
        caller.addJuliaObject(JuliaObject.createDoubleVariable("a", 3.456));
        String s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":3.456}", s);

        caller.addJuliaObject(JuliaObject.createFloatVariable("a", 3.4567f));
        s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":3.4567}", s);
    }

    @Test
    public void createIntegralVariableTest() throws IOException {
        caller.addJuliaObject(JuliaObject.createIntVariable("a", 3));
        String s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":3}", s);

        caller.addJuliaObject(JuliaObject.createLongVariable("a", 34567));
        s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":34567}", s);
    }

    @Test
    public void createBooleanVariableTest() throws IOException {
        caller.addJuliaObject(JuliaObject.createBooleanVariable("a", true));
        String s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":true}", s);

        caller.addJuliaObject(JuliaObject.createBooleanVariable("a", false));
        s = caller.GetAsJSONString("a");
        assertEquals("{\"a\":false}", s);
    }

    @Test
    public void createArrayVariableTest() throws IOException {
        List<Double> values = List.of(1.0, 2.0, 10.0, -4.0);
        caller.addJuliaObject(JuliaObject.createArrayVariable("a", values));
        JSONArray obj = caller.GetAsJSONArray("a");
        assertEquals(obj.length(), 4);
        assertEquals(1.0, obj.getDouble(0));
        assertEquals(2.0, obj.getDouble(1));
        assertEquals(10.0, obj.getDouble(2));
        assertEquals(-4.0, obj.getDouble(3));
    }

}
