import com.example.HockeyStandings.core.match.*;
import com.example.HockeyStandings.core.match.converter.MatchToMatchViewConverter;
import com.example.HockeyStandings.core.match.web.MatchBaseReq;
import com.example.HockeyStandings.core.match.web.MatchView;
import com.example.HockeyStandings.core.team.Team;
import com.example.HockeyStandings.core.team.TeamRepo;
import com.example.HockeyStandings.core.team.web.TeamView;
import com.example.HockeyStandings.core.tournament.Tournament;
import com.example.HockeyStandings.core.tournament.TournamentRepo;
import com.example.HockeyStandings.core.tournament.web.TournamentView;
import com.example.HockeyStandings.error.EntityNotFoundException;
import com.example.HockeyStandings.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {
    @Mock
    private MatchRepo matchRepo;

    @Mock
    private MatchToMatchViewConverter matchToMatchViewConverter;

    @Mock
    private TeamRepo teamRepo;

    @Mock
    private TournamentRepo tournamentRepo;

    @Mock
    private MessageUtil messageUtil;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchService matchService;

    private Match match;
    private MatchView matchView;
    private Team homeTeam;
    private Team awayTeam;
    private Tournament tournament;


    @BeforeEach
    void setUp() {
        // Создаем команды
        homeTeam = new Team();
        homeTeam.setId(1L);
        homeTeam.setName("Hawks");

        awayTeam = new Team();
        awayTeam.setId(2L);
        awayTeam.setName("Falcons");

        // Создаем турнир
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Champions League");

        // Создаем матч
        match = new Match();
        match.setId(1L);
        match.setMatchDate(LocalDate.now());
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setTournament(tournament);
        match.setHomeScore(3);
        match.setAwayScore(2);

        // Создаем объекты TeamView для матчевых команд
        TeamView homeTeamView = new TeamView();
        homeTeamView.setName("Hawks");

        TeamView awayTeamView = new TeamView();
        awayTeamView.setName("Falcons");

        // Создаем объект MatchView
        matchView = new MatchView();
        matchView.setHomeView(homeTeamView); // Устанавливаем TeamView для домашней команды
        matchView.setAwayView(awayTeamView); // Устанавливаем TeamView для выездной команды
        matchView.setTournamentView(new TournamentView()); // Здесь можете создать и установить TournamentView по аналогии
        matchView.setHomeScore(3);
        matchView.setAwayScore(2);
    }


    @Test
    void testGetMatch_Success() {
        // Создание объектов TeamView и TournamentView для использования в MatchView
        TeamView homeTeamView = new TeamView();
        homeTeamView.setName("Hawks");

        TeamView awayTeamView = new TeamView();
        awayTeamView.setName("Falcons");

        TournamentView tournamentView = new TournamentView();
        tournamentView.setName("Champions League");

        // Устанавливаем MatchView
        matchView.setHomeView(homeTeamView);
        matchView.setAwayView(awayTeamView);
        matchView.setTournamentView(tournamentView);
        matchView.setHomeScore(3);
        matchView.setAwayScore(2);

        // Прокси для репозитория и конвертера
        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(matchToMatchViewConverter.convert(match)).thenReturn(matchView);

        MatchView result = matchService.getMatch(1L);

        assertNotNull(result);
        assertEquals("Hawks", result.getHomeView().getName());
        assertEquals("Falcons", result.getAwayView().getName());
        assertEquals("Champions League", result.getTournamentView().getName());
    }

    @Test
    void testGetMatch_NotFound() {
        when(matchRepo.findById(1L)).thenReturn(Optional.empty());
        when(messageUtil.getMessage("match.NotFound", 1L)).thenReturn("Match not found");

        assertThrows(EntityNotFoundException.class, () -> matchService.getMatch(1L));
    }

    @Test
    void testFindAllMatches() {
        // Создание объектов для каждого матча
        TeamView homeTeamView = new TeamView();
        homeTeamView.setName("Hawks");

        TeamView awayTeamView = new TeamView();
        awayTeamView.setName("Falcons");

        TournamentView tournamentView = new TournamentView();
        tournamentView.setName("Champions League");

        matchView.setHomeView(homeTeamView);
        matchView.setAwayView(awayTeamView);
        matchView.setTournamentView(tournamentView);
        matchView.setHomeScore(3);
        matchView.setAwayScore(2);

        Pageable pageable = Pageable.unpaged();
        Page<Match> matches = new PageImpl<>(Arrays.asList(match));

        when(matchRepo.findAll(pageable)).thenReturn(matches);
        when(matchToMatchViewConverter.convert(match)).thenReturn(matchView);

        Page<MatchView> result = matchService.findAllMatch(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Hawks", result.getContent().get(0).getHomeView().getName());
        assertEquals("Falcons", result.getContent().get(0).getAwayView().getName());
        assertEquals("Champions League", result.getContent().get(0).getTournamentView().getName());
    }

    @Test
    void testCreateMatch() {
        // Входной DTO-запрос
        MatchBaseReq req = new MatchBaseReq();
        req.setMatchDate(LocalDate.of(2025, 6, 5));
        req.setHomeTeamId(1L);
        req.setAwayTeamId(2L);
        req.setTourId(1L);
        req.setHomeScore(4);
        req.setAwayScore(1);

        // Исходная и сохранённая сущности
        Match match = new Match();
        Match savedMatch = new Match();
        savedMatch.setId(1L);
        savedMatch.setMatchDate(req.getMatchDate());
        savedMatch.setHomeScore(4);
        savedMatch.setAwayScore(1);

        // Возвращаемое представление
        MatchView matchView = new MatchView();
        matchView.setId(1L);
        matchView.setMatchDate(req.getMatchDate());
        matchView.setHomeScore(4);
        matchView.setAwayScore(1);

        // Настройка моков
        doAnswer(invocation -> {
            Match m = invocation.getArgument(0);
            m.setMatchDate(req.getMatchDate());
            m.setHomeScore(req.getHomeScore());
            m.setAwayScore(req.getAwayScore());
            return m;
        }).when(matchMapper).prepare(any(Match.class), eq(req));

        when(matchRepo.save(any(Match.class))).thenReturn(savedMatch);
        when(matchToMatchViewConverter.convert(savedMatch)).thenReturn(matchView);

        // Вызов
        MatchView result = matchService.create(req);

        // Проверки
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(4, result.getHomeScore());
        assertEquals(1, result.getAwayScore());
        assertEquals(req.getMatchDate(), result.getMatchDate());
    }


    @Test
    void testDeleteMatch_Success() {
        doNothing().when(matchRepo).deleteById(1L);
        assertDoesNotThrow(() -> matchService.delete(1L));
    }

    @Test
    void testDeleteMatch_NotFound() {
        doThrow(new EntityNotFoundException("Match not found"))
                .when(matchRepo).deleteById(1L);
        assertThrows(EntityNotFoundException.class, () -> matchService.delete(1L));
    }

    @Test
    void testUpdateMatch() {
        // Подготовка запроса
        MatchBaseReq req = new MatchBaseReq();
        req.setMatchDate(LocalDate.now());
        req.setHomeTeamId(1L);
        req.setAwayTeamId(2L);
        req.setTourId(1L);
        req.setHomeScore(3);
        req.setAwayScore(2);

        // Сущности
        Match match = new Match(); // исходный
        Match updated = new Match(); // после prepare()
        updated.setMatchDate(req.getMatchDate());
        updated.setHomeScore(3);
        updated.setAwayScore(2);

        Match saved = updated; // репозиторий возвращает этот

        // View
        MatchView matchView = new MatchView();
        matchView.setHomeScore(3);
        matchView.setAwayScore(2);

        TeamView homeTeamView = new TeamView();
        homeTeamView.setName("Hawks");
        matchView.setHomeView(homeTeamView);

        TeamView awayTeamView = new TeamView();
        awayTeamView.setName("Falcons");
        matchView.setAwayView(awayTeamView);

        TournamentView tournamentView = new TournamentView();
        tournamentView.setName("Champions League");
        matchView.setTournamentView(tournamentView);

        // Мокаем зависимости
        when(matchMapper.prepare(any(Match.class), any(MatchBaseReq.class))).thenReturn(updated);
        when(matchRepo.save(updated)).thenReturn(saved);
        when(matchToMatchViewConverter.convert(saved)).thenReturn(matchView);

        // Вызов
        MatchView result = matchService.update(match, req);

        // Проверки
        assertNotNull(result);
        assertEquals("Hawks", result.getHomeView().getName());
        assertEquals("Falcons", result.getAwayView().getName());
        assertEquals("Champions League", result.getTournamentView().getName());
        assertEquals(3, result.getHomeScore());
        assertEquals(2, result.getAwayScore());
    }
}
