package com.productivity.tracker.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    private LocalDate dueDate;
    
    private LocalDate completedDate;
    
    public LocalDate getCompletedDate() {
		return completedDate;
	}



	public void setCompletedDate(LocalDate completedDate) {
		this.completedDate = completedDate;
	}



	@PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private ProductivityWorkspace workspace;

    private Integer workedMinutes = 0;
  
	public Integer getWorkedMinutes() {
		return workedMinutes;
	}



	public void setWorkedMinutes(Integer workedMinutes) {
		this.workedMinutes = workedMinutes;
	}



	public Task() {
    }
	
	

	public ProductivityWorkspace getWorkspace() {
		return workspace;
	}



	public void setWorkspace(ProductivityWorkspace workspace) {
		this.workspace = workspace;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	

    
}