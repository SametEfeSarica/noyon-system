package com.noyon.system.service;

import com.noyon.system.dto.board.BoardDtos.*;
import com.noyon.system.entity.*;
import com.noyon.system.exception.ResourceNotFoundException;
import com.noyon.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final TaskColumnRepository columnRepository;
    private final ProjectTaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ChecklistItemRepository checklistItemRepository;

    // getBoard — workspaceId parametresi ekle
    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long userId, Long workspaceId) {
        List<TaskColumn> columns;

        if (workspaceId != null) {
            columns = columnRepository.findByUserIdAndWorkspaceIdOrderByPositionAsc(userId, workspaceId);
        } else {
            columns = columnRepository.findByUserIdOrderByPositionAsc(userId);
        }

        List<ColumnResponse> columnDtos = columns.stream()
                .map(this::toColumnResponse)
                .collect(Collectors.toList());

        Set<User> memberSet = new LinkedHashSet<>();
        for (TaskColumn col : columns) {
            if (col.getCards() != null) {
                for (ProjectTask card : col.getCards()) {
                    if (card.getAssignees() != null) memberSet.addAll(card.getAssignees());
                }
            }
        }

        return BoardResponse.builder()
                .columns(columnDtos)
                .members(memberSet.stream().map(this::toAssigneeDto).collect(Collectors.toList()))
                .build();
    }

    // createColumn — workspaceId'yi kaydet
    @Transactional
    public ColumnResponse createColumn(CreateColumnRequest req, Long userId) {
        User user = findUser(userId);

        Long wsId = req.getWorkspaceId() != null ? req.getWorkspaceId() : 1L;

        // workspace bazlı pozisyon hesapla
        Integer maxPos = columnRepository.findMaxPositionByUserIdAndWorkspaceId(userId, wsId);
        int nextPos = (maxPos != null) ? maxPos + 1 : 0;

        TaskColumn column = TaskColumn.builder()
                .title(req.getTitle())
                .color(req.getColor() != null ? req.getColor() : "#6c6af6")
                .position(nextPos)
                .workspaceId(wsId)   // ← ekle
                .user(user)
                .cards(new ArrayList<>())
                .build();

        return toColumnResponse(columnRepository.save(column));
    }

    @Transactional
    public ColumnResponse updateColumn(Long columnId, UpdateColumnRequest req, Long userId) {
        TaskColumn column = columnRepository.findByIdAndUserId(columnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sütun bulunamadı", columnId));
        if (req.getTitle() != null && !req.getTitle().isBlank()) column.setTitle(req.getTitle());
        if (req.getColor() != null) column.setColor(req.getColor());
        return toColumnResponse(columnRepository.save(column));
    }

    @Transactional
    public void deleteColumn(Long columnId, Long userId) {
        TaskColumn column = columnRepository.findByIdAndUserId(columnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sütun bulunamadı", columnId));
        columnRepository.delete(column);
    }

    @Transactional
    public void reorderColumns(ReorderColumnsRequest req, Long userId) {
        List<Long> ids = req.getColumnIds();
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            TaskColumn col = columnRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sütun bulunamadı", id));
            col.setPosition(i);
            columnRepository.save(col);
        }
    }

    @Transactional
    public CardResponse createCard(Long columnId, CreateCardRequest req, Long userId) {
        User user = findUser(userId);
        TaskColumn column = columnRepository.findByIdAndUserId(columnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sütun bulunamadı", columnId));

        String title = (req.getTitle() == null || req.getTitle().isBlank()) ? "Yeni Görev" : req.getTitle();

        // NullPointerException riskini tamamen ortadan kaldıran güvenli kontrol
        Integer maxPos = taskRepository.findMaxPositionByColumnId(columnId);
        int nextPos = (maxPos != null) ? maxPos + 1 : 0;

        ProjectTask task = ProjectTask.builder()
                .title(title).description(req.getDescription())
                .priority(req.getPriority() != null ? req.getPriority() : "medium")
                .dueDate(parseDate(req.getDueDate())).position(nextPos)
                .column(column).user(user).assignees(resolveAssignees(req.getAssigneeIds()))
                .labels(req.getLabels() != null ? req.getLabels() : new ArrayList<>())
                .checklist(new ArrayList<>()).build();
        return toCardResponse(taskRepository.save(task));
    }

    @Transactional
    public CardResponse updateCard(Long cardId, UpdateCardRequest req, Long userId) {
        ProjectTask task = taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kart bulunamadı", cardId));
        if (req.getTitle() != null && !req.getTitle().isBlank()) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate().isBlank() ? null : LocalDate.parse(req.getDueDate()));
        if (req.getAssigneeIds() != null) task.setAssignees(resolveAssignees(req.getAssigneeIds()));
        if (req.getLabels() != null) task.setLabels(req.getLabels());
        if (req.getChecklist() != null) syncChecklist(task, req.getChecklist());
        return toCardResponse(taskRepository.save(task));
    }

    @Transactional
    public CardResponse moveCard(Long cardId, MoveCardRequest req, Long userId) {
        ProjectTask task = taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kart bulunamadı", cardId));
        TaskColumn targetColumn = columnRepository.findByIdAndUserId(req.getTargetColumnId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Hedef sütun bulunamadı", req.getTargetColumnId()));
        task.setColumn(targetColumn);
        task.setPosition(req.getNewPosition());
        return toCardResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteCard(Long cardId, Long userId) {
        ProjectTask task = taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kart bulunamadı", cardId));
        taskRepository.delete(task);
    }

    @Transactional
    public CardResponse toggleChecklistItem(Long cardId, Long itemId, Long userId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist öğesi bulunamadı", itemId));
        item.setCompleted(!item.isCompleted());
        checklistItemRepository.save(item);
        return toCardResponse(taskRepository.findById(cardId).orElseThrow());
    }

    private void syncChecklist(ProjectTask task, List<ChecklistItemDto> dtoList) {
        task.getChecklist().clear();
        for (ChecklistItemDto dto : dtoList) {
            ChecklistItem item = new ChecklistItem();
            item.setTask(task);
            item.setText(dto.getText());
            item.setCompleted(dto.isDone());
            task.getChecklist().add(item);
        }
    }

    private User findUser(Long userId) { return userRepository.findById(userId).orElseThrow(); }
    private List<User> resolveAssignees(List<Long> ids) { return (ids == null || ids.isEmpty()) ? new ArrayList<>() : userRepository.findAllById(ids); }
    private LocalDate parseDate(String dateStr) { if (dateStr == null || dateStr.isBlank()) return null; try { return LocalDate.parse(dateStr); } catch (Exception e) { return null; } }

    private ColumnResponse toColumnResponse(TaskColumn col) {
        return ColumnResponse.builder().id(col.getId()).title(col.getTitle()).color(col.getColor()).position(col.getPosition())
                .cards(col.getCards().stream().map(this::toCardResponse).collect(Collectors.toList())).build();
    }

    public CardResponse toCardResponse(ProjectTask task) {
        return CardResponse.builder().id(task.getId()).title(task.getTitle()).description(task.getDescription()).priority(task.getPriority())
                .dueDate(task.getDueDate() != null ? task.getDueDate().toString() : null).position(task.getPosition()).columnId(task.getColumn().getId())
                .assignees(task.getAssignees().stream().map(this::toAssigneeDto).collect(Collectors.toList())).labels(task.getLabels())
                .checklist(task.getChecklist().stream().map(i -> ChecklistItemDto.builder().id(i.getId()).text(i.getText()).done(i.isCompleted()).build()).collect(Collectors.toList())).build();
    }

    private AssigneeDto toAssigneeDto(User user) {
        return AssigneeDto.builder().id(user.getId()).name(user.getUsername())
                .initials(user.getUsername().substring(0, Math.min(2, user.getUsername().length())).toUpperCase()).build();
    }
}