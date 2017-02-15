export default class ReportProvider {
    constructor(public pairingHistory: any) {
    }

    getPairHistoryReports(players) {
        const allReports = [];

        players.forEach(player => {
            const candidates = players.filter(function (otherPlayer) {
                return otherPlayer !== player && otherPlayer.badge !== player.badge;
            });

            if(candidates.length > 0){
              const pairCandidateReport = this.pairingHistory.getPairCandidateReport(player, candidates);
              allReports.push(pairCandidateReport);
            }
        });

        if(allReports.length === 0){
          players.forEach(player => {
              const candidates = players.filter(function (otherPlayer) {
                  return otherPlayer !== player;
              });

              const pairCandidateReport = this.pairingHistory.getPairCandidateReport(player, candidates);
              allReports.push(pairCandidateReport);
          });
        }

        return allReports;
    }
}