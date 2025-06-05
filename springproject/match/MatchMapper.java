package com.example.HockeyStandings.core.match;

import com.example.HockeyStandings.core.match.web.MatchBaseReq;
import com.example.HockeyStandings.core.team.TeamRepo;
import com.example.HockeyStandings.core.tournament.TournamentRepo;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {
    private final TeamRepo teamRepo;
    private final TournamentRepo tournamentRepo;

    public MatchMapper(TeamRepo teamRepo, TournamentRepo tournamentRepo) {
        this.teamRepo = teamRepo;
        this.tournamentRepo = tournamentRepo;
    }

    public Match prepare(Match match, MatchBaseReq req) {
        match.setMatchDate(req.getMatchDate());
        match.setHomeTeam(teamRepo.getOne(req.getHomeTeamId()));
        match.setAwayTeam(teamRepo.getOne(req.getAwayTeamId()));
        match.setTournament(tournamentRepo.getOne(req.getTourId()));
        match.setHomeScore(req.getHomeScore());
        match.setAwayScore(req.getAwayScore());
        return match;
    }
}
