package de.is24.util.karmatestrunner;

import de.is24.util.karmatestrunner.jetty.ResultReceiverServer;
import org.junit.runner.notification.RunNotifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class JSTestExecutionServer {

    ResultReceiverServer server;
    int port = 9000;
    String [] karma_start_cmds;

    public JSTestExecutionServer(int port){
        this.port = port;
    }

    public void setKarmaStartCmd(String ... args){
        this.karma_start_cmds = args;
    }

    public void beforeTests() throws Exception{
            server = new ResultReceiverServer(port);
            server.start();
    }

    public void runTests(RunNotifier notifier){
        TestReporter reporter = new TestReporter(notifier);

        server.setTestReporter(reporter);

        try {
            runKarma();
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void afterTests(){

    }

    private void runKarma() throws Exception {
        System.err.println(System.getenv("PATH"));

        Process process;
        // Get the command args and execute them, merging STDOUT and STDERR
        //ProcessBuilder builder = new ProcessBuilder("/usr/local/lib/node_modules/karma/bin/karma","start");
        ProcessBuilder builder = new ProcessBuilder(karma_start_cmds);
        builder.redirectErrorStream(true);
        builder.redirectOutput();
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new IOException(
                    "error while starting karma: " + e.toString());
        }

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("Program terminated!");

    }

}
