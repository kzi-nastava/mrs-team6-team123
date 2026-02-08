package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class DriverProfileChangeService {

    private final PendingDriverProfileChangeRepository pendingChangeRepository;
    private final UserRepository userRepository;

    public DriverProfileChangeService(PendingDriverProfileChangeRepository pendingChangeRepository,
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
    public PendingChangeResponseDTO approveChange(Long id) {
        PendingDriverProfileChange change = pendingChangeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Change request not found"));

        if (change.getStatus() != ChangeStatus.PENDING) {
            throw new RuntimeException("Already been reviewed");
        }

        change.setReviewedAt(LocalDateTime.now());
        change.setStatus(ChangeStatus.APPROVED);

        // Apply changes to driver profile
        Driver driver = change.getDriver();
        driver.setFirstName(change.getFirstName());
        driver.setLastName(change.getLastName());
        driver.setPhone(change.getPhone());
        driver.setAddress(change.getAddress());
        userRepository.save(driver);

        PendingDriverProfileChange saved = pendingChangeRepository.save(change);
        return mapToDTO(saved);
    }

    @Transactional
    public PendingChangeResponseDTO declineChange(Long id) {
        PendingDriverProfileChange change = pendingChangeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Change request not found"));

        if (change.getStatus() != ChangeStatus.PENDING) {
            throw new RuntimeException("Already been reviewed");
        }

        change.setReviewedAt(LocalDateTime.now());
        change.setStatus(ChangeStatus.REJECTED);

        PendingDriverProfileChange saved = pendingChangeRepository.save(change);
        return mapToDTO(saved);
    }

    private PendingChangeResponseDTO mapToDTO(PendingDriverProfileChange change) {
        PendingChangeResponseDTO dto = new PendingChangeResponseDTO();
        dto.setId(change.getId());
        dto.setDriverId(change.getDriver().getId());
        dto.setDriverName(change.getDriver().getFirstName() + " " + change.getDriver().getLastName());
        dto.setDriverEmail(change.getDriver().getEmail());

        // Old values (current driver info)
        dto.setFirstNameOld(change.getDriver().getFirstName());
        dto.setLastNameOld(change.getDriver().getLastName());
        dto.setPhoneOld(change.getDriver().getPhone());
        dto.setAddressOld(change.getDriver().getAddress());

        // New values (requested changes)
        dto.setFirstNameNew(change.getFirstName());
        dto.setLastNameNew(change.getLastName());
        dto.setPhoneNew(change.getPhone());
        dto.setAddressNew(change.getAddress());

        dto.setStatus(change.getStatus());
        return dto;
    }
}
