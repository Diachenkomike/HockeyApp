//package com.example.HockeyStandings.core.tournament;

import com.example.HockeyStandings.core.tournament.Tournament;
import com.example.HockeyStandings.core.tournament.TournamentRepo;
import com.example.HockeyStandings.core.tournament.TournamentService;
import com.example.HockeyStandings.core.tournament.converter.TournamentToTournamentViewConverter;
import com.example.HockeyStandings.core.tournament.web.TournamentBaseReq;
import com.example.HockeyStandings.core.tournament.web.TournamentView;
import com.example.HockeyStandings.error.EntityNotFoundException;
import com.example.HockeyStandings.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepo tournamentRepo;

    @Mock
    private TournamentToTournamentViewConverter tournamentToTournamentViewConverter;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament tournament;
    private TournamentView tournamentView;
    private TournamentBaseReq tournamentBaseReq;

    @BeforeEach
    void setUp() {
        // Setup test data
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Champions League");
        tournament.setYear(2025);

        tournamentView = new TournamentView();
        tournamentView.setId(1L);
        tournamentView.setName("Champions League");
        tournamentView.setYear(2025);

        tournamentBaseReq = new TournamentBaseReq();
        tournamentBaseReq.setName("Champions League");
        tournamentBaseReq.setYear(2025);
    }

    @Test
    void createTournament_ShouldReturnTournamentView() {
        // Arrange
        when(tournamentRepo.save(any(Tournament.class))).thenReturn(tournament);
        when(tournamentToTournamentViewConverter.convert(any(Tournament.class))).thenReturn(tournamentView);

        // Act
        TournamentView result = tournamentService.create(tournamentBaseReq);

        // Assert
        assertNotNull(result);
        assertEquals("Champions League", result.getName());
        assertEquals(2025, result.getYear());
        verify(tournamentRepo).save(any(Tournament.class));
    }

    @Test
    void getTournament_ShouldReturnTournamentView() {
        // Arrange
        when(tournamentRepo.findById(1L)).thenReturn(java.util.Optional.of(tournament));
        when(tournamentToTournamentViewConverter.convert(any(Tournament.class))).thenReturn(tournamentView);

        // Act
        TournamentView result = tournamentService.getTournament(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Champions League", result.getName());
        assertEquals(2025, result.getYear());
        verify(tournamentRepo).findById(1L);
    }

    @Test
    void getTournament_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(tournamentRepo.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tournamentService.getTournament(1L));
        verify(tournamentRepo).findById(1L);
    }

    @Test
    void updateTournament_ShouldReturnUpdatedTournamentView() {
        // Arrange
        when(tournamentRepo.save(any(Tournament.class))).thenReturn(tournament);
        when(tournamentToTournamentViewConverter.convert(any(Tournament.class))).thenReturn(tournamentView);

        // Act
        TournamentView result = tournamentService.update(tournament, tournamentBaseReq);

        // Assert
        assertNotNull(result);
        assertEquals("Champions League", result.getName());
        assertEquals(2025, result.getYear());
        verify(tournamentRepo).save(any(Tournament.class));
    }

    @Test
    void deleteTournament_ShouldDeleteTournament() {
        // Arrange
        doNothing().when(tournamentRepo).deleteById(1L);

        // Act
        tournamentService.delete(1L);

        // Assert
        verify(tournamentRepo).deleteById(1L);
    }

    @Test
    void deleteTournament_ShouldThrowEntityNotFoundException_WhenTournamentNotFound() {
        // Arrange
        doThrow(EmptyResultDataAccessException.class).when(tournamentRepo).deleteById(1L);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tournamentService.delete(1L));
        verify(tournamentRepo).deleteById(1L);
    }

    @Test
    void findAllTournament_ShouldReturnPageOfTournamentView() {
        // Arrange
        List<Tournament> tournaments = Collections.singletonList(tournament);
        Page<Tournament> page = new PageImpl<>(tournaments, PageRequest.of(0, 10), tournaments.size());
        when(tournamentRepo.findAll(any(Pageable.class))).thenReturn(page);
        when(tournamentToTournamentViewConverter.convert(any(Tournament.class))).thenReturn(tournamentView);

        // Act
        Page<TournamentView> result = tournamentService.findAllTournament(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(tournamentRepo).findAll(any(Pageable.class));
    }
}
