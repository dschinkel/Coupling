"use strict";

describe('Service: ', function () {
    beforeEach(function () {
        module('coupling.services');
    });

    describe('Coupling', function () {

        var httpBackend;
        var Coupling;

        beforeEach(function () {
            inject(function (_Coupling_, $httpBackend) {
                httpBackend = $httpBackend;
                Coupling = _Coupling_;
            });
        });

        describe('get tribes', function () {
            it('calls back with tribes on success', function () {
                var expectedTribes = [
                    {_id: 'one'},
                    {_id: 'two'}
                ];

                httpBackend.whenGET('/api/tribes').respond(200, expectedTribes);

                var returnedTribes = null;
                Coupling.getTribes(function (resultTribes) {
                    returnedTribes = resultTribes;
                });

                httpBackend.flush();

                expect(returnedTribes).toEqual(expectedTribes);
            });

            it('shows error on failure', function () {
                var statusCode = 404;
                var url = '/api/tribes';
                var expectedData = 'nonsense';
                httpBackend.whenGET(url).respond(statusCode, expectedData);
                var callCount = 0;
                Coupling.getTribes(function () {
                    callCount++;
                });

                var alertSpy = spyOn(window, 'alert');
                httpBackend.flush();
                expect(callCount).toBe(0);
                expect(alertSpy).toHaveBeenCalledWith('There was a problem loading ' + url + '\n' +
                'Data was: <' + expectedData + '>\n' +
                'Status code: ' + statusCode);
            });
        });

        describe('select tribe', function () {
            it('will request players and history for given tribe', function () {
                var tribeId = 'awesomeTribe';

                var expectedPlayers = [
                    {name: 'player1'},
                    {name: 'player2'}
                ];

                var expectedHistory = [
                    {time: 'before'},
                    {time: 'after'}
                ];

                httpBackend.whenGET('/api/tribes').respond(200, []);
                httpBackend.whenGET('/api/' + tribeId + '/players').respond(200, expectedPlayers);
                httpBackend.whenGET('/api/' + tribeId + '/history').respond(200, expectedHistory);

                var loadedPlayers = null, loadedHistory = null;
                Coupling.selectTribe(tribeId, function (players, history) {
                    loadedPlayers = players;
                    loadedHistory = history;
                });

                httpBackend.flush();

                expect(expectedPlayers).toEqual(loadedPlayers);
                expect(expectedHistory).toEqual(loadedHistory);
            });
        });

        describe('save tribe', function () {
            it('will post to persistence and callback', function () {

                httpBackend.whenPOST('/api/tribes').respond(200);
                var tribe = {name: 'Navi'};
                var callbackCallCount = 0;
                Coupling.saveTribe(tribe, function () {
                    callbackCallCount++;
                });

                httpBackend.flush();
                expect(callbackCallCount).toBe(1);
            });

        });

        describe('list all pins', function () {
            var tribeId = 'scruff';
            var url = '/api/' + tribeId + '/pins';

            it('will list all pins for a tribe', function (done) {
                var expectedPins = [{stuff: 'maguff'}, {stuff: 'mcduff'}];
                httpBackend.whenGET(url).respond(200, expectedPins);

                var pinsPromise = Coupling.promisePins(tribeId);
                pinsPromise.then(function (pins) {
                    expect(pins).toEqual(expectedPins);
                    done();
                }).catch(done);
                httpBackend.flush();
            });

            it('shows error on failure', function (done) {
                var statusCode = 404;
                var expectedData = 'nonsense';
                httpBackend.whenGET(url).respond(statusCode, expectedData);
                var alertSpy = spyOn(window, 'alert');
                Coupling.promisePins(tribeId).then(function () {
                    done("This should not succeed.");
                }).catch(function () {
                    expect(alertSpy).toHaveBeenCalledWith('Communication error with server. URL: ' + url + '\n' +
                    'Data: <' + expectedData + '>\n' +
                    'Status: ' + statusCode);
                    done();
                });
                httpBackend.flush();
            });
        });
    });
});