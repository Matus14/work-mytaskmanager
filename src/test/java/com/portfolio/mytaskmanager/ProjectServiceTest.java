package com.portfolio.mytaskmanager;

import com.portfolio.mytaskmanager.dto.ProjectRequestDTO;
import com.portfolio.mytaskmanager.dto.ProjectResponseDTO;
import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.repository.ProjectRepository;
import com.portfolio.mytaskmanager.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @InjectMocks
    private ProjectService service;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;



                                // ====== CREATE ======

    @Test
    void create_whenCreate_thenEntityIsSavedAndDtoReturned(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("My first Project");
        request.setStartDate(LocalDate.of(2025,11,10));
        request.setEndDate(LocalDate.of(2025,12,1));
        request.setDescription("Shopping list");

        when(repository.existsByNameIgnoreCase("My first Project")).thenReturn(false);
        when(repository.save(any(Project.class)))
                .thenAnswer(inv -> {
                    Project p = inv.getArgument(0);
                    p.setId(5L);
                    return p;
                });

        ProjectResponseDTO dto = service.create(request);
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("My first Project");
        assertThat(dto.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(dto.getDescription().trim()).isEqualTo(request.getDescription());

        verify(repository).existsByNameIgnoreCase("My first Project");
        verify(repository).save(any(Project.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void create_whenNameIsEmpty_throwBadRequest(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName(" ");
        request.setStartDate(LocalDate.of(2025,11,10));
        request.setEndDate(LocalDate.of(2025,12,1));
        request.setDescription("Shopping list");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Name must be filled in");

        verify(repository, never()).save(any(Project.class));
        verifyNoInteractions(repository);
    }

    @Test
    void create_whenNameIsNull_throwBadRequest() {

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName(null);
        request.setStartDate(LocalDate.of(2025,11,10));
        request.setEndDate(LocalDate.of(2025,12,1));
        request.setDescription("Shopping list");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Name must be filled in");

        verify(repository, never()).save(any(Project.class));
        verifyNoInteractions(repository);
    }

    @Test
    void create_whenEndDateIsBeforeStartDate_thenThrowBadRequest(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("My first Project");
        request.setStartDate(LocalDate.of(2025,12,10));
        request.setEndDate(LocalDate.of(2025,11,10));
        request.setDescription("Shopping list");

        assertThatThrownBy(()-> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("endDate must be after startDate");

        verifyNoInteractions(repository);
    }

    @Test
    void create_whenStartDateIsSameAsEndDate_throwBadRequest(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("My first Project");
        request.setStartDate(LocalDate.of(2025,12,10));
        request.setEndDate(LocalDate.of(2025,12,10));
        request.setDescription("Shopping list");

        assertThatThrownBy(()-> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("endDate must be after startDate");

        verifyNoInteractions(repository);

    }

    @Test
    void create_whenProjectAlreadyExists_thenThrowConflict(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("My first Project");
        request.setStartDate(LocalDate.of(2025,11,10));
        request.setEndDate(LocalDate.of(2025,12,10));
        request.setDescription("Shopping list");

        when(repository.existsByNameIgnoreCase("My first Project")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT)
                )
                .hasMessageContaining("Project name already exists");

        verify(repository).existsByNameIgnoreCase("My first Project");
        verify(repository, never()).save(any(Project.class));
        verifyNoMoreInteractions(repository);
    }


    @Test
    void create_whenEntityIsCreatedAllDataAreStoredCorrectlyAsDto(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("My first Project");
        request.setStartDate(LocalDate.of(2025,11,10));
        request.setEndDate(LocalDate.of(2025,12,10));
        request.setDescription("Shopping list");

        when(repository.existsByNameIgnoreCase("My first Project")).thenReturn(false);
        when(repository.save(any(Project.class)))
                .thenAnswer(inv -> {
                    Project p = inv.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ProjectResponseDTO dto = service.create(request);

        verify(repository).existsByNameIgnoreCase("My first Project");
        verify(repository).save(projectCaptor.capture());
        Project pro = projectCaptor.getValue();

        assertThat(pro.getId()).isEqualTo(1L);
        assertThat(pro.getName()).isEqualTo("My first Project");
        assertThat(pro.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(pro.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(pro.getDescription()).isEqualTo(request.getDescription());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("My first Project");
        assertThat(dto.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
    }

                             // ====== FIND ALL =====

    @Test
    void findAll_whenEntitiesAreStored_thenShowAllOfThemInList(){

        Project p1 = Project.builder().id(4L)
                .name("P one")
                .startDate(LocalDate.of(2025,10,10))
                .endDate(LocalDate.of(2025,11,11))
                .description("Shopping list")
                .build();

        Project p2 = Project.builder().id(5L)
                .name("P two")
                .startDate(LocalDate.of(2025,10,5))
                .endDate(LocalDate.of(2025,11,6))
                .description("Itinerary")
                .build();

        when(repository.findAll()).thenReturn(List.of(p1,p2));

        List <ProjectResponseDTO> results = service.findAll();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(4L);
        assertThat(results.get(0).getName()).isEqualTo("P one");
        assertThat(results.get(0).getStartDate()).isEqualTo(p1.getStartDate());
        assertThat(results.get(0).getEndDate()).isEqualTo(p1.getEndDate());
        assertThat(results.get(0).getDescription()).isEqualTo(p1.getDescription());

        assertThat(results.get(1).getId()).isEqualTo(5L);
        assertThat(results.get(1).getName()).isEqualTo("P two");
        assertThat(results.get(1).getStartDate()).isEqualTo(p2.getStartDate());
        assertThat(results.get(1).getEndDate()).isEqualTo(p2.getEndDate());
        assertThat(results.get(1).getDescription()).isEqualTo(p2.getDescription());


        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_whenListIsEmpty_showList(){

        when(repository.findAll()).thenReturn(List.of());

        List <ProjectResponseDTO> result = service.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }


                                        // ======= FIND BY ID ======


    @Test
    void findById_whenEntityExistsByUniqueId_thenReturnDto() {

        Project p1 = Project.builder().id(4L)
                .name("P one")
                .startDate(LocalDate.of(2025,10,10))
                .endDate(LocalDate.of(2025,11,11))
                .description("Shopping list")
                .build();

        when(repository.findById(4L)).thenReturn(Optional.of(p1));

        ProjectResponseDTO dto = service.findById(4L);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getName()).isEqualTo("P one");
        assertThat(dto.getStartDate()).isEqualTo(p1.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(p1.getEndDate());
        assertThat(dto.getDescription()).isEqualTo(p1.getDescription());


        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);

    }

    @Test
    void findById_whenIdIsInvalid_thenThrowNotFound(){

        when(repository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> service.findById(4L))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Id not found");

        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

                            // ===== UPDATE =======


    @Test
    void update_ifExistingIdAndValidRequest_whenUpdate_thenFieldsAreUpdatedAndDtoReturned(){

        Project existing = Project.builder()
                .id(1L)
                .name("Old")
                .description("Old description")
                .startDate(LocalDate.of(2025,10,2))
                .endDate(LocalDate.of(2025,11,10))
                .build();

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName(" New name");
        request.setDescription("New description");
        request.setStartDate(LocalDate.of(2025,2,14));
        request.setEndDate(LocalDate.of(2025,3,15));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));


        ProjectResponseDTO dto = service.update(1L,request);

        verify(repository).findById(1L);
        verify(repository.save(projectCaptor.capture()));
        verifyNoMoreInteractions(repository);

        Project save = projectCaptor.getValue();
        assertThat(save.getId()).isEqualTo(1L);
        assertThat(save.getName()).isEqualTo("New name");
        assertThat(save.getDescription()).isEqualTo("New description");
        assertThat(save.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(save.getEndDate()).isEqualTo(request.getEndDate());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("New name");
        assertThat(dto.getDescription()).isEqualTo("New description");
        assertThat(dto.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(request.getEndDate());

    }

    @Test
    void update_whenIdNotValid_thenThrowNotFound(){

        when(repository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(4L,new ProjectRequestDTO()))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Project not found");

        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_nameIsEmpty_ThrowBadRequest() {

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("  ");
        request.setDescription("xyz");
        request.setStartDate(LocalDate.of(2025,10,5));
        request.setEndDate(LocalDate.of(2025,11,15));

        assertThatThrownBy(()-> service.update(10L, request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("Name must be filled in");

        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_checkEndTimeBeforeStartTime_thenThrowBadRequest(){

        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("AAA");
        request.setDescription("xyz");
        request.setStartDate(LocalDate.of(2025,10,5));
        request.setEndDate(LocalDate.of(2025,9,15));

        assertThatThrownBy(() -> service.update(5L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("endDate must be after startDate");

        verifyNoInteractions(repository);

    }

                                        // ====== DELETE ======



    @Test
    void delete_whenIdExists_thenMakeDelete(){

        when(repository.existsById(5L)).thenReturn(true);

        service.delete(5L);

        verify(repository).existsById(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void delete_whenIdInvalid_thenThrowNotFound() {

        when(repository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(5L))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Not found for delete");

        verify(repository).existsById(5L);
        verify(repository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

}
