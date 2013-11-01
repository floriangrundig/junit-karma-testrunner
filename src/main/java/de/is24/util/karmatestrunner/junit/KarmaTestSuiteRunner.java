package de.is24.util.karmatestrunner.junit;

import de.is24.util.karmatestrunner.JSTestExecutionServer;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The KarmaTestSuiteRunner executes javascript test with karma and displays the results in ide using standard junit runnotifiers.
 * The KarmaTestSuiteRunner does not produce a junit report file or something like that.
 * <p/>
 * <p/>
 * Use the KarmaProcessName annotation to configure the process name if it is not "karma" for linux and mac machines or "karma.cmd" for windows machines which will be detected automatically.
 * Use the KarmaProcessBuilderArgs annotation to configure how to start karma (defaults to {"karma", "start"}.
 * Use the KarmaConfigPath annotation to configure the path to the karma config file (defaults to "karma.conf").
 * Use the KarmaRemoteServerPort annotation to configure the port of the server which receives the test results from karma (defaults to 9889).
 */
public class KarmaTestSuiteRunner extends ParentRunner<String> {

  private static final int DEFAUTL_REMOTE_SERVER_PORT = 9889;

  /**
   * Points to the karma process name. Defaults to
   * "karma" on linux or mac machines and to "karma.cmd" on windows machines.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Inherited
  public @interface KarmaProcessName {
    String value();
  }

  /**
   * Provides the karma process argument. Defaults to
   * "start".
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Inherited
  public @interface KarmaProcessArg {
    String value();
  }

  /**
   * Provide a list of executable files. The first existing file will be executed all other will be ignored.
   * If this annotation is used, the annotations KarmaProcessName and KarmaProcessArg are forbidden.
   */
  @Deprecated
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Inherited
  public @interface KarmaStartupScripts {
    String[] value();
  }

  /**
   * Describes the process builder arguments to start karma. Defaults to ["karma", "start"]
   * @deprecated use KarmaProcessName and KarmaProcessArg if auto detection work not for you.
   */
  @Deprecated
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Inherited
  public @interface KarmaProcessBuilderArgs {
    String[] value();
  }

  /**
   * Describes the karma config path used by the test results server. Defaults to
   * "karma.conf".
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Inherited
  public @interface KarmaConfigPath {
    String value();
  }

  /**
   * Describes the remote server port which karma reports the test result to. Defaults to 9889.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Inherited
  public @interface KarmaRemoteServerPort {
    int value();
  }


  private RunnerScheduler fScheduler = new RunnerScheduler() {
    public void schedule(Runnable childStatement) {
      childStatement.run();
    }

    public void finished() {
      // do nothing
    }
  };

  private JSTestExecutionServer jsTestExecutionServer;


  public KarmaTestSuiteRunner(Class<?> testClass) throws InitializationError {
    super(testClass);

    KarmaProcessBuilderArgs karmaProcessBuilderArgsAnnotation = testClass.getAnnotation(KarmaProcessBuilderArgs.class);
    ArrayList<String> karmaProcessBuilderArgs;
    if (karmaProcessBuilderArgsAnnotation == null) {
      karmaProcessBuilderArgs = new ArrayList<String>(Arrays.asList("karma", "start")); // defaults
    } else {
      karmaProcessBuilderArgs = new ArrayList<String>(Arrays.asList(karmaProcessBuilderArgsAnnotation.value()));
    }

    KarmaConfigPath karmaConfigPathAnnotation = testClass.getAnnotation(KarmaConfigPath.class);
    String karmaConfigPath;
    if (karmaConfigPathAnnotation == null) {
      karmaConfigPath = "karma.conf";
    } else {
      karmaConfigPath = karmaConfigPathAnnotation.value();
    }

    KarmaRemoteServerPort karmaRemoteServerPortAnnotation = testClass.getAnnotation(KarmaRemoteServerPort.class);
    int karmaRemoteServerPort;
    if (karmaRemoteServerPortAnnotation == null) {
      karmaRemoteServerPort = DEFAUTL_REMOTE_SERVER_PORT;
    } else {
      karmaRemoteServerPort = karmaRemoteServerPortAnnotation.value();
    }

    karmaProcessBuilderArgs.add(karmaConfigPath);

    jsTestExecutionServer = new JSTestExecutionServer(karmaRemoteServerPort);
    jsTestExecutionServer.setKarmaStartCmd((String[]) karmaProcessBuilderArgs.toArray(new String[0]));
    jsTestExecutionServer.setTestClass(testClass);
  }


  /**
   * Clean up our test environment.
   *
   * @param statement the statement we should append to.
   * @return the appended statement.
   */
  private Statement afterTests(final Statement statement) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          jsTestExecutionServer.afterTests();
          // Evaluate all that comes before this point.
          statement.evaluate();
        } finally {
          // nop
        }
      }
    };
  }

  /**
   * Establish our test environment.
   *
   * @param statement the statement to prepend.
   * @return the prepended statement.
   */
  private Statement beforeTests(final Statement statement) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        jsTestExecutionServer.beforeTests();
        // Evaluate the remaining statements.
        statement.evaluate();
      }
    };
  }

  @Override
  protected Statement classBlock(final RunNotifier notifier) {
    Statement statement = super.classBlock(notifier);
    statement = beforeTests(statement);
    statement = afterTests(statement);
    return statement;
  }


  /**
   * Returns a {@link Statement}: Call {@link #runChild(Object, RunNotifier)}
   * on each object returned by {@link #getChildren()} (subject to any imposed
   * filter and sort)
   */
  protected Statement childrenInvoker(final RunNotifier notifier) {
    return new Statement() {
      @Override
      public void evaluate() {
        runChildren(notifier);
      }
    };
  }

  private void runChildren(final RunNotifier notifier) {
    fScheduler.schedule(new Runnable() {
      public void run() {
        runChild(null, notifier);
      }
    });
    fScheduler.finished();
  }

  @Override
  protected Description describeChild(String name) {
    return Description
      .createTestDescription(this.getTestClass().getJavaClass(),
        name);
  }

  @Override
  protected List<String> getChildren() {
    ArrayList<String> names = new ArrayList<String>();
    return names;
  }


  @Override
  protected void runChild(String name, RunNotifier notifier) {
    jsTestExecutionServer.runTests(notifier);
  }

}
