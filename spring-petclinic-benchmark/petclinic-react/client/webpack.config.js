const path = require('path');
const webpack = require('webpack');
const port = process.env.PORT || 3000;
const entries = [
  './src/main.tsx'
];
module.exports = {
  devtool: 'source-map',
  entry: entries,
  output: {
    path: path.join(__dirname, 'public/dist/'),
    filename: 'bundle.js',
    publicPath: '/dist/'
  },
  plugins: [
      new webpack.DefinePlugin({
        __API_SERVER_URL__: JSON.stringify('http://localhost:9966/petclinic')
      }),
    ],
  resolve: {
    extensions: ['.ts', '.tsx', '.js']
  },
  resolveLoader: {
    modules: [path.join(__dirname, 'node_modules')]
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader']
      },
      {
        test: /\.less$/,
        use: ['style-loader', 'css-loader', 'less-loader'],
        include: path.join(__dirname, 'src/styles')
      },
      {
        test: /\.(png|jpg)$/,
        loader: 'url-loader',
        options: { limit: 25000 }
      },
      {
        test: /\.(eot|svg|ttf|woff|woff2)$/,
        loader: 'file-loader',
        options: { name: 'public/fonts/[name].[ext]' }
      },
      {
        test: /\.tsx?$/,
        use: ['babel-loader', 'ts-loader'],
        include: path.join(__dirname, 'src')
      },
      {
        test: /\.tsx?$/,
        use: [
          'babel-loader',
          {
            loader: 'ts-loader',
            options: {
              transpileOnly: true
            }
          }
        ],
        include: path.join(__dirname, 'src')
      }
    ]
  }
};
