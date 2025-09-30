package com.portfolio.mytaskmanager;

import com.portfolio.mytaskmanager.dto.TaskRequestDTO;
import com.portfolio.mytaskmanager.dto.TaskResponseDTO;
import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.entity.Status;
import com.portfolio.mytaskmanager.entity.Task;
import com.portfolio.mytaskmanager.repository.ProjectRepository;
import com.portfolio.mytaskmanager.repository.TaskRepository;
import com.portfolio.mytaskmanager.service.TaskService;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService service;


    @Captor
    private ArgumentCaptor<Task> taskCaptor;



                                 // ======= CREATE =======

    @Test
    void create_whenEntityIsStored_thenAllValuesAreCorrect(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("AAA");
        request.setDueDate(LocalDate.of(2025,12,10));
        request.setStatus(Status.TODO);
        request.setProjectId(6L);

        Project project = Project.builder().id(6L).name("ANY").build();
        when(projectRepository.findById(6L)).thenReturn(Optional.of(project));

        when(repository.save(any(Task.class)))
                .thenAnswer(inv -> {
                    Task t = inv.getArgument(0);
                    t.setId(5L);
                    return t;
                });

        TaskResponseDTO dto = service.create(request);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getTitle()).isEqualTo("XXX");
        assertThat(dto.getDescription()).isEqualTo("AAA");
        assertThat(dto.getDueDate()).isEqualTo(request.getDueDate());
        assertThat(dto.getStatus()).isEqualTo(request.getStatus());
        assertThat(dto.getProjectId()).isEqualTo(6L);


        verify(projectRepository).findById(6L);
        verify(repository).save(any(Task.class));
        verifyNoMoreInteractions(repository, projectRepository);
    }

    @Test
    void create_whenProjectIdInvalid_thenThrowNotFoundAndNoSave() {

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("AAA");
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setStatus(Status.TODO);
        request.setProjectId(5L);

        when(projectRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Project not found");

        verify(projectRepository).findById(5L);
        verify(repository, never()).save(any(Task.class));
        verifyNoMoreInteractions(repository, projectRepository);
    }

    @Test
    void create_whenTitleIsBlank_thenThrowBadRequest(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle(" ");
        request.setDescription("AAA");
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setStatus(Status.TODO);
        request.setProjectId(5L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("Title cannot be blank");


        verifyNoMoreInteractions(repository, projectRepository);
    }

    @Test
    void create_whenDescriptionIsBlank_thenThrowBadRequest() {

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("AAA");
        request.setDescription("  ");
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setStatus(Status.TODO);
        request.setProjectId(5L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("Description cannot be blank");

        verifyNoMoreInteractions(repository, projectRepository);
    }

    @Test
    void create_whenProjectIdMissing_thenBadRequestAndNoRepositoryCalls() {

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Build UI");
        request.setDescription("Implement feature X");
        request.setDueDate(LocalDate.now().plusDays(3));
        request.setStatus(Status.TODO);
        request.setProjectId(null);


        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("projectId is required");


        verifyNoInteractions(repository, projectRepository);
    }

    @Test
    void create_whenDueDateInPast_thenBadRequestAndNoRepositoryCalls(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Build UI");
        request.setDescription("Implement feature X");
        request.setDueDate(LocalDate.now().minusDays(1));
        request.setStatus(Status.TODO);
        request.setProjectId(5L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("dueDate cannot be in the past");

        verifyNoInteractions(repository, projectRepository);
    }

    @Test
    void create_whenSavingTask_thenEntityHasThatProject_andDtoHasSameIdCaptorUse(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Build UI");
        request.setDescription("Implement feature X");
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setStatus(Status.TODO);
        request.setProjectId(5L);

        Project project = Project.builder().id(5L).name("XXX").build();

        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));
        when(repository.save(any(Task.class)))
                .thenAnswer(inv -> {
                    Task t = inv.getArgument(0);
                    t.setId(5L);
                    return t;
                });

        TaskResponseDTO dto = service.create(request);

        verify(repository).save(taskCaptor.capture());
        Task saved = taskCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getTitle()).isEqualTo("Build UI");
        assertThat(saved.getDescription()).isEqualTo("Implement feature X");
        assertThat(saved.getDueDate()).isEqualTo(request.getDueDate());
        assertThat(saved.getStatus()).isEqualTo(Status.TODO);
        assertThat(saved.getProject()).isNotNull();
        assertThat(saved.getProject().getId()).isEqualTo(5L);


        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getTitle()).isEqualTo("Build UI");
        assertThat(dto.getDescription()).isEqualTo("Implement feature x");
        assertThat(dto.getDueDate()).isEqualTo(request.getDueDate());
        assertThat(dto.getStatus()).isEqualTo(Status.TODO);
        assertThat(dto.getProjectId()).isEqualTo(5L);

        verify(projectRepository).findById(5L);
        verifyNoMoreInteractions(repository, projectRepository);
    }

}
