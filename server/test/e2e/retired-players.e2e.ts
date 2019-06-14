"use strict";
import {browser, By, element} from "protractor";
import * as monk from "monk";
import e2eHelp from "./e2e-help";

const config = require("../../config/config");
const hostName = 'http://' + config.publicHost + ':' + config.port;
const database = monk.default(config.tempMongoUrl);
const tribeCollection = database.get('tribes');
const playersCollection = database.get('players');
const pluck = require('ramda/src/pluck');

describe('The retired players page', function () {

    const tribe = {
        id: 'graveyard',
        name: 'Arkham Asylum'
    };

    const player1 = {_id: monk.id(), tribe: tribe.id, name: "player1", isDeleted: true};
    const player2 = {_id: monk.id(), tribe: tribe.id, name: "player2", isDeleted: true};
    const player3 = {_id: monk.id(), tribe: tribe.id, name: "player3", isDeleted: null};
    const player4 = {_id: monk.id(), tribe: tribe.id, name: "player4", isDeleted: true};
    const players = [
        player1,
        player2,
        player3,
        player4
    ];

    const retiredPlayers = [
        player1,
        player2,
        player4
    ];

    beforeAll(async function () {
        await tribeCollection.insert(tribe);
        await e2eHelp.authorizeUserForTribes([tribe.id]);
        await playersCollection.drop();
        await playersCollection.insert(players);

        await browser.get(`${hostName}/test-login?username=${e2eHelp.userEmail}&password="pw"`);
        await browser.waitForAngular();
    });

    afterAll(async function () {
        await tribeCollection.remove({id: tribe.id}, false)
    });

    e2eHelp.afterEachAssertLogsAreEmpty();

    beforeEach(async function () {
        await browser.setLocation(`/${tribe.id}/players/retired`);
    });

    it('shows the retired players', async function () {
        const playerElements = element.all(By.css('.react-player-card'));
        expect(playerElements.getText()).toEqual(pluck('name', retiredPlayers));
    });

    it('has a tribe card', function () {
        const tribeCardHeaderElement = element(By.className("tribe-card-header"));
        expect(tribeCardHeaderElement.getText()).toEqual(tribe.name);
    });

});