'use strict';
var supertest = require('supertest');
var should = require('should');
var config = require('./../../config');
var monk = require('monk');
var _ = require('underscore');

var path = '/api/tribes';

describe(path, function () {
    var host = supertest('http://localhost:' + config.port);
    var Cookies;

    var userEmail = 'test@test.tes';

    beforeEach(function (done) {
        host.get('/test-login?username=' + userEmail + '&password=pw').end(function (err, res) {
            Cookies = res.headers['set-cookie'].pop().split(';')[0];
            done();
        });
    });

    var database = monk(config.tempMongoUrl);
    var tribesCollection = database.get('tribes');
    var playersCollection = database.get('players');
    var usersCollection = monk(config.mongoUrl).get('users');

    function authorizeUserForTribes(authorizedTribes) {
        usersCollection.update({email: userEmail + "._temp"}, {$set: {tribes: authorizedTribes}});
    }

    it('GET will return all available tribes.', function (done) {
        tribesCollection.find({}, {}, function (error, tribeDocuments) {
            var authorizedTribes = _.pluck(tribeDocuments, '_id');
            authorizeUserForTribes(authorizedTribes);

            var httpGet = host.get(path);
            httpGet.cookies = Cookies;
            httpGet.expect('Content-Type', /json/).end(function (error, response) {
                should.not.exist(error);
                response.status.should.equal(200);
                JSON.stringify(response.body).should.equal(JSON.stringify(tribeDocuments));
                done();
            });
        });
    });

    it('GET will return all any tribe that has a player with the given email.', function (done) {
        var tribe = {_id: 'delete-me', name: 'tribe-from-endpoint-tests'};
        tribesCollection.insert(tribe);
        playersCollection.insert({_id: 'delete-me', name: 'delete-me', tribe: 'delete-me', email: userEmail});

        authorizeUserForTribes([]);

        var httpGet = host.get(path);
        httpGet.cookies = Cookies;
        httpGet.expect('Content-Type', /json/).end(function (error, response) {
            should.not.exist(error);
            response.status.should.equal(200);
            JSON.stringify(response.body).should.equal(JSON.stringify([tribe]));

            tribesCollection.remove({_id: 'delete-me'});
            playersCollection.remove({_id: 'delete-me'}, done);
        });
    });

    it('GET will not return all available tribes when the user does not have explicit permission.', function (done) {
        authorizeUserForTribes([]);
        var httpGet = host.get(path);
        httpGet.cookies = Cookies;
        httpGet.expect('Content-Type', /json/).end(function (error, response) {
            should.not.exist(error);
            response.status.should.equal(200);
            JSON.stringify(response.body).should.equal(JSON.stringify([]));
            done();
        });
    });

    describe('POST', function () {
        var newTribe = {name: 'TeamMadeByTest', _id: 'deleteme'};

        it('will create a tribe and authorize it.', function (done) {
            var httpPost = host.post(path);
            httpPost.cookies = Cookies;
            httpPost.send(newTribe).expect('Content-Type', /json/).end(function (error, response) {
                should.not.exist(error);
                response.status.should.equal(200);
                JSON.stringify(response.body).should.equal(JSON.stringify(newTribe));

                var httpGet = host.get(path);
                httpGet.cookies = Cookies;
                httpGet.expect('Content-Type', /json/).end(function (error, response) {
                    should.not.exist(error);
                    response.status.should.equal(200);
                    should.exist(_.findWhere(response.body, newTribe));
                    done();
                });
            });
        });

        after(function () {
            tribesCollection.remove({_id: newTribe._id}, false);
        });
    });
});