package de.is24.util.karmatestrunner;

import de.is24.util.karmatestrunner.junit.KarmaTestSuiteRunner;
import org.junit.runner.RunWith;

/**
 * To run this test make sure that you have all required node-modules installed (see README in karma_test_project)
 */
@RunWith(KarmaTestSuiteRunner.class)
@KarmaTestSuiteRunner.KarmaConfigPath("./karma_test_project/config/karma.conf.v0.9.x.js")
//@KarmaTestSuiteRunner.KarmaProcessBuilderArgs("./karma_test_project/scripts/test.v0.9.x.sh") // on my MAC I need to set the PATH for finding karma, so I run a shell script
public class KarmaTestSuiteRunnerBasicTestFixture {

}
