package org.expr.juliacaller;

import java.io.File;

public class Utilities {

    public final static String TryFindingJuliaExecutable() {
        String readpath = Constants.properties.getProperty(Constants.JULIA_PATH);
        String[] paths = new String[] {readpath, "/usr/local/bin/julia", "/usr/bin/julia", "/bin/julia", "C:\\Program Files\\Julia\\bin\\julia.exe"};
        
        for (String path: paths) {
            File f = new File(path);
            if (f.exists() && f.canExecute()) {
                return path;
            }
        } 
        
        throw new JuliaRuntimeException("Julia executable is not found in predefined paths.");
    }
    
}
