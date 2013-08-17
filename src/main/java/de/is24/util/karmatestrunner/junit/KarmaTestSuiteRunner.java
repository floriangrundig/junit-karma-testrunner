/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package de.is24.util.karmatestrunner.junit;

import de.is24.util.karmatestrunner.JSTestExecutionServer;
import de.is24.util.karmatestrunner.jetty.ResultReceiverServer;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class KarmaTestSuiteRunner extends ParentRunner<String> {

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
         jsTestExecutionServer = new JSTestExecutionServer(9000);
        // TOOD replace with default cmd and annotation value
        jsTestExecutionServer.setKarmaStartCmd("/bin/sh", "-c", "/Users/florian/IdeaProjects/gitClones/junit-karma-testrunner/karma_test_project/scripts/test.v0.9.x.sh");
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

    // TODO remove?
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
