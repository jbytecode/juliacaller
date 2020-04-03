package com.matletik.juliacaller;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class JuliaCaller {

    private String pathToJulia;
    private Socket socket;
    private BufferedWriter bufferedWriterForJuliaConsole, bufferedWriterForSocket;
    private BufferedReader bufferedReaderForJuliaConsole, bufferedReaderForSocket;
    private int port;
    private int maximumTriesToConnect = 5;

    public JuliaCaller(String pathToJulia, int port){
        this.pathToJulia = pathToJulia;
        this.port = port;
    }

    public void setMaximumTriesToConnect(int tries){
        this.maximumTriesToConnect = tries;
    }

    public int getMaximumTriesToConnect(){
        return this.maximumTriesToConnect;
    }

    public void startServer() throws IOException {
        Process process = Runtime.getRuntime().exec(pathToJulia);
        InputStream is = this.getClass().getResourceAsStream("juliacaller.jl");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while(true){
            String line = reader.readLine();
            if(line == null){
                break;
            }
            sb.append(line);
            sb.append("\r\n");
        }
        reader.close();
        is.close();
        bufferedReaderForJuliaConsole = new BufferedReader(new InputStreamReader(process.getInputStream()));
        bufferedWriterForJuliaConsole = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        bufferedWriterForJuliaConsole.write(sb.toString());
        bufferedWriterForJuliaConsole.newLine();
        bufferedWriterForJuliaConsole.write("serve(" + this.port + ")");
        bufferedWriterForJuliaConsole.newLine();
        bufferedWriterForJuliaConsole.flush();
    }

    public void Connect() throws IOException {
        int numtries = 1;
        boolean connected = false;
        while (numtries <= this.maximumTriesToConnect){
            try{
                socket = new Socket("localhost", this.port);
                connected = true;
                break;
            }catch (ConnectException ce){
                numtries++;
                try{
                    System.out.println("C: retrying to connect: " + numtries + " / " + maximumTriesToConnect);
                    Thread.sleep(1000);
                }catch (InterruptedException ie){

                }
            }
        }
        if(connected) {
            bufferedWriterForSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReaderForSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }else{
            throw new MaximumTriesForConnectionException("Socket cannot connect in maximum number of iterations defined as " + maximumTriesToConnect);
        }
    }

    public void Execute(String command) throws IOException {
        bufferedWriterForSocket.write("execute " + command);
        bufferedWriterForSocket.newLine();
    }

    public String GetAsJSON(String varname) throws IOException {
        bufferedWriterForSocket.write("get " + varname);
        bufferedWriterForSocket.newLine();
        bufferedWriterForSocket.flush();
        return bufferedReaderForSocket.readLine();
    }
}
