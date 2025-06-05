import com.example.HockeyStandings.core.player.*;
import com.example.HockeyStandings.core.player.converter.PlayerToPlayerViewConverter;
import com.example.HockeyStandings.core.player.web.PlayerBaseReq;
import com.example.HockeyStandings.core.player.web.PlayerView;
import com.example.HockeyStandings.core.team.Team;
import com.example.HockeyStandings.core.team.TeamRepo;
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
class PlayerServiceTest {
    @Mock
    private PlayerRepo playerRepo;

    @Mock
    private TeamRepo teamRepo;

    @Mock
    private PlayerToPlayerViewConverter playerToPlayerViewConverter;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private PlayerService playerService;

    private Player player;
    private PlayerView playerView;
    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Hawks");

        player = new Player();
        player.setId(1L);
        player.setName("John Doe");
        player.setAge(25);
        player.setTeam(team);

        playerView = new PlayerView();
        playerView.setName("John Doe");
    }

    @Test
    void testGetPlayer_Success() {
        when(playerRepo.findById(1L)).thenReturn(Optional.of(player));
        when(playerToPlayerViewConverter.convert(player)).thenReturn(playerView);

        PlayerView result = playerService.getPlayer(1L);
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testGetPlayer_NotFound() {
        when(playerRepo.findById(1L)).thenReturn(Optional.empty());
        when(messageUtil.getMessage("player.NotFound", 1L)).thenReturn("Player not found");

        assertThrows(EntityNotFoundException.class, () -> playerService.getPlayer(1L));
    }

    @Test
    void testFindAllPlayers() {
        Pageable pageable = Pageable.unpaged();
        Page<Player> players = new PageImpl<>(Arrays.asList(player));
        when(playerRepo.findAll(pageable)).thenReturn(players);
        when(playerToPlayerViewConverter.convert(player)).thenReturn(playerView);

        Page<PlayerView> result = playerService.findAllPlayer(pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
    }

    @Test
    void testCreatePlayer() {
        PlayerBaseReq req = new PlayerBaseReq();
        req.setName("John Doe");
        req.setAge(25);
        req.setTeam(1L);

        when(teamRepo.getOne(1L)).thenReturn(team);
        when(playerRepo.save(any(Player.class))).thenReturn(player);
        when(playerToPlayerViewConverter.convert(any(Player.class))).thenReturn(playerView);

        PlayerView result = playerService.create(req);
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testDeletePlayer_Success() {
        doNothing().when(playerRepo).deleteById(1L);
        assertDoesNotThrow(() -> playerService.delete(1L));
    }

    @Test
    void testDeletePlayer_NotFound() {
        doThrow(new EntityNotFoundException("Player not found"))
                .when(playerRepo).deleteById(1L);
        assertThrows(EntityNotFoundException.class, () -> playerService.delete(1L));
    }

    @Test
    void testUpdatePlayer() {
        PlayerBaseReq req = new PlayerBaseReq();
        req.setName("Jane Doe");
        req.setAge(26);
        req.setTeam(1L);

        when(teamRepo.getOne(1L)).thenReturn(team);
        when(playerRepo.save(any(Player.class))).thenReturn(player);
        when(playerToPlayerViewConverter.convert(any(Player.class))).thenReturn(playerView);

        PlayerView result = playerService.update(player, req);
        assertNotNull(result);
    }
}