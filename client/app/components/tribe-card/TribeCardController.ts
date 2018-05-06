import * as template from "./tribe-card.pug";
import * as styles from './styles.css'
import {module} from "angular";
import IController = angular.IController;
import Tribe from "../../../../common/Tribe";

export class TribeCardController implements IController {
    static $inject = ['$location'];
    public tribe: Tribe;
    size: number;
    maxFontHeight: number;
    minFontHeight: number;
    cardStyle: any;
    headerStyle: any;
    styles: any;

    constructor(public $location: angular.ILocationService) {
        this.styles = styles;
    }

    $onInit() {
        if (!this.size) {
            this.size = 150;
        }
        const pixelWidth = this.size;
        const pixelHeight = (this.size * 1.4);
        const paddingAmount = (this.size * 0.02);

        const borderAmount = (this.size * 0.01);
        this.maxFontHeight = (this.size * 0.15);
        this.minFontHeight = (this.size * 0.16);
        this.cardStyle = {
            width: `${pixelWidth}px`,
            height: `${pixelHeight}px`,
            padding: `${paddingAmount}px`,
            'border-width': `${borderAmount}px`,
        };
        const headerMargin = (this.size * 0.02);
        const maxHeaderHeight = this.size * 0.35;
        this.headerStyle = {
            margin: `${headerMargin}px 0 0 0`,
            'height': `${maxHeaderHeight}px`
        };
    }

    clickOnTribeCard() {
        this.$location.path("/" + this.tribe.id + "/pairAssignments/current");
    }

    clickOnTribeName($event) {
        if ($event.stopPropagation) $event.stopPropagation();
        this.$location.path("/" + this.tribe.id + '/edit/');
    }
}

export default module('coupling.tribeCard', [])
    .controller('TribeCardController', TribeCardController)
    .directive('tribecard', function () {
        return {
            controller: 'TribeCardController',
            controllerAs: 'tribecard',
            scope: {
                tribe: '=',
                size: '=?'
            },
            bindToController: true,
            restrict: 'E',
            template: template
        }
    });