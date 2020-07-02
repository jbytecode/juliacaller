package com.matletik.juliacaller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class JuliaCaller {

    private String pathToJulia;
    private Socket socket;
    private BufferedWriter bufferedWriterForJuliaConsole, bufferedWriterForSocket;
    private BufferedReader bufferedReaderForJuliaConsole, bufferedReaderForSocket;
    private int port;
    private int maximumTriesToConnect = 10;

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
        SimpleLog("startServer: Sending serve(" + this.port + ") request.") ;
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
                SimpleLog("Connect: connected!");
                break;
            }catch (ConnectException ce){
                numtries++;
                try{
                    SimpleLog("Connect: retrying to connect: " + numtries + " / " + maximumTriesToConnect);
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

    public synchronized void Execute(String command) throws IOException {
        SimpleLog("Execute: Sending '" + command + "'");
        bufferedWriterForSocket.write("execute " + command);
        bufferedWriterForSocket.newLine();
    }
    
    public void addJuliaObject(JuliaObject object) throws IOException {
        Execute(object.getCode());
    }

    public void ExitSession() throws IOException {
        bufferedWriterForSocket.write("exit");
        bufferedWriterForSocket.newLine();
    }

    public void ShutdownServer() throws IOException {
        bufferedWriterForSocket.write("shutdown");
        bufferedWriterForSocket.newLine();
    }

    public synchronized String GetAsJSONString(String varname) throws IOException {
        SimpleLog("GetAsJSONString: Requesting variable " + varname);
        bufferedWriterForSocket.write("get " + varname);
        bufferedWriterForSocket.newLine();
        bufferedWriterForSocket.flush();
        return bufferedReaderForSocket.readLine();
    }

    public JSONObject GetAsJSONObject(String name) throws IOException {
        return new JSONObject(GetAsJSONString(name));
    }

    public JSONArray GetAsJSONArray(String name) throws IOException {
        String jsonString = GetAsJSONString(name);
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray(name);
        return arr;
    }

    public static void SimpleLog(String log){
        if(Constants.VERBOSE_TRUE.equals(Constants.properties.getProperty("VERBOSE", Constants.VERBOSE_FALSE))){
            System.out.println("JuliaCaller: " + log);
        }
    }
}
