var filters = angular.module("coupling.filters", []);
filters.filter('gravatarUrl', function (gravatarService) {
    return function (player, options) {
        if (player && player.image) {
            return "/images/icons/players/" + player.image;
        } else {
            var email = player && player.email ? player.email : "";
            return gravatarService.url(email, options);
        }
    }
});

filters.filter('tribeImageUrl', function (gravatarService) {
    return function (tribe, options) {
        if (tribe) {
            if (tribe.imageURL) {
                return tribe.imageURL;
            } else if (tribe.email) {
                return gravatarService.url(tribe.email, options);
            }
        }
        return "/images/icons/tribes/no-tribe.png";
    }
});