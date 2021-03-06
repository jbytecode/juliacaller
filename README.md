
[![Build Status](https://travis-ci.org/jbytecode/juliacaller.svg?branch=master&status=passed)](https://travis-ci.org/github/jbytecode/juliacaller)

# JuliaCaller

A library for calling Julia from Java.


# Initials
JuliaCaller creates a TCP server that listens on a special port in Julia side. This server executes 
Julia statements and expressions that sent from the Java side. The result is then handled in Java side as 
primitives, JSONObjects and JSONArrays.

## First things first!
Don't forget to install the **JSON** package in your Julia environment before running JuliaCaller. JuliaCaller first tries to install the **JSON** package when its first use, however, this may take time and the maximum number of tries of connection may be exceed.

```julia
]add JSON
```

or 

```julia
julia> using Pkg
julia> Pkg.add("JSON")
```


# javax.script interface
JuliaCaller implements javax.script interface, that is, it can be used as a scripting engine in Java.

# Examples

Here is the section of examples. For now, we have only tests in the source code. Please have a look at the test folder.

## Getting primitives
This example create a scripting environment for Julia. The statement 'a = 3' is sent to Julia and the result is handled from Java.
```java
Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin/julia");
Constants.setProperties(Constants.JULIA_PORT, "8001");

// Creating a scripting interface for Julia
manager = new ScriptEngineManager();
engine = manager.getEngineByName("Julia");

// Sending command 'a = 3' to Julia from Java
engine.eval("a = 3");

// Handling the result in Java
Object a = engine.get("a");
```
## Getting arrays
```java
Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin/julia");
Constants.setProperties(Constants.JULIA_PORT, "8001");

// Creating a scripting interface for Julia
manager = new ScriptEngineManager();
engine = manager.getEngineByName("Julia");

// Creating array in Julia
engine.eval("a = [1,2,3]");

// Handling result in Java
Object result = engine.get("a");

assertTrue(result instanceof JSONArray);

JSONArray arr = (JSONArray) result;
assertEquals(1, arr.getInt(0));
assertEquals(2, arr.getInt(1));
assertEquals(3, arr.getInt(2));
```

## Function calls
```java
Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin/julia");
Constants.setProperties(Constants.JULIA_PORT, "8001");

// Creating a scripting interface for Julia
manager = new ScriptEngineManager();
engine = manager.getEngineByName("Julia");

// Creating array in Julia
engine.eval("a = [1,2,3]");
engine.eval("f(x) = sum(x) / length(x)")
engine.eval("b = f(a)")

// Handling result in Java
Object result = engine.get("b");
```

## Invocable interface
```java
Constants.setProperties(Constants.JULIA_PATH, "/usr/local/bin/julia");
Constants.setProperties(Constants.JULIA_PORT, "8001");

// Creating a scripting interface for Julia
manager = new ScriptEngineManager();
engine = manager.getEngineByName("Julia");

Invocable invocable = (Invocable)engine;
engine.eval("using Statistics");
engine.eval("a = [1.0, 2.0, 3.0]");

// The result is average of 1.0, 2.0, and 3.0
// result = 2.0
Object result = invocable.invokeFunction("mean", "a");
```

# Using JuliaCaller without javax.script interface
```java
caller = new JuliaCaller("/usr/local/bin/julia", 8000);
caller.startServer();
caller.Connect();

caller.Execute("mypi = 3.14159265");
JSONObject obj = caller.GetAsJSONObject("mypi");
// Assertation is true
assertEquals(obj.getDouble("mypi"), 3.14159265);

// Creating object 'myarray' in Julia
caller.Execute("myarray = [1,2,3,4,5]");

// Getting the array back in Java
JSONArray obj = caller.GetAsJSONArray("myarray");

// Assertations are true
assertEquals(obj.length(), 5);
assertEquals(obj.getInt(0), 1);
assertEquals(obj.getInt(1), 2);
assertEquals(obj.getInt(2), 3);
assertEquals(obj.getInt(3), 4);
assertEquals(obj.getInt(4), 5);

caller.ShutdownServer();
```

# Easy passing of objects
```java
List<Double> values = List.of(1.0, 2.0, 10.0, -4.0);
caller.addJuliaObject(JuliaObject.createArrayVariable("a", values));
caller.Execute("using Statistics");
caller.Execute("ave = mean(a)");
double result = caller.getDouble("ave");
assertEquals(2.25, result);
```

Note that the library is in its early development stage. 

