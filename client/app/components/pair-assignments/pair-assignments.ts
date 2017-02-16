import * as _ from "underscore";
import * as template from "./pair-assignments.pug";
import Tribe from "../../../../common/Tribe";
import PairAssignmentSet from "../../../../common/PairAssignmentSet";
import Player from "../../../../common/Player";

export class PairAssignmentsController {
    static $inject = ['Coupling', '$location'];
    tribe: Tribe;
    players: Player[];
    pairAssignments: PairAssignmentSet;
    isNew: boolean;
    private _unpairedPlayers: Player[];

    constructor(public Coupling, private $location) {
    }

    get unpairedPlayers(): Player[] {
        if (this._unpairedPlayers) {
            return this._unpairedPlayers;
        } else {
            this._unpairedPlayers = this.findUnpairedPlayers(this.players, this.pairAssignments);
            return this._unpairedPlayers;
        }
    }

    save() {
        const self = this;
        this.Coupling.saveCurrentPairAssignments(this.pairAssignments)
            .then(function () {
                self.$location.path("/" + self.tribe.id + "/pairAssignments/current");
            });
    }

    onDrop(draggedPlayer, droppedPlayer) {
        const pairWithDraggedPlayer = this.findPairContainingPlayer(draggedPlayer, this.pairAssignments.pairs);
        const pairWithDroppedPlayer = this.findPairContainingPlayer(droppedPlayer, this.pairAssignments.pairs);

        if (pairWithDraggedPlayer != pairWithDroppedPlayer) {
            this.swapPlayers(pairWithDraggedPlayer, draggedPlayer, droppedPlayer);
            this.swapPlayers(pairWithDroppedPlayer, droppedPlayer, draggedPlayer);
        }
    }

    private findPairContainingPlayer(player, pairs: Player[][]) {
        return _.find(pairs, function (pair) {
            return _.findWhere(pair, {
                _id: player._id
            });
        });
    }


    private swapPlayers(pair, swapOutPlayer, swapInPlayer) {
        _.each(pair, function (player: Player, index) {
            if (swapOutPlayer._id === player._id) {
                pair[index] = swapInPlayer;
            }
        });
    }

    private findUnpairedPlayers(players: Player[], pairAssignmentDocument: PairAssignmentSet): Player[] {
        if (!pairAssignmentDocument) {
            return players;
        }
        const currentlyPairedPlayers = _.flatten(pairAssignmentDocument.pairs);
        return _.filter(players, function (value: Player) {
            const found = _.findWhere(currentlyPairedPlayers, {_id: value._id});
            return found == undefined;
        });
    }
}

export default angular.module('coupling.pairAssignments', [])
    .controller('PairAssignmentsController', PairAssignmentsController)
    .directive('pairAssignments', () => {
        return {
            controller: 'PairAssignmentsController',
            controllerAs: 'pairAssignments',
            bindToController: {
                tribe: '=',
                players: '=',
                pairAssignments: '=pairs',
                isNew: '='
            },
            restrict: 'E',
            template: template
        }
    });
