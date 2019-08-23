import CouplingDataService from "./lib/CouplingDataService";
import UserDataService from "./lib/UserDataService";
import * as Promise from "bluebird";
import * as express from "express";
import * as expressWs from "express-ws";

const config = require('./config/config');
const serverKt = require("server");

const serverless = require('serverless-http');

function buildApp() {
    const wsInstance = expressWs(express());
    const app = wsInstance.app;
    const couplingDataService = new CouplingDataService(config.mongoUrl);
    const userDataService = new UserDataService(couplingDataService.database);

    require('./config/express')(app, userDataService);
    require('./routes')(wsInstance, userDataService, couplingDataService);
    return app;
}

export const handler = serverless(buildApp());

export function start() {
    const app = buildApp();

    return new Promise(function (resolve) {
        const server = app.listen(app.get('port'), function () {
            //noinspection JSUnresolvedVariable, JSUnresolvedFunction
            serverKt.com.zegreatrob.coupling.server.logStartup(
                app.get('port'),
                config.buildDate,
                config.gitRev,
                app.get('env')
            );

            resolve(server);
        });
    });
}