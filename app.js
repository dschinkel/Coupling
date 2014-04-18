/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var history = require('./routes/history');
var players = require('./routes/players');
var game = require('./routes/game');
var savePairs = require('./routes/savePairs');
var http = require('http');
var path = require('path');
var config = require('./config');

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));


// development only
if ('development' == app.get('env')) {
    app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/history', history(config.mongoUrl));
app.get('/players', players(config.mongoUrl));
app.post('/savePairs', savePairs(config.mongoUrl));
app.get('/game', game(config.mongoUrl));

http.createServer(app).listen(app.get('port'), function () {
    console.log('Express server listening on port ' + app.get('port'));
});
