package org.expr.juliacaller;

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
    private BufferedReader bufferedReaderForJuliaConsoleErrors;
    private int port;
    private int maximumTriesToConnect = 90;
    private JuliaErrorConsoleWatcher watcher;
    private Process process;

    public JuliaCaller(String pathToJulia, int port) {
        this.pathToJulia = pathToJulia;
        this.port = port;
    }

    public void setMaximumTriesToConnect(int tries) {
        this.maximumTriesToConnect = tries;
    }

    public int getMaximumTriesToConnect() {
        return this.maximumTriesToConnect;
    }

    public void startServer() throws IOException {
        process = new ProcessBuilder(pathToJulia).start();
        InputStream is = this.getClass().getResourceAsStream("juliacaller.jl");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            sb.append(line);
            sb.append("\r\n");
        }
        reader.close();
        is.close();
        bufferedReaderForJuliaConsole = new BufferedReader(new InputStreamReader(process.getInputStream()));
        bufferedWriterForJuliaConsole = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        if (Constants.TRUE.equals(Constants.properties.getProperty(Constants.JULIA_ERROR_CONSOLE, Constants.FALSE))) {
            watcher = new JuliaErrorConsoleWatcher(process);
            watcher.startWatching();
        }

        bufferedWriterForJuliaConsole.write(sb.toString());
        bufferedWriterForJuliaConsole.newLine();
        SimpleLog("startServer: Sending serve(" + this.port + ") request.");
        bufferedWriterForJuliaConsole.write("serve(" + this.port + ")");
        bufferedWriterForJuliaConsole.newLine();
        bufferedWriterForJuliaConsole.flush();
    }

    public void Connect() throws IOException {
        int numtries = 1;
        boolean connected = false;
        while (numtries <= this.maximumTriesToConnect) {
            try {
                socket = new Socket("localhost", this.port);
                connected = true;
                SimpleLog("Connect: connected!");
                break;
            } catch (ConnectException ce) {
                numtries++;
                try {
                    SimpleLog("Connect: retrying to connect: " + numtries + " / " + maximumTriesToConnect);
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {

                }
            }
        }
        if (connected) {
            bufferedWriterForSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReaderForSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } else {
            throw new MaximumTriesForConnectionException(
                    "Socket cannot connect in maximum number of iterations defined as " + maximumTriesToConnect);
        }
    }

    public synchronized void InstallPackage(String pkg) throws IOException {
        SimpleLog("Installing package " + pkg);
        Execute("install " + pkg);
    }

    public synchronized void Execute(String command) throws IOException {
        SimpleLog("Execute: Sending '" + command + "'");
        bufferedWriterForSocket.write("execute " + command);
        bufferedWriterForSocket.newLine();
    }

    /**
     * Executes a Julia command and returns the result as a String.
     *
     * @param command The Julia command to execute. Command may be a struct or a function definition.
     * @return The result of the command execution as a String.
     * @throws IOException If an I/O error occurs while communicating with the Julia server.
     */
    public synchronized void ExecuteDefine(String command) throws IOException {
        SimpleLog("Execute: Sending function definition");
        File tempfile = File.createTempFile("juliacaller-function-definition", "jl");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempfile));
        writer.write(command);
        writer.flush();
        writer.close();
        Execute("include(\"" + tempfile.getAbsolutePath() + "\")");
    }

    public void addJuliaObject(JuliaObject object) throws IOException {
        Execute(object.getCode());
    }

    public void ExitSession() throws IOException {
        bufferedWriterForSocket.write("exit");
        bufferedWriterForSocket.newLine();
    }

    public void ShutdownServer() throws IOException {
        if (Constants.TRUE.equals(Constants.properties.getProperty(Constants.JULIA_ERROR_CONSOLE, Constants.FALSE))) {
            watcher.stopWatching();
        }
        bufferedWriterForSocket.write("shutdown");
        bufferedWriterForSocket.newLine();
    }

    public String GetAsJSONString(String varname) throws IOException {
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

    public double getDouble(String name) throws IOException {
        return (new JSONObject(GetAsJSONString(name))).getDouble(name);
    }

    public float getFloat(String name) throws IOException {
        return (new JSONObject(GetAsJSONString(name))).getFloat(name);
    }

    public int getInt(String name) throws IOException {
        return (new JSONObject(GetAsJSONString(name))).getInt(name);
    }

    public long getLong(String name) throws IOException {
        return (new JSONObject(GetAsJSONString(name))).getLong(name);
    }

    public boolean getBoolean(String name) throws IOException {
        return (new JSONObject(GetAsJSONString(name))).getBoolean(name);
    }

    public static void SimpleLog(String log) {
        if (Constants.TRUE.equals(Constants.properties.getProperty("VERBOSE", Constants.FALSE))) {
            System.out.println("JuliaCaller: " + log);
        }
    }
}
