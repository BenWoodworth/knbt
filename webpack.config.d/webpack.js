const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");

config.plugins = [
    new NodePolyfillPlugin(), // https://github.com/square/okio/issues/1163
];
