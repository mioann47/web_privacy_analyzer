package privacyanalyzer.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import privacyanalyzer.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}
