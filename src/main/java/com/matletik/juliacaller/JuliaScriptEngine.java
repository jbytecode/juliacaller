package com.matletik.juliacaller;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class JuliaScriptEngine implements ScriptEngine, Invocable {

    ScriptContext context;
    JuliaCaller caller;

    public JuliaScriptEngine(){
        String juliaExecutable = Constants.properties.getProperty(Constants.JULIA_PATH);
        String juliaPort = Constants.properties.getProperty(Constants.JULIA_PORT);
        int port = Integer.parseInt(juliaPort);
        caller = new JuliaCaller(juliaExecutable, port);
        caller.setMaximumTriesToConnect(20);
        try {
            caller.startServer();
            caller.Connect();
        }catch (Exception e) {
            throw new JuliaRuntimeException(e.toString());
        }
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        String result = null;
        String randomVariableName = this.randomNumberString();
        StringBuilder s = new StringBuilder();
        s.append(thiz);
        s.append(".");
        s.append(name);
        s.append("(");
        for (int i = 0; i < args.length; i++){
            s.append(args[i]);
            if(i < args.length - 1){
                s.append(", ");
            }
        }
        s.append(")");
        try {
            caller.Execute(randomVariableName + " = " + s.toString());
        }catch (Exception e){
            throw new JuliaRuntimeException(e.toString());
        }
        try{
            result = caller.GetAsJSONString(randomVariableName);
        }catch (Exception e){
            throw new JuliaRuntimeException(e.toString());
        }
        return result;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        Object result = null;
        String randomVariableName = this.randomNumberString();
        StringBuilder s = new StringBuilder();
        s.append(name);
        s.append("(");
        for (int i = 0; i < args.length; i++){
            s.append(args[i]);
            if(i < args.length - 1){
                s.append(", ");
            }
        }
        s.append(")");
        try {
            caller.Execute(randomVariableName + " = " + s.toString());
        }catch (Exception e){
            throw new JuliaRuntimeException(e.toString());
        }
        try{
            result = caller.GetAsJSONObject(randomVariableName).get(randomVariableName);
        }catch (Exception e){
            try{
                result = caller.GetAsJSONArray(randomVariableName);
            }catch (Exception e2){
                throw new JuliaRuntimeException(e.toString());
            }
        }

        return result;
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return null;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        try {
            caller.Execute(script);
        }catch (Exception e){
            throw new JuliaRuntimeException(e.toString());
        }
        return null;
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        BufferedReader r = new BufferedReader(reader);
        try {
            while (true) {
                String s = r.readLine();
                if (s == null) {
                    break;
                }
                caller.Execute(s);
            }
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public Object eval(String script) throws ScriptException {
        try {
            caller.Execute(script);
        } catch (IOException e) {
            throw  new RuntimeException(e.toString());
        }
        return  null;
    }

    @Override
    public Object eval(Reader reader) throws ScriptException {
        BufferedReader r = new BufferedReader(reader);
        try {
            while (true) {
                String s = r.readLine();
                if (s == null) {
                    break;
                }
                caller.Execute(s);
            }
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public Object eval(String script, Bindings n) throws ScriptException {
        try {
            caller.Execute(script);
        } catch (IOException e) {

        }
        return  null;
    }

    @Override
    public Object eval(Reader reader, Bindings n) throws ScriptException {
        BufferedReader r = new BufferedReader(reader);
        try {
            while (true) {
                String s = r.readLine();
                if (s == null) {
                    break;
                }
                caller.Execute(s);
            }
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public void put(String key, Object value) {
        try {
            caller.Execute("execute " + key + " = " + value.toString());
        } catch (IOException e) {

        }
    }

    @Override
    public Object get(String key) {
        try {
            JSONObject val = caller.GetAsJSONObject(key);
            return val.get(key);
        } catch (IOException e) {
            try{
                JSONArray val = caller.GetAsJSONArray(key);
                return val;
            }catch (IOException e2){
                return null;
            }
        }
    }

    @Override
    public Bindings getBindings(int scope) {
        return new SimpleBindings();
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {

    }

    @Override
    public Bindings createBindings() {
        return null;
    }

    @Override
    public ScriptContext getContext() {
        return this.context;
    }

    @Override
    public void setContext(ScriptContext context) {
        this.context = context;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return this.getFactory();
    }

    private String randomNumberString(){
        return "__JULIACALLER__" + String.valueOf(Math.random() * 10000000).replaceAll("\\.","");
    }
}
