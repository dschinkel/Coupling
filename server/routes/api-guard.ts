import CouplingDataService from "../lib/CouplingDataService";

var config = require('../../config');

export default function (couplingDataService) {
    var tempDataService = new CouplingDataService(config.tempMongoUrl);
    return function (request, response, next) {

        request.statsdKey = ['http', request.method.toLowerCase(), request.path].join('.');
        if (!request.isAuthenticated()) {
            response.sendStatus(401);
        } else {
            if (request.user.email.indexOf('._temp') != -1) {
                request.dataService = tempDataService;
            } else {
                request.dataService = couplingDataService;
            }
            next();
        }
    };
};