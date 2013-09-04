#JUnit-Karma test suite runner#
This library allows you to run a karma test suite with a java JUnit test runner.

************
A full running demo for using the runner can be found athttps://github.com/FlorianGrundig/demo-junit-karma-testrunner
************


IMPORTANT: junit-karma-testrunner requires > java 7

###Prerequisites###
To combine the javascript karma test runner with a java JUnit runner you need this library and our
<a href="http://github.com/ImmobilienScout24/karma-remote-reporter" target="_blank">karma-remote-reporter</a>.
The karma-remote-reporter is a karma plugin which you can add easily in your "karma.conf":
<pre><code>
module.exports = function (config) {
    config.set({
      basePath: '../.',
        files: [
            'lib/angular/angular.js',
            'js/*.js',
            'unit-tests/**/*.js'
        ],

        browsers: ['PhantomJS'],
        reporters: ['junit','remote'],
        frameworks: ["jasmine"],
        autoWatch: false,
        singleRun: true,
        junitReporter: {
            outputFile: 'target/test_out/unit.xml',
            suite: 'unit'
        },
        remoteReporter: {
            host: 'localhost',
            port: '9876'
        },
        plugins: [
            'karma-jasmine',
            'karma-phantomjs-launcher',
            'karma-junit-reporter',
            'karma-remote-reporter'
        ]
    });
};
</code></pre>
To add the remote reporter plugin add 'karma-remote-reporter' to the plugin sections and add 'remote' to the reporters section.
To configure the host and port where the karma remote reporter should send the test results use the remoteReporter section.

The test results will send by the remote reporter to a server which is in the java world e.g. this JUnit-karma-testrunner.
All you need is a normal java test class:

###JavaScriptTestSuite.java###
<pre><code>
@RunWith(KarmaTestSuiteRunner.class)
@KarmaTestSuiteRunner.KarmaProcessBuilderArgs({"karma", "start"}) // you can also provide a shell script which starts karma
@KarmaTestSuiteRunner.KarmaConfigPath("karma.conf") // use different karma configurations for e.g. to run unit or even e2e tests
@KarmaTestSuiteRunner.KarmaRemoteServerPort(9876)
public class KarmaTestSuiteRunnerBasicTestFixture {

    @BeforeClass
    public static void setupTestSzenario(){
     ...
    }
    @AfterClass
    public static void cleanupTestSzenario(){
     ...
    }
}

</code></pre>
With the KarmaTestSuiteRunner annotations you can configure your the server where the test results will be reported and the karma config.
With @BeforeClass and @AfterClass you can setup and cleanup your test scenario when running e2e tests.

