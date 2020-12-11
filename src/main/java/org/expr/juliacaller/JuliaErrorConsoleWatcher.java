package org.expr.juliacaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JuliaErrorConsoleWatcher {

    final BufferedReader reader;
    final Process process;

    private Thread th;

    public JuliaErrorConsoleWatcher(Process process){
        this.process = process;
        this.reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    public void startWatching(){
        th = new Thread(new Runnable(){
            public void run(){
                while (true){
                    try{
                    String s = reader.readLine();
                    System.out.println(s);
                    }catch(IOException e){
                        System.out.println("Julia Error Console Message:");
                        System.out.println(e.toString());
                    }
                }
            }
        });
        th.start();
        System.out.println("Error console wathcing started:");
    }

    public void stopWatching(){
        System.out.println("** Julia Error Console is terminating");            
        th.interrupt();
    }
}