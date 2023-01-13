package org.expr.juliacaller;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.ArrayList;
import java.util.List;

public class JuliaScriptEngineFactory implements ScriptEngineFactory {
    @Override
    public String getEngineName() {
        return ("Julia");
    }

    @Override
    public String getEngineVersion() {
        return ("0.1");
    }

    @Override
    public List<String> getExtensions() {
        ArrayList<String> ext = new ArrayList<>();
        ext.add("jl");
        ext.add("julia");
        return (ext);
    }

    @Override
    public List<String> getMimeTypes() {
        ArrayList<String> mimes = new ArrayList<>();
        mimes.add("text/julia");
        return (mimes);
    }

    @Override
    public List<String> getNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Julia");
        names.add("JuliaCaller");
        return (names);
    }

    @Override
    public String getLanguageName() {
        return ("Julia");
    }

    @Override
    public String getLanguageVersion() {
        return ("1.4");
    }

    @Override
    public Object getParameter(String key) {
        if (null != key) {
            switch (key) {
                case ScriptEngine.ENGINE:
                    return this.getEngineName();
                case ScriptEngine.ENGINE_VERSION:
                    return this.getEngineVersion();
                case ScriptEngine.NAME:
                    return this.getNames();
                case ScriptEngine.LANGUAGE:
                    return this.getLanguageName();
                case ScriptEngine.LANGUAGE_VERSION:
                    return this.getLanguageVersion();
                default:
                    return null;
            }
        }
        return ("!");
    }

    @Override
    public String getMethodCallSyntax(String string, String string1, String... strings) {
        StringBuilder buf = new StringBuilder();
        buf.append(string);
        buf.append("(");
        buf.append(string1);
        if (strings.length == 0) {
            buf.append(")");
            return (buf.toString());
        }
        for (int i = 0; i < strings.length; i++) {
            buf.append(strings[i]);
            if (i < strings.length) {
                buf.append(",");
            }
        }
        buf.append(")");
        return (buf.toString());
    }

    @Override
    public String getOutputStatement(String string) {
        return ("print(" + string + ")");
    }

    @Override
    public String getProgram(String... strings) {
        return ("Program");
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return (new JuliaScriptEngine());
    }

}
