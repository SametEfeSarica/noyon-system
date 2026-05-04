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

/**
 * BoardService — Kanban panosunun tüm iş mantığı.
 *
 * Tasarım kararları:
 * - Tüm entity → DTO dönüşümü burada yapılır (Controller DTO döner, entity değil).
 * - Sahiplik kontrolü: her operasyonda userId doğrulanır.
 *   → Bir kullanıcı başkasının kartını/sütununu değiştiremez.
 * - Drag & drop "move" operasyonu atomik: column + position tek @Transactional'da.
 */
@Service
@RequiredArgsConstructor
public class BoardService {

    private static final Set<String> VALID_PRIORITIES =
            Set.of("urgent", "high", "medium", "low");

    private final TaskColumnRepository    columnRepository;
    private final ProjectTaskRepository   taskRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final UserRepository          userRepository;

    // ═══════════════════════════════════════════════════════════════════════════
    // PANO — tek seferde tüm veri
    // ═══════════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long userId) {
        List<TaskColumn> columns = columnRepository.findByUserIdOrderByPositionAsc(userId);

        List<ColumnResponse> columnDtos = columns.stream()
                .map(this::toColumnResponse)
                .collect(Collectors.toList());

        // Panodaki tüm atanmış kullanıcıları çek (member listesi için)
        Set<User> memberSet = new LinkedHashSet<>();
        columns.forEach(col -> col.getCards().forEach(card -> memberSet.addAll(card.getAssignees())));
        List<AssigneeDto> members = memberSet.stream()
                .map(this::toAssigneeDto)
                .collect(Collectors.toList());

        return BoardResponse.builder()
                .columns(columnDtos)
                .members(members)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SÜTUN OPERASYONLARI
    // ═══════════════════════════════════════════════════════════════════════════

    @Transactional
    public ColumnResponse createColumn(CreateColumnRequest req, Long userId) {
        User user = findUser(userId);
        int nextPos = columnRepository.findMaxPositionByUserId(userId) + 1;

        TaskColumn column = TaskColumn.builder()
                .title(req.getTitle())
                .color(req.getColor() != null ? req.getColor() : "#505060")
                .position(nextPos)
                .user(user)
                .build();

        return toColumnResponse(columnRepository.save(column));
    }

    @Transactional
    public ColumnResponse updateColumn(Long columnId, UpdateColumnRequest req, Long userId) {
        TaskColumn column = columnRepository.findByIdAndUserId(columnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskColumn", columnId));

        if (req.getTitle() != null && !req.getTitle().isBlank()) column.setTitle(req.getTitle());
        if (req.getColor() != null) column.setColor(req.getColor());

        return toColumnResponse(columnRepository.save(column));
    }

    @Transactional
    public void deleteColumn(Long columnId, Long userId) {
        TaskColumn column = columnRepository.findByIdAndUserId(columnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskColumn", columnId));
        columnRepository.delete(column);
        // Cascade ALL → kartlar ve checklist'ler otomatik silinir
    }

    /**
     * Sütun sırası yeniden düzenleme (sürükle-bırak).
     * Frontend yeni sırayla column ID listesi gönderir.
     */
    @Transactional
    public void reorderColumns(ReorderColumnsRequest req, Long userId) {
        List<Long> ids = req.getColumnIds();
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            TaskColumn col = columnRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("TaskColumn", id));
            col.setPosition(i);
            columnRepository.save(col);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // KART OPERASYONLARI
    // ═══════════════════════════════════════════════════════════════════════════

    @Transactional
    public CardResponse createCard(Long columnId, CreateCardRequest req, Long userId) {
        User user = findUser(userId);
        TaskColumn column = columnRepository.findByIdAndUserId(columnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskColumn", columnId));

        String priority = validatePriority(req.getPriority(), "medium");
        int nextPos = taskRepository.findMaxPositionByColumnId(columnId) + 1;

        List<User> assignees = resolveAssignees(req.getAssigneeIds());

        ProjectTask task = ProjectTask.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(priority)
                .dueDate(parseDate(req.getDueDate()))
                .position(nextPos)
                .column(column)
                .user(user)
                .assignees(assignees)
                .labels(req.getLabels() != null ? req.getLabels() : new ArrayList<>())
                .build();

        return toCardResponse(taskRepository.save(task));
    }

    @Transactional
    public CardResponse updateCard(Long cardId, UpdateCardRequest req, Long userId) {
        ProjectTask task = taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectTask", cardId));

        if (req.getTitle() != null && !req.getTitle().isBlank()) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getPriority() != null) task.setPriority(validatePriority(req.getPriority(), task.getPriority()));
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate().isBlank() ? null : LocalDate.parse(req.getDueDate()));
        if (req.getAssigneeIds() != null) task.setAssignees(resolveAssignees(req.getAssigneeIds()));
        if (req.getLabels() != null) task.setLabels(req.getLabels());

        // Sütun değişimi (modal'dan "Sütun" seçildiğinde)
        if (req.getColumnId() != null && !req.getColumnId().equals(task.getColumn().getId())) {
            TaskColumn newCol = columnRepository.findByIdAndUserId(req.getColumnId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("TaskColumn", req.getColumnId()));
            task.setColumn(newCol);
            int newPos = taskRepository.findMaxPositionByColumnId(req.getColumnId()) + 1;
            task.setPosition(newPos);
        }

        // Checklist güncelleme (tüm liste gönderilir, diff yapılmaz)
        if (req.getChecklist() != null) {
            syncChecklist(task, req.getChecklist());
        }

        return toCardResponse(taskRepository.save(task));
    }

    /**
     * Drag & Drop — Optimistic UI'ya karşılık gelen gerçek güncelleme.
     *
     * Frontend şunu gönderir:
     *   { targetColumnId: 5, newPosition: 2 }
     *
     * Service:
     * 1. Kartı yeni sütuna taşır.
     * 2. Hedef sütundaki diğer kartların position'larını shift eder.
     * 3. Kaynak sütundaki boşluğu kapatır.
     */
    @Transactional
    public CardResponse moveCard(Long cardId, MoveCardRequest req, Long userId) {
        ProjectTask task = taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectTask", cardId));

        Long sourceColumnId = task.getColumn().getId();
        Long targetColumnId = req.getTargetColumnId();
        int newPos = req.getNewPosition();

        TaskColumn targetColumn = columnRepository.findByIdAndUserId(targetColumnId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskColumn", targetColumnId));

        if (!sourceColumnId.equals(targetColumnId)) {
            // Kaynak sütundaki boşluğu kapat
            shiftPositionsDown(sourceColumnId, task.getPosition());
        } else {
            // Aynı sütun içi taşıma: eski yerden boşluk kapat
            shiftPositionsDown(sourceColumnId, task.getPosition());
        }

        // Hedef sütunda yer aç
        shiftPositionsUp(targetColumnId, newPos);

        task.setColumn(targetColumn);
        task.setPosition(newPos);

        return toCardResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteCard(Long cardId, Long userId) {
        ProjectTask task = taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectTask", cardId));
        Long columnId = task.getColumn().getId();
        int deletedPos = task.getPosition();
        taskRepository.delete(task);
        shiftPositionsDown(columnId, deletedPos);
    }

    @Transactional
    public CardResponse toggleChecklistItem(Long cardId, Long itemId, Long userId) {
        taskRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectTask", cardId));
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem", itemId));
        item.setCompleted(!item.isCompleted());
        checklistItemRepository.save(item);
        ProjectTask refreshed = taskRepository.findById(cardId).orElseThrow();
        return toCardResponse(refreshed);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // YARDIMCI METODLAR
    // ═══════════════════════════════════════════════════════════════════════════

    private void shiftPositionsDown(Long columnId, int fromPosition) {
        List<ProjectTask> tasks = taskRepository.findByColumnIdOrderByPositionAsc(columnId);
        tasks.stream()
                .filter(t -> t.getPosition() > fromPosition)
                .forEach(t -> {
                    t.setPosition(t.getPosition() - 1);
                    taskRepository.save(t);
                });
    }

    private void shiftPositionsUp(Long columnId, int fromPosition) {
        List<ProjectTask> tasks = taskRepository.findByColumnIdOrderByPositionAsc(columnId);
        tasks.stream()
                .filter(t -> t.getPosition() >= fromPosition)
                .forEach(t -> {
                    t.setPosition(t.getPosition() + 1);
                    taskRepository.save(t);
                });
    }

    private void syncChecklist(ProjectTask task, List<ChecklistItemDto> dtoList) {
        // Mevcut item'ları güncelle veya sil, yenileri ekle
        Map<Long, ChecklistItem> existing = task.getChecklist().stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(ChecklistItem::getId, i -> i));

        List<ChecklistItem> updated = new ArrayList<>();
        for (ChecklistItemDto dto : dtoList) {
            if (dto.getId() != null && existing.containsKey(dto.getId())) {
                ChecklistItem item = existing.get(dto.getId());
                item.setText(dto.getText());
                item.setCompleted(dto.isDone());
                updated.add(item);
            } else {
                ChecklistItem newItem = new ChecklistItem();
                newItem.setTask(task);
                newItem.setText(dto.getText());
                newItem.setCompleted(dto.isDone());
                updated.add(newItem);
            }
        }
        task.getChecklist().clear();
        task.getChecklist().addAll(updated);
    }

    private String validatePriority(String input, String defaultVal) {
        if (input == null) return defaultVal;
        String lower = input.toLowerCase();
        if (!VALID_PRIORITIES.contains(lower)) {
            throw new IllegalArgumentException("Geçersiz öncelik: " + input +
                    ". Geçerli değerler: " + VALID_PRIORITIES);
        }
        return lower;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return LocalDate.parse(dateStr);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private List<User> resolveAssignees(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        return userRepository.findAllById(ids);
    }

    // ── DTO Dönüştürücüler ────────────────────────────────────────────────────

    private ColumnResponse toColumnResponse(TaskColumn col) {
        List<CardResponse> cards = col.getCards().stream()
                .map(this::toCardResponse)
                .collect(Collectors.toList());
        return ColumnResponse.builder()
                .id(col.getId())
                .title(col.getTitle())
                .color(col.getColor())
                .position(col.getPosition())
                .cards(cards)
                .build();
    }

    public CardResponse toCardResponse(ProjectTask task) {
        List<AssigneeDto> assignees = task.getAssignees().stream()
                .map(this::toAssigneeDto)
                .collect(Collectors.toList());

        List<ChecklistItemDto> checklist = task.getChecklist().stream()
                .map(i -> ChecklistItemDto.builder()
                        .id(i.getId())
                        .text(i.getText())
                        .done(i.isCompleted())
                        .build())
                .collect(Collectors.toList());

        return CardResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .dueDate(task.getDueDate() != null ? task.getDueDate().toString() : null)
                .position(task.getPosition())
                .columnId(task.getColumn().getId())
                .assignees(assignees)
                .labels(task.getLabels() != null ? task.getLabels() : new ArrayList<>())
                .checklist(checklist)
                .build();
    }

    private AssigneeDto toAssigneeDto(User user) {
        String name = user.getUsername(); // veya user.getFirstName() + " " + user.getLastName()
        String initials = buildInitials(name);
        return AssigneeDto.builder()
                .id(user.getId())
                .name(name)
                .initials(initials)
                .build();
    }

    private String buildInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
