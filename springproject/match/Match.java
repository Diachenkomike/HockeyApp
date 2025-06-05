package com.example.HockeyStandings.core.match;

import com.example.HockeyStandings.core.team.Team;
import com.example.HockeyStandings.core.tournament.Tournament;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "hockey_match")
public class Match {
    @Id
    @Column(name = "id")
    @GenericGenerator(
            name = "hockey_match_id_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name",value = "hockey_match_id_seq"),
                    @org.hibernate.annotations.Parameter(name = "INCREMENT",value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MINVALUE",value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MAXVALUE",value = "2147483647"),
                    @org.hibernate.annotations.Parameter(name = "CACHE",value = "1")
            }
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "hockey_match_id_seq")
    private Long id;
    @Column(name = "match_date")
    private LocalDate matchDate;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "homeid")
    private Team homeTeam;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "awayid")
    private Team awayTeam;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tourid")
    private Tournament tournament;
    @Column(name = "scorehome")
    private int homeScore;
    @Column(name = "scoreaway")
    private int awayScore;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public LocalDate getMatchDate(){
        return matchDate;
    }
    public void setMatchDate(LocalDate matchDate){
        this.matchDate=matchDate;
    }
    public Tournament getTournament(){
        return tournament;
    }
    public void setTournament(Tournament tournament){
        this.tournament=tournament;
    }
    public Team getHomeTeam(){
        return homeTeam;
    }
    public void setHomeTeam(Team homeTeam){
        this.homeTeam=homeTeam;
    }
    public Team getAwayTeam(){
        return awayTeam;
    }
    public void setAwayTeam(Team awayTeam){
        this.awayTeam=awayTeam;
    }
    public int getHomeScore(){
        return homeScore;
    }
    public void setHomeScore(int homeScore){
        this.homeScore=homeScore;
    }
    public int getAwayScore(){
        return awayScore;
    }
    public void setAwayScore(int awayScore){
        this.awayScore=awayScore;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", matchDate=" + matchDate +
                ", homeTeam=" + homeTeam +
                ", awayTeam=" + awayTeam +
                ", tournament=" + tournament +
                ", homeScore=" + homeScore +
                ", awayScore=" + awayScore +
                '}';
    }
}

