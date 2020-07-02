package com.matletik.juliacaller;

import java.util.List;

public class JuliaObject {

    final private String code;

    private JuliaObject(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static JuliaObject createDoubleVariable(String name, double value) {
        return new JuliaObject(name + " = " + String.valueOf(value));
    }

    public static JuliaObject createFloatVariable(String name, float value) {
        return new JuliaObject(name + " = " + String.valueOf(value));
    }

    public static JuliaObject createIntVariable(String name, int value) {
        return new JuliaObject(name + " = " + String.valueOf(value));
    }

    public static JuliaObject createLongVariable(String name, long value) {
        return new JuliaObject(name + " = " + String.valueOf(value));
    }

    public static JuliaObject createBooleanVariable(String name, boolean value) {
        return new JuliaObject(name + " = " + String.valueOf(value));
    }

    public static <T extends Number> JuliaObject createArrayVariable(String name, final List<T> values) {
        StringBuilder s = new StringBuilder();
        s.append(name).append(" = [");
        for (int i = 0; i < values.size(); i++) {
            s.append(String.valueOf(values.get(i)));
            if (i < values.size() - 1) {
                s.append(", ");
            }
        }
        s.append("]");
        return new JuliaObject(s.toString());
    }
}
