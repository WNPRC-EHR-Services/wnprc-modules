const path = require('path');
var webpack = require("webpack");

module.exports = {
    entry: {
        "legacy": path.resolve(__dirname, 'src', 'ts', 'legacy.ts')
    },

    output: {
        path: path.resolve(__dirname, 'build', 'explodedModule', 'web', 'webutils', 'lib'),
        filename: '[name].js',
        // Export the module as the SimpleFilter variable
        library: "WebUtils",
        libraryTarget: "umd",
        sourceMapFilename: '[name].js.map'
    },

    // Enable sourcemaps for debugging webpack's output.
    // TODO: switch to 'source-map' for production
    devtool: "eval-source-map",

    resolve: {
        alias: {
            classify: path.join(__dirname, "./external_resources/js/classify.js"),
            supersqlstore: path.join(__dirname, "./external_resources/js/supersqlstore.js")
        },

        // Add '.ts' and '.tsx' as resolvable extensions.
        extensions: [".ts", ".tsx", ".js"],

        // Include Bower to find certain modules.
        modules: ["node_modules"],
        descriptionFiles: ["package.json"]
    },

    module: {
        rules: [
            // All files with a '.ts' or '.tsx' extension will be handled by 'awesome-typescript-loader'.
            { test: /\.tsx?$/, loader: "ts-loader" },

            // All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
            { test: /\.js$/, loader: "source-map-loader", enforce: "pre" },

            { test: /\.scss$/, loader: 'style!css!sass' }
        ]
    },

    externals: {
        "c3":     'c3',
        "d3":     'd3',
        "fetch":  'fetch',
        "jquery": 'jQuery',
        "knockout": "ko",
        "moment": 'moment',
        "qunit":  'QUnit',
        "Qunit":  "Qunit",
        "react":  'React',
        "React":  'React',

        "react-dom":        'ReactDOM',
        "react-dom/server": 'ReactDOMServer'
    }
};