import {module} from "angular";
import * as template from "./tribe-config.pug";
import PairingRule from "../../../../common/PairingRule";
import * as flip from "ramda/src/flip";
import * as merge from "ramda/src/merge";

import * as styles from './styles.css'
import {Coupling} from "../../services";
import Tribe from "../../../../common/Tribe";

const defaults = flip(merge);

export class TribeConfigController {
    static $inject = ['$location', 'Coupling', '$scope'];
    public tribe: Tribe;
    public isNew: boolean;
    public styles: any;
    public pairingRules;

    constructor(public $location: angular.ILocationService, public Coupling: Coupling, public $scope) {
        this.styles = styles;
        this.pairingRules = [
            {id: PairingRule.LongestTime, description: "Prefer Longest Time"},
            {id: PairingRule.PreferDifferentBadge, description: "Prefer Different Badges (Beta)"},
        ];
    }

    $onInit() {
        this.tribe = defaults(this.tribe, {
            pairingRule: PairingRule.LongestTime,
            defaultBadgeName: 'Default',
            alternateBadgeName: 'Alternate',
        });
    }

    async clickSaveButton() {
        await Coupling.saveTribe(this.tribe);
        this.$location.path("/tribes");
        this.$scope.$apply()
    }

    async removeTribe() {
        await Coupling.deleteTribe(this.tribe.id);
        this.$location.path("/tribes");
        this.$scope.$apply()
    }
}

export default module("coupling.tribeConfig", [])
    .controller('TribeConfigController', TribeConfigController)
    .directive('tribeConfig', function () {
        return {
            controller: 'TribeConfigController',
            controllerAs: 'self',
            bindToController: true,
            scope: {
                tribe: '=tribe',
                isNew: '=isNew'
            },
            restrict: 'E',
            template: template
        }
    });