package com.matletik.juliacaller;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;

public class TestBasics {

    static JuliaCaller caller;

    @BeforeAll
    public static void init() throws IOException {
        System.out.println("* Initializing tests");
        caller = new JuliaCaller("/usr/local/bin/julia", 8000);
        caller.startServer();
        caller.Connect();
    }

    @Test
    public  void AssignmentTest() throws IOException {
        caller.Execute("a = 10");
        String s = caller.GetAsJSON("a");
        assertEquals("{\"a\":10}", s);
    }

    @Test
    public void getArrayAsStringTest() throws IOException {
        caller.Execute("a = [1,2,3,4]");
        String s = caller.GetAsJSON("a");
        assertEquals("{\"a\":[1,2,3,4]}", s);
    }

    @Test
    public void getDictAsStringTest() throws IOException {
        caller.Execute("a = [1,2,3,4]");
        caller.Execute("b = 6.5");
        caller.Execute("d = Dict(\"first\" => b, \"second\" => a)");
        String s = caller.GetAsJSON("d");
        assertEquals("{\"d\":{\"second\":[1,2,3,4],\"first\":6.5}}", s);
    }

    @Test
    public void requiringPackageTest() throws IOException {
        caller.Execute("using Statistics");
        caller.Execute("d = [1,2,3]");
        caller.Execute("ave = mean(d)");
        String s = caller.GetAsJSON("ave");
        assertEquals("{\"ave\":2.0}", s);
    }
}
