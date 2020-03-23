const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

//
// function testResolve() {
//   let resolve = clone(webpackConfig.resolve);
//   resolve.modules = [
//     path.resolve(__dirname, '../../build/js/node_modules'),
//     path.resolve(__dirname, '../../build/processedResources/Js/main'),
//     path.resolve(__dirname, '../../node_modules')
//   ];
//
//   return resolve;
// }
//
// const SpeedMeasurePlugin = require("speed-measure-webpack-plugin");
//
// const smp = new SpeedMeasurePlugin();
let resourcesPath = path.resolve(__dirname, '../../../../client/build/processedResources/Js/main');

config.externals = {jquery: "jQuery"};
config.resolve.modules.push(
  resourcesPath
);
config.resolve.extensions = ['.js', '.ts'];

config.module.rules.push({
    test: /\.ts(x?)$/,
    use: [
      'babel-loader',
      'ts-loader?' + JSON.stringify({silent: true})
    ]
  },
  {test: /\.md$/i, use: 'raw-loader'}, {
    test: /\.(sa|sc|c)ss$/,
    use: [
      {
        loader: MiniCssExtractPlugin.loader,
        options: {
          hmr: process.env.NODE_ENV === 'development',
        },
      },
      {
        loader: 'css-loader',
        options: {
          modules: 'global'
        }
      },
      'sass-loader',
    ],
  }, {
    test: /\.(png|woff|woff2|eot|ttf|svg)(\?v=\d+\.\d+\.\d+)?$/,
    loader: 'url-loader?limit=100000'
  }
);
config.plugins.push(new MiniCssExtractPlugin({
  filename: './styles.css'
}));

// const config = {
//   module: webpackConfig.module,
//   entry: path.resolve(__dirname, 'tests.bundle.js'),
//   resolve: testResolve(),
//   externals: webpackConfig.externals,
//   mode: "development",
//   plugins: [
//     new MiniCssExtractPlugin({
//       filename: './styles.css'
//     }),
//     new webpack.DllReferencePlugin({
//       context: '.',
//       manifest: require('../build/lib/vendor/vendor-manifest.json')
//     }),
//     new webpack.DllReferencePlugin({
//       context: '.',
//       manifest: require('../build/lib/test-vendor/testVendor-manifest.json')
//     }),
//     new webpack.ProvidePlugin({'window.jQuery': 'jquery', $: 'jquery', 'jQuery': 'jquery'})
//   ],
//   optimization: {
//     removeAvailableModules: false,
//     removeEmptyChunks: false,
//     splitChunks: false,
//   },
//   devtool: 'inline-source-map'
// };
