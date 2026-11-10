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

        String executableName = File.separatorChar == '\\' ? "julia.exe" : "julia";
        String executableOnPath = findOnPath(executableName, System.getenv("PATH"));
        if (executableOnPath != null) {
            return executableOnPath;
        }

        throw new JuliaRuntimeException("Julia executable is not found in predefined paths.");
    }

    static String findOnPath(String executableName, String path) {
        if (path == null) {
            return null;
        }

        for (String directory : path.split(File.pathSeparator)) {
            File executable = new File(directory, executableName);
            if (executable.isFile() && executable.canExecute()) {
                return executable.getPath();
            }
        }

        return null;
    }

}
