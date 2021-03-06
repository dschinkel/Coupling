import CouplingDataService from "../lib/CouplingDataService";
// @ts-ignore
import * as server from "Coupling-server";

const commandDispatcher = server.com.zegreatrob.coupling.server.commandDispatcher;

export default function (userDataService, couplingDataService, tempDataService) {
    return function (request, response, next) {

        request.statsdKey = ['http', request.method.toLowerCase(), request.path].join('.');
        if (!request.isAuthenticated()) {
            if (request.originalUrl.includes('.websocket')) {
                request.close();
            } else {
                response.sendStatus(401);
            }
        } else {
            let email = request.user.email;
            const tempSuffixIndex = email.indexOf('._temp');
            let dataService: CouplingDataService;
            if (tempSuffixIndex != -1) {
                dataService = tempDataService;
            } else {
                dataService = couplingDataService;
            }

            commandDispatcher(
                dataService,
                userDataService.usersCollection,
                request.user,
                `${request.method} ${request.path}`,
                request.traceId
            ).then(dispatcher => {
                request.commandDispatcher = dispatcher;
                next();
            });
        }
    };
};