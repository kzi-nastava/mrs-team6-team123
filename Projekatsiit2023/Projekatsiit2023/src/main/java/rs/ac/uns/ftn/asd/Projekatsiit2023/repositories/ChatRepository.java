package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Chat;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByUserId(Long userId);
    List<Chat> findByAdminId(Long adminId);

    @Query("""
        SELECT c.admin.id, COUNT(c)
        FROM Chat c
        GROUP BY c.admin.id
        ORDER BY COUNT(c) ASC
    """)
    List<Object[]> countChatsPerAdmin();
}
