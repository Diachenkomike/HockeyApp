import com.example.HockeyStandings.core.team.*;
import com.example.HockeyStandings.core.team.converter.TeamToTeamViewConverter;
import com.example.HockeyStandings.core.team.web.TeamBaseReq;
import com.example.HockeyStandings.core.team.web.TeamView;
import com.example.HockeyStandings.error.EntityNotFoundException;
import com.example.HockeyStandings.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    private TeamRepo teamRepo;

    @Mock
    private TeamToTeamViewConverter teamToTeamViewConverter;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private TeamView teamView;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Hawks");
        team.setOwner("John Doe");

        teamView = new TeamView();
        teamView.setName("Hawks");
    }

    @Test
    void testGetTeam_Success() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));
        when(teamToTeamViewConverter.convert(team)).thenReturn(teamView);

        TeamView result = teamService.getTeam(1L);
        assertNotNull(result);
        assertEquals("Hawks", result.getName());
    }

    @Test
    void testGetTeam_NotFound() {
        when(teamRepo.findById(1L)).thenReturn(Optional.empty());
        when(messageUtil.getMessage("team not found", 1L)).thenReturn("Team not found");

        assertThrows(EntityNotFoundException.class, () -> teamService.getTeam(1L));
    }

    @Test
    void testFindAllTeam() {
        Pageable pageable = Pageable.unpaged();
        Page<Team> teams = new PageImpl<>(Arrays.asList(team));
        when(teamRepo.findAll(pageable)).thenReturn(teams);
        when(teamToTeamViewConverter.convert(team)).thenReturn(teamView);

        Page<TeamView> result = teamService.findAllTeam(pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("Hawks", result.getContent().get(0).getName());
    }

    @Test
    void testCreateTeam() {
        TeamBaseReq req = new TeamBaseReq();
        req.setName("Hawks");
        req.setOwner("John Doe");
        when(teamRepo.save(any(Team.class))).thenReturn(team);
        when(teamToTeamViewConverter.convert(any(Team.class))).thenReturn(teamView);

        TeamView result = teamService.create(req);
        assertNotNull(result);
        assertEquals("Hawks", result.getName());
    }

    @Test
    void testDeleteTeam_Success() {
        doNothing().when(teamRepo).deleteById(1L);
        assertDoesNotThrow(() -> teamService.delete(1L));
    }

    @Test
    void testDeleteTeam_NotFound() {
        doThrow(new EntityNotFoundException("Team not found"))
                .when(teamRepo).deleteById(1L);
        assertThrows(EntityNotFoundException.class, () -> teamService.delete(1L));
    }

    @Test
    void testUpdateTeam() {
        TeamBaseReq req = new TeamBaseReq();
        req.setName("Eagles");
        req.setOwner("Jane Doe");
        when(teamRepo.save(any(Team.class))).thenReturn(team);
        when(teamToTeamViewConverter.convert(any(Team.class))).thenReturn(teamView);

        TeamView result = teamService.update(team, req);
        assertNotNull(result);
    }
}
