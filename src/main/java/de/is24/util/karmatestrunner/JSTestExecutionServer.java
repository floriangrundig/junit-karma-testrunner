package de.is24.util.karmatestrunner;

import de.is24.util.karmatestrunner.jetty.ResultReceiverServer;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.util.ArrayList;


public class JSTestExecutionServer {

    final static int MAX_RETRIES_TO_START_RESULT_RECEIVER_SERVER = 25;
    ResultReceiverServer server;
    int port;
    ArrayList<String> karmaProcessBuilderArgs;
    private Class<?> testClass;
    private boolean resultReceiverStarted = false;


    public JSTestExecutionServer(int port) {
        this.port = port;
    }


    public void beforeTests() throws Exception {
        tryToStartResultReceiverServer(MAX_RETRIES_TO_START_RESULT_RECEIVER_SERVER);
    }

    public void runTests(RunNotifier notifier) {
        TestReporter reporter = new TestReporter(notifier);
        reporter.setTestClass(testClass);

        if (!resultReceiverStarted) {
            notifier.fireTestFailure(
                    new Failure(Description.createSuiteDescription(testClass), new RuntimeException("result receiver server could not be started...."))
            );
            return;
        }

        server.setTestReporter(reporter);

        try {
            runKarma();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void afterTests() {
        // nop
    }

    public void setKarmaStartCmd(ArrayList<String> args) {
        this.karmaProcessBuilderArgs = args;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    private void tryToStartResultReceiverServer(int retries) throws Exception {

        if (retries == 0) {
            System.err.println("Maximum retries (" + MAX_RETRIES_TO_START_RESULT_RECEIVER_SERVER + ") to start the karma result receiver exceeded");
            return;
        }


        port = PortFinder.findFreePort(port); // this is only a good guess - if the port will be used just in time, when we will retry to connect with another port

        server = new ResultReceiverServer(port);

        try {
            server.start();
            resultReceiverStarted = true;
            karmaProcessBuilderArgs.add("--remoteReporterPort=" + port);
            System.out.println("Karma result receiver server startet at port: " + port);
        } catch (BindException e) {
            retries--;
            tryToStartResultReceiverServer(retries);
        }
    }

    private void runKarma() throws Exception {
        Process process;
        // Get the command args and execute them, merging STDOUT and STDERR
        ProcessBuilder builder = new ProcessBuilder(karmaProcessBuilderArgs);
        builder.redirectErrorStream(true);
//        builder.redirectOutput();
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
