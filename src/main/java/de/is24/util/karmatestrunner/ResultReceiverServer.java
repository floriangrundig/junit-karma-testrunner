package de.is24.util.karmatestrunner;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ResultReceiverServer {
    final public int port;
    private TestReporter reporter;
    private ResultReceiver resultReceiver;
    private Thread serverThread;

    public ResultReceiverServer(int port) {
        this.port = port;
    }

    public void stop() {
        resultReceiver.setRunning(false);
        serverThread.interrupt();
    }

    public void setTestReporter(TestReporter reporter) {
        this.reporter = reporter;
    }


    private class ResultReceiver implements Runnable {
        private final ServerSocket serverSocket;
        protected volatile boolean running = true;

        public void setRunning(boolean running) {
            this.running = running;
        }

        private ResultReceiver(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            String line = null;
            try {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                while (running) {
                    line = in.readLine();

                    if (line != null && !line.equalsIgnoreCase("done")) { // "done" is just a hack to avoid JSON parsing
                        reporter.handleMessage(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        resultReceiver = new ResultReceiver(serverSocket);
        serverThread = new Thread(resultReceiver);
        serverThread.setDaemon(true);
        serverThread.start();

    }

}
