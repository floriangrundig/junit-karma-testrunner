package de.is24.util.karmatestrunner;

import java.util.ArrayList;
import java.util.Arrays;

import static de.is24.util.karmatestrunner.junit.KarmaTestSuiteRunner.KarmaConfigPath;
import static de.is24.util.karmatestrunner.junit.KarmaTestSuiteRunner.KarmaProcessArgs;
import static de.is24.util.karmatestrunner.junit.KarmaTestSuiteRunner.KarmaProcessName;
import static de.is24.util.karmatestrunner.junit.KarmaTestSuiteRunner.KarmaRemoteServerPort;
import static de.is24.util.karmatestrunner.junit.KarmaTestSuiteRunner.KarmaStartupScripts;

/**
 * The config provider parses the test class annotations and the system properties and provides
 * the configuration for setting up the execution server.
 */
public class ExecutionServerConfigProvider {

  public static final String KARMA_PROCESS_NAME_SYSTEM_PROPERTY = "karma.process.name";
  public static final String KARMA_PROCESS_ARGS_SYSTEM_PROPERTY = "karma.process.args";
  public static final String KARMA_STARTUP_SCRIPTS_SYSTEM_PROPERTY = "karma.startup.scripts";
  public static final String KARMA_REMOTE_SERVER_PORT_SYSTEM_PROPERTY = "karma.remoteServerPort";

  public static final int DEFAULT_KARMA_REMOTE_SERVER_PORT = 9889;
  public static final ArrayList<String> DEFAULT_KARMA_PROCESS_ARGS = new ArrayList(Arrays.asList("start"));
  public static final ArrayList<String> DEFAULT_KARMA_STARTUP_SCRIPTS = new ArrayList();
  public static final String DEFAULT_KARMA_CONFIG_PATH = "karma.conf.js";

  private final Class<?> testClass;

  public ExecutionServerConfigProvider(Class<?> testClass) {
    this.testClass = testClass;
  }

  public String getKarmaProcessName() {
    KarmaProcessName annotation = testClass.getAnnotation(KarmaProcessName.class);
    String resultAsString = System.getProperty(KARMA_PROCESS_NAME_SYSTEM_PROPERTY, annotation != null ? annotation.value() : "");

    if (resultAsString.isEmpty()) {
      if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
        return "karma.cmd";
      } else {
        return "karma";
      }
    }

    return resultAsString;
  }

  public ArrayList<String> getKarmaProcessArgs() {
    KarmaProcessArgs annotation = testClass.getAnnotation(KarmaProcessArgs.class);
    String resultAsString = System.getProperty(KARMA_PROCESS_ARGS_SYSTEM_PROPERTY, annotation != null ? annotation.value() : "");
    if (resultAsString.isEmpty()){
      return DEFAULT_KARMA_PROCESS_ARGS;
    }
    return new ArrayList(Arrays.asList(resultAsString.split(",")));
  }

  public ArrayList<String> getKarmaStartupScripts() {
    KarmaStartupScripts annotation = testClass.getAnnotation(KarmaStartupScripts.class);
    String resultAsString = System.getProperty(KARMA_STARTUP_SCRIPTS_SYSTEM_PROPERTY, annotation != null ? annotation.value() : "");
    if (resultAsString.isEmpty()){
      return DEFAULT_KARMA_STARTUP_SCRIPTS;
    }
    return new ArrayList(Arrays.asList(resultAsString.split(",")));
  }

  public String getKarmaConfigPath() {
    KarmaConfigPath annotation = testClass.getAnnotation(KarmaConfigPath.class);
    String resultAsString = annotation != null ? annotation.value() : "";
    return resultAsString.isEmpty() ? DEFAULT_KARMA_CONFIG_PATH : resultAsString;
  }

  public int getKarmaRemoteServerPort() {
    KarmaRemoteServerPort annotation = testClass.getAnnotation(KarmaRemoteServerPort.class);
    String resultAsString = System.getProperty(KARMA_REMOTE_SERVER_PORT_SYSTEM_PROPERTY, annotation != null ? (annotation.value() + "") : "");
    return resultAsString.isEmpty() ? DEFAULT_KARMA_REMOTE_SERVER_PORT : Integer.parseInt(resultAsString);
  }

}
