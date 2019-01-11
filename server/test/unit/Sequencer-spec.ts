import Sequencer from "../../lib/Sequencer";
import PairHistoryReport from "../../lib/PairCandidateReport";
import PairingRule from "../../../common/PairingRule";

// @ts-ignore
import {actionDispatcherMock} from "engine_test"


describe('Sequencer', function () {

    const bill = {_id: "Bill", tribe: ''};
    const ted = {_id: "Ted", tribe: ''};
    const amadeus = {_id: "Mozart", tribe: ''};
    const shorty = {_id: "Napoleon", tribe: ''};

    it('will use the Pairing History to produce a wheel spin sequence in order of longest time since paired to shortest', function () {
        const players = [bill, ted, amadeus, shorty];

        const billsPairCandidates = new PairHistoryReport(bill, [], 3);
        const tedsPairCandidates = new PairHistoryReport(ted, [], 7);
        const amadeusPairCandidates = new PairHistoryReport(amadeus, [], 4);
        const shortyPairCandidates = new PairHistoryReport(shorty, [], 5);

        const reportProvider = {pairingHistory: {historyDocuments: []}};

        const mock = actionDispatcherMock();
        mock.setPairCandidateReportsToReturn(
            [billsPairCandidates, tedsPairCandidates, amadeusPairCandidates, shortyPairCandidates]
        )

        const sequencer = new Sequencer(reportProvider, mock);

        const next = sequencer.getNextInSequence(players, PairingRule.LongestTime);

        expect(next).toEqual(tedsPairCandidates);
    });

    it('a person who just paired has lower priority than someone who has not paired in a long time', function () {
        const players = [bill, ted, amadeus, shorty];

        const amadeusPairCandidates = new PairHistoryReport(amadeus, [], 5);
        const shortyPairCandidates = new PairHistoryReport(shorty, [], 0);

        let reports = [amadeusPairCandidates, shortyPairCandidates];

        const reportProvider = {pairingHistory: {historyDocuments: []}};
        const mock = actionDispatcherMock();
        mock.setPairCandidateReportsToReturn(reports);

        const sequencer = new Sequencer(reportProvider, mock);

        const next = sequencer.getNextInSequence(players, PairingRule.LongestTime);

        expect(next).toEqual(amadeusPairCandidates);
    });

    it('will use the Pairing History to produce a wheel spin sequence in order of longest time since paired to shortest', function () {
        const players = [bill, amadeus, shorty];

        const billsPairCandidates = new PairHistoryReport(bill, [], 3);
        const amadeusPairCandidates = new PairHistoryReport(amadeus, [], 4);
        const shortyPairCandidates = new PairHistoryReport(shorty, [], 5);

        let reports = [billsPairCandidates, amadeusPairCandidates, shortyPairCandidates];
        const reportProvider = {pairingHistory: {historyDocuments: []}};
        const mock = actionDispatcherMock();
        mock.setPairCandidateReportsToReturn(reports);
        const sequencer = new Sequencer(reportProvider, mock);

        const next = sequencer.getNextInSequence(players, PairingRule.LongestTime);
        expect(next).toEqual(shortyPairCandidates);
    });

    it('will use the Pairing History to get the next in sequence for when a player has never paired.', function () {
        const players = [bill, amadeus, shorty];

        const billsPairCandidates = new PairHistoryReport(bill, [], 3);
        const amadeusPairCandidates = new PairHistoryReport(amadeus, [], 4);
        const shortyPairCandidates = new PairHistoryReport(shorty, [], undefined);

        let reports = [billsPairCandidates, amadeusPairCandidates, shortyPairCandidates];
        const reportProvider = {pairingHistory: {historyDocuments: []}};
        const mock = actionDispatcherMock();
        mock.setPairCandidateReportsToReturn(reports);
        const sequencer = new Sequencer(reportProvider, mock);

        const next = sequencer.getNextInSequence(players, PairingRule.LongestTime);
        expect(next).toEqual(shortyPairCandidates);
    });

    it('will prioritize the report with fewest players when equal amounts of time.', function () {
        const players = [bill, amadeus, shorty];

        const billsPairCandidates = new PairHistoryReport(bill, [
            {_id: '', tribe: ''},
            {_id: '', tribe: ''},
            {_id: '', tribe: ''}
        ], undefined);
        const amadeusPairCandidates = new PairHistoryReport(amadeus, [
            {_id: '', tribe: ''}
        ], undefined);
        const shortyPairCandidates = new PairHistoryReport(shorty, [
            {_id: '', tribe: ''}, {_id: '', tribe: ''}
        ], undefined);

        let reports = [billsPairCandidates, amadeusPairCandidates, shortyPairCandidates];
        const reportProvider = {pairingHistory: {historyDocuments: []}};
        const mock = actionDispatcherMock();
        mock.setPairCandidateReportsToReturn(reports);
        const sequencer = new Sequencer(reportProvider, mock);
        const next = sequencer.getNextInSequence(players, PairingRule.LongestTime);
        expect(next).toEqual(amadeusPairCandidates);
    });
});
