package com.matletik.juliacaller;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

public class JuliaScriptContext implements ScriptContext {

    HashMap<String, Object> Attributes;
    Bindings bindings;

    public JuliaScriptContext(){
        this.Attributes = new HashMap<>();
        this.bindings = new SimpleBindings();
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        this.bindings = bindings;
    }

    @Override
    public Bindings getBindings(int scope) {
        return this.bindings;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        this.Attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name, int scope) {
        return this.Attributes.get(name);
    }

    @Override
    public Object removeAttribute(String name, int scope) {
        Object value = this.Attributes.get(name);
        this.Attributes.remove(name);
        return value;
    }

    @Override
    public Object getAttribute(String name) {
        return this.Attributes.get(name);
    }

    @Override
    public int getAttributesScope(String name) {
        return 0;
    }

    @Override
    public Writer getWriter() {
        return null;
    }

    @Override
    public Writer getErrorWriter() {
        return null;
    }

    @Override
    public void setWriter(Writer writer) {

    }

    @Override
    public void setErrorWriter(Writer writer) {

    }

    @Override
    public Reader getReader() {
        return null;
    }

    @Override
    public void setReader(Reader reader) {

    }

    @Override
    public List<Integer> getScopes() {
        return null;
    }
}
