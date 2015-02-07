module.exports = function (config) {
    config.set({

        basePath: '../.',
        files: [
            'lib/angular/angular.js',
            'lib/angular/angular-loader.js',
            'lib/angular/angular-resource.js',
            'lib/angular/angular-sanitize.js',
            'lib/angular/angular-mocks.js',
            'js/*.js',
            'unit-tests/**/*.js'
        ],

        browsers: ['PhantomJS'],
        reporters: ['progress', 'junit', 'remote'],
        frameworks: ["jasmine"],
        autoWatch: false,
        singleRun: true,
        junitReporter: {
            outputFile: 'target/test_out/unit.xml',
            suite: 'unit'
        },
        remoteReporter: {
            port: '9889',
            finishDelay : 100
        },
        plugins: [
            'karma-jasmine',
            'karma-phantomjs-launcher',
            'karma-junit-reporter',
            'karma-remote-reporter'
        ]
    });
};