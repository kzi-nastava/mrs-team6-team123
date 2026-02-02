package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin.ApproveChangeRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.admin.PendingChangeResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.ChangeStatus;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Driver;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.PendingDriverProfileChange;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.PendingDriverProfileChangeRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminProfileChangeService {

    private final PendingDriverProfileChangeRepository pendingChangeRepository;
    private final UserRepository userRepository;

    public AdminProfileChangeService(PendingDriverProfileChangeRepository pendingChangeRepository,
            UserRepository userRepository) {
        this.pendingChangeRepository = pendingChangeRepository;
        this.userRepository = userRepository;
    }

    public List<PendingChangeResponseDTO> getPendingChanges() {
        return pendingChangeRepository.findByStatus(ChangeStatus.PENDING)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PendingChangeResponseDTO getChangeRequest(Long id) {
        PendingDriverProfileChange change = pendingChangeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Change request not found"));
        return mapToDTO(change);
    }

    @Transactional
    public PendingChangeResponseDTO reviewChangeRequest(Long id, ApproveChangeRequestDTO dto, Long adminId) {
        PendingDriverProfileChange change = pendingChangeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Change request not found"));

        if (change.getStatus() != ChangeStatus.PENDING) {
            throw new RuntimeException("Already been reviewed");
        }

        change.setReviewedAt(LocalDateTime.now());
        change.setReviewedByAdminId(adminId);

        if (dto.isApproved()) {
            // Apply changes to driver profile
            Driver driver = change.getDriver();
            driver.setFirstName(change.getFirstName());
            driver.setLastName(change.getLastName());
            driver.setEmail(change.getEmail());
            driver.setPhone(change.getPhone());
            driver.setAddress(change.getAddress());
            userRepository.save(driver);

            change.setStatus(ChangeStatus.APPROVED);
        } else {
            change.setStatus(ChangeStatus.REJECTED);
        }

        PendingDriverProfileChange saved = pendingChangeRepository.save(change);
        return mapToDTO(saved);
    }

    private PendingChangeResponseDTO mapToDTO(PendingDriverProfileChange change) {
        PendingChangeResponseDTO dto = new PendingChangeResponseDTO();
        dto.setId(change.getId());
        dto.setDriverId(change.getDriver().getId());
        dto.setDriverName(change.getDriver().getFirstName() + " " + change.getDriver().getLastName());
        dto.setDriverEmail(change.getDriver().getEmail());
        dto.setFirstName(change.getFirstName());
        dto.setLastName(change.getLastName());
        dto.setEmail(change.getEmail());
        dto.setPhone(change.getPhone());
        dto.setAddress(change.getAddress());
        dto.setStatus(change.getStatus());
        dto.setRequestedAt(change.getRequestedAt());
        dto.setReviewedAt(change.getReviewedAt());
        return dto;
    }
}
