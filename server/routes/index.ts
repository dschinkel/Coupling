const config = require('../config/config');

export function index(request, response) {
    if (!request.isAuthenticated()) {
        response.redirect('/welcome');
    } else {
        response.render('index', {title: 'Coupling', buildDate: config.buildDate, gitRev: config.gitRev});
    }
}

export function welcome(request, response) {
    response.render('welcome', {
        buildDate: config.buildDate,
        gitRev: config.gitRev,
        googleClientId: config.googleClientID
    });
}

export function partials(request, response) {
    response.render('partials/' + request.params.name);
}

export function components(request, response) {
    const formatPathForViewEngine = request.path.slice(1, -5);
    response.render(formatPathForViewEngine);
}