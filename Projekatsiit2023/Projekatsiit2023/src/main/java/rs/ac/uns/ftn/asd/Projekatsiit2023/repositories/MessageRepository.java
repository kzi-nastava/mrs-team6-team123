package rs.ac.uns.ftn.asd.Projekatsiit2023.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientId(Long recipientId);
    List<Message> findBySenderId(Long senderId);
}
