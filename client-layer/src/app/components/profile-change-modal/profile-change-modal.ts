import { Component, EventEmitter, Input, Output, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileChangeService } from '../../services/profile-change.service';
import { PendingProfileChange } from '../../models/pending-profile-change.model';

@Component({
  selector: 'app-profile-change-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-change-modal.html',
  styleUrls: ['./profile-change-modal.css']
})
export class ProfileChangeModalComponent implements OnInit {
  @Input() changeId: number | null = null;
  @Output() close = new EventEmitter<boolean>();
  
  change: PendingProfileChange | null = null;
  loading = true;
  approving = false;
  declining = false;

  constructor(
    private profileChangeService: ProfileChangeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.changeId) {
      this.loadChange();
    }
  }

  loadChange(): void {
    if (!this.changeId) return;
    
    this.profileChangeService.getPendingChange(this.changeId).subscribe({
      next: (data) => {
        this.change = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load change:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  approve(): void {
    if (!this.change) return;
    this.approving = true;
    
    this.profileChangeService.approveChange(this.change.id).subscribe({
      next: () => {
        this.approving = false;
        this.cdr.detectChanges();
        this.close.emit(true);
      },
      error: (err) => {
        console.error('Failed to approve:', err);
        this.approving = false;
        this.cdr.detectChanges();
      }
    });
  }

  decline(): void {
    if (!this.change) return;
    this.declining = true;
    
    this.profileChangeService.declineChange(this.change.id).subscribe({
      next: () => {
        this.declining = false;
        this.cdr.detectChanges();
        this.close.emit(true);
      },
      error: (err) => {
        console.error('Failed to decline:', err);
        this.declining = false;
        this.cdr.detectChanges();
      }
    });
  }

  getChanges() {
    if (!this.change) return [];
    const changes = [];
    
    if (this.change.firstNameOld !== this.change.firstNameNew) {
      changes.push({ key: 'First Name', old: this.change.firstNameOld, new: this.change.firstNameNew });
    }
    if (this.change.lastNameOld !== this.change.lastNameNew) {
      changes.push({ key: 'Last Name', old: this.change.lastNameOld, new: this.change.lastNameNew });
    }
    if (this.change.phoneOld !== this.change.phoneNew) {
      changes.push({ key: 'Phone', old: this.change.phoneOld, new: this.change.phoneNew });
    }
    if (this.change.addressOld !== this.change.addressNew) {
      changes.push({ key: 'Address', old: this.change.addressOld, new: this.change.addressNew });
    }
    
    return changes;
  }

  closeModal(): void {
    this.close.emit(false);
  }
}
