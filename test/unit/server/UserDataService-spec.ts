import * as monk from "monk";
import * as Promise from "bluebird";
import UserDataService from "../../../server/lib/UserDataService";

var config = require('../../../config');

var mongoUrl = config.testMongoUrl + '/UsersTest';
var database = monk.default(mongoUrl);
var userDataService = new UserDataService(database);

var safeDone = function (error, done) {
    if (error) {
        done.fail(error);
    } else {
        done();
    }
};
describe('UserDataService', function () {
    var usersCollection = database.get('users');

    beforeEach(function (done) {
        usersCollection.drop()
            .then(done, done.fail);
    });

    describe('findOrCreate', function () {
        it('will create a user if it does not already exist', function (done) {
            var email = 'awesome.o@super.coo';
            userDataService.findOrCreate(email, function (user) {
                expect(user).not.toBe(undefined);
                expect(user.email).toEqual(email);

                usersCollection.find({}, function (error, docs) {
                    expect(docs.length).toEqual(1);
                    expect(docs[0]).toEqual(user);

                    safeDone(error, done);
                });
            });
        });

        it('will get existing user when the user with that email already exists', function (done) {
            var email = 'awesome.o@super.coo';
            userDataService.findOrCreate(email, function (newlyCreatedUser) {
                userDataService.findOrCreate(email, function (existingUser) {
                    expect(existingUser).toEqual(newlyCreatedUser);
                    usersCollection.find({}, function (error, docs) {
                        expect(docs.length).toEqual(1);
                        expect(docs[0]).toEqual(existingUser);
                        safeDone(error, done);
                    });
                });
            });
        });
    });

    describe('serialize user', function () {
        it('will return the _id', function (done) {
            var user = {_id: 'amazingId'};
            userDataService.serializeUser(user, function (error, id) {
                expect(id).toEqual(user._id);
                safeDone(error, done);
            });
        });

        it('will return error if there is no _id', function (done) {
            var user = {notId: 'amazingId'};
            userDataService.serializeUser(user, function (error) {
                expect(error).toEqual('The user did not have an id to serialize.');
                done();
            });
        });
    });

    describe('deserialize user', function () {
        it('will return object in the users collection from mongo', function (done) {
            var id = monk.id();
            var expectedUser = {_id: id, uniqueValue: 'bloopers'};
            usersCollection.insert(expectedUser)
                .then(function () {
                    return usersCollection.find({_id: id});
                })

                .then(function () {
                    return Promise.promisify(userDataService.deserializeUser)(id);
                })
                .then(function (loadedUser) {
                    expect(loadedUser).toEqual(expectedUser);
                })
                .then(done, done.fail);
        });

        it('will return error when user is not in mongo', function (done) {
            var id = monk.id();
            var expectedUser = {_id: id, uniqueValue: 'bloopers'};
            Promise.promisify(userDataService.deserializeUser)(expectedUser._id)
                .then(function () {
                    fail('This should have thrown an error.');
                }, function (error) {
                    expect(error.message).toEqual('The user with id: ' +
                        id + ' could not be found in the database.');
                })
                .then(done, done.fail);
        });
    });
});