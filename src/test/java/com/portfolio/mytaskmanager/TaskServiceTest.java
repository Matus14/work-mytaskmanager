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
import java.util.List;
import java.util.Optional;
import java.util.Vector;

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

                                    // ======= UPDATE ======

    @Test
    void update_whenTaskValid_thenUpdate_thenEntityUpdateAndStored(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("AAA");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setProjectId(6L);

        Project oldProject = Project.builder().id(1L).name("OLD PROJECT").build();

        Task existing = Task.builder()
                .id(11L)
                .title("OLD")
                .description("Old project")
                .status(Status.DELAYED)
                .dueDate(LocalDate.now().plusDays(10))
                .project(oldProject)
                .build();

        Project newProject = Project.builder().id(6L).name("New Project").build();

        when(repository.findById(11L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(6L)).thenReturn(Optional.of(newProject));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));


        TaskResponseDTO dto = service.update(11L,request);

        verify(repository).save(taskCaptor.capture());
        Task saved = taskCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(11L);
        assertThat(saved.getTitle()).isEqualTo("XXX");
        assertThat(saved.getDescription()).isEqualTo("AAA");
        assertThat(saved.getStatus()).isEqualTo(request.getStatus());
        assertThat(saved.getDueDate()).isEqualTo(request.getDueDate());
        assertThat(saved.getProject()).isNotNull();
        assertThat(saved.getProject().getId()).isEqualTo(6L);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getTitle()).isEqualTo("XXX");
        assertThat(dto.getDescription()).isEqualTo("AAA");
        assertThat(dto.getStatus()).isEqualTo(request.getStatus());
        assertThat(dto.getDueDate()).isEqualTo(request.getDueDate());
        assertThat(dto.getProjectId()).isEqualTo(6L);

        verify(repository).findById(11L);
        verify(projectRepository).findById(6L);
        verifyNoMoreInteractions(repository,projectRepository);
    }

    @Test
    void update_whenEndTimeIsInPast_thenThrowBadRequest(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("AAA");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().minusDays(1));
        request.setProjectId(6L);

        assertThatThrownBy(()-> service.update(6L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("DueDate cannot be in the past");

        verifyNoInteractions(repository,projectRepository);
    }

    @Test
    void update_whenProjectIdMissing_thenThrowBadRequest(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("AAA");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().plusDays(1));
        request.setProjectId(null);

        assertThatThrownBy(()-> service.update(6L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("ProjectId is required");


        verifyNoInteractions(repository,projectRepository);
    }

    @Test
    void update_whenProjectIdIsInvalid_thenThrowNotFound() {

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("AAA");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setProjectId(88L);

        Task existing = Task.builder()
                .id(4L)
                .title("old")
                .description("old")
                .status(Status.TODO)
                .build();

        when(repository.findById(4L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(88L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(4L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Project not found");

        verify(repository).findById(4L);
        verify(projectRepository).findById(88L);
        verify(repository, never()).save(any(Task.class));
        verifyNoMoreInteractions(repository,projectRepository);
    }

    @Test
    void update_whenTitleIsEmpty_thenThrowBadRequestAndNoRepositoryCalls(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle(" ");
        request.setDescription("AAA");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setProjectId(88L);

        assertThatThrownBy(() -> service.update(88L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("Title cannot be blank");

        verifyNoInteractions(repository,projectRepository);
    }

    @Test
    void update_whenDescriptionEmpty_thenThrowBadRequestAndNoRepositoryCall() {

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription(" ");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setProjectId(88L);

        assertThatThrownBy(() -> service.update(88L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                )
                .hasMessageContaining("Description cannot be blank");

        verifyNoInteractions(repository,projectRepository);
    }

    @Test
    void update_whenTaskIDMissing_thenThrowNotFoundAndNoSave(){

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("XXX");
        request.setDescription("OOO");
        request.setStatus(Status.TODO);
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setProjectId(88L);



        when(repository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(88L,request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Task not found");

        verify(repository).findById(44L);
        verify(repository , never()).save(any(Task.class));
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(projectRepository);

    }

                                        // ======= FIND ALL ========

    @Test
    void findAll_whenEntitiesAreStored_thenShowThemInList() {

        Task t1 = Task.builder().id(4L).title("AAAA").description("x").status(Status.TODO).build();
        Task t2 = Task.builder().id(11L).title("BBBB").description("y").status(Status.FAILED).build();

        when(repository.findAll()).thenReturn(List.of(t1,t2));

        List<TaskResponseDTO> result = service.findAll();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getId()).isEqualTo(4L);
        assertThat(result.get(0).getTitle()).isEqualTo("AAAA");
        assertThat(result.get(0).getDescription()).isEqualTo("x");
        assertThat(result.get(0).getStatus()).isEqualTo(t1.getStatus());

        assertThat(result.get(1).getId()).isEqualTo(11L);
        assertThat(result.get(1).getTitle()).isEqualTo("BBBB");
        assertThat(result.get(1).getDescription()).isEqualTo("y");
        assertThat(result.get(1).getStatus()).isEqualTo(t2.getStatus());

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_whenNoEntityAvailable_thenShowEmptyList(){

        when(repository.findAll()).thenReturn(List.of());

        List<TaskResponseDTO> result = service.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

                                      // ===== FIND BY ID =======


    @Test
    void findById_whenEntityFindByUniqueId_thenReturnDto(){

        LocalDate myDate = LocalDate.now().plusDays(10);

        Task newTask = Task.builder()
                .id(44L)
                .title("S")
                .description("s")
                .status(Status.DONE)
                .dueDate(myDate)
                .build();

        when(repository.findById(44L)).thenReturn(Optional.of(newTask));

        TaskResponseDTO dto = service.findById(44L);

        assertThat(dto.getId()).isEqualTo(44L);
        assertThat(dto.getTitle()).isEqualTo("S");
        assertThat(dto.getDescription()).isEqualTo("s");
        assertThat(dto.getStatus()).isEqualTo(Status.DONE);
        assertThat(dto.getDueDate()).isEqualTo(myDate);

        verify(repository).findById(44L);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void findById_whenNoIDExists_thenThrowNotFound(){

        when(repository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> service.findById(44L))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Task not found");


        verify(repository).findById(44L);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(projectRepository);
    }

                                        // ====== DELETE ======

    @Test
    void delete_whenEntityIdExists_thenMakeDelete() {


        when(repository.existsById(44L)).thenReturn(true);

        service.delete(44L);

        verify(repository).existsById(44L);
        verify(repository).deleteById(44L);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void delete_whenIdNotFound_thenThrowNotFound() {

        when(repository.existsById(44L)).thenReturn(false);

        assertThatThrownBy(()-> service.delete(44L))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                )
                .hasMessageContaining("Task not found for delete");

        verify(repository).existsById(44L);
        verify(repository,never()).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(projectRepository);
    }


}
