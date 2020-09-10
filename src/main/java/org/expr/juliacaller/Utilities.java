package org.expr.juliacaller;

import java.io.File;

public class Utilities {

    public final static String TryFindingJuliaExecutable() {
        String[] paths = new String[]{
            "/usr/local/bin/julia",
            "/usr/bin/julia",
            "./julia-1.5.1/bin/julia"
        };
        for (String s : paths) {
            File f = new File(s);
            if (f.exists() && f.canExecute()) {
                return s;
            }
        }
        throw new JuliaRuntimeException("Julia executable is not found in predefined paths.");
    }
    
}
