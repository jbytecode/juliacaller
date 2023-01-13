package org.expr.juliacaller;

import java.io.IOException;
import java.util.List;
import static org.expr.juliacaller.TestBasics.caller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestFunctionDefinition {

    static JuliaCaller caller;

    @BeforeAll
    public static void init() throws IOException {
        System.out.println("* Initializing tests");
        // caller = new JuliaCaller("/usr/local/bin/julia", 8000);
        // caller = new JuliaCaller("/usr/bin/julia", 8000);
        caller = new JuliaCaller(Utilities.TryFindingJuliaExecutable(), 8001);
        caller.startServer();
        caller.Connect();
    }

    @AfterAll
    public static void finish() throws IOException {
        Thread th = new Thread(new Runnable() {
            public void run() {
                System.out.println("* Shuting down the server in 1 second(s)");
                for (int i = 0; i < 1; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.print(".");
                    } catch (InterruptedException ie) {

                    }
                }
            };
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {

        }
        caller.ShutdownServer();
    }

    @Test
    public void defineFunctionAndCallTest() throws IOException {
        String code = """
                function mysum(x, y)
                  total = x + y
                  return total
                end
                """;
        caller.ExecuteDefineFunction(code);
        caller.Execute("jresult = mysum(3, 5)");

        int result = caller.getInt("jresult");
        assertEquals(8, result);
    }

    @Test
    public void defineFunctionAndCallTestMultipleDispatch() throws IOException {
        String code = """
                function mysum(x::Int64, y::Int64)::Int64
                  total = x + y
                  return total
                end

                function mysum(x::Float64, y::Float64)::Float64
                  total = x + y
                  return total
                end
                """;

        caller.ExecuteDefineFunction(code);

        caller.Execute("jresult = mysum(3, 5)");
        int result = caller.getInt("jresult");
        assertEquals(8, result);

        caller.Execute("jresult = mysum(3.1, 5.1)");
        double resultdbl = caller.getDouble("jresult");
        assertEquals(8.2, resultdbl);
    }

    @Test
    public void defineFunctionThatReturnsDict() throws IOException {
        String code = """
                function getDictionary()
                    return Dict(
                        "a" => 1,
                        "b" => 2,
                        "c" => 3.14,
                        "d" => true
                    )
                end
                """;
        caller.ExecuteDefineFunction(code);

        caller.Execute("jresult = getDictionary()");

        JSONObject jresult = caller.GetAsJSONObject("jresult");
        assertEquals(1, jresult.getJSONObject("jresult").getInt("a"));
        assertEquals(2, jresult.getJSONObject("jresult").getInt("b"));
        assertEquals(3.14, jresult.getJSONObject("jresult").getDouble("c"));
        assertEquals(true, jresult.getJSONObject("jresult").getBoolean("d"));
    }
}
