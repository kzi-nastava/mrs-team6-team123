import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../enviroment';

@Component({
  selector: 'app-panic-button',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule],
  template: `
    <button 
      class="panic-btn"
      (click)="openPanicDialog()"
      [disabled]="triggered">
      🚨 {{ triggered ? 'PANIC ACTIVATED' : 'PANIC' }}
    </button>
  `,
  styles: [`
    .panic-btn {
      position: fixed;
      bottom: 20px;
      right: 20px;
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background: linear-gradient(135deg, #ff0000 0%, #cc0000 100%);
      color: white;
      border: 4px solid white;
      box-shadow: 0 4px 20px rgba(255, 0, 0, 0.4);
      font-size: 14px;
      font-weight: bold;
      cursor: pointer;
      transition: all 0.3s ease;
      z-index: 1000;
    }

    .panic-btn:hover:not(:disabled) {
      transform: scale(1.1);
      box-shadow: 0 6px 30px rgba(255, 0, 0, 0.6);
    }

    .panic-btn:active:not(:disabled) {
      transform: scale(0.95);
    }

    .panic-btn:disabled {
      background: #999;
      cursor: not-allowed;
      animation: pulse 1.5s infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.6; }
    }
  `]
})
export class PanicButtonComponent {
  @Input() rideId!: number;
  @Input() userId!: number;
  @Input() currentLocation?: string;

  triggered = false;

  constructor(
    private dialog: MatDialog,
    private http: HttpClient
  ) {}

  openPanicDialog() {
    const dialogRef = this.dialog.open(PanicDialogComponent, {
      width: '400px',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.triggerPanic();
      }
    });
  }

  triggerPanic() {
    const payload = {
      rideId: this.rideId,
      userId: this.userId,
      currentLocation: this.currentLocation || 'Unknown location'
    };

    console.log('🚨 Triggering PANIC:', payload);

    this.http.post(`${environment.apiUrl}/api/panic/trigger`, payload)
      .subscribe({
        next: (response: any) => {
          console.log('✅ PANIC triggered:', response);
          this.triggered = true;
          alert('🚨 Emergency services have been notified!');
        },
        error: (error) => {
          console.error('❌ PANIC failed:', error);
          alert('Failed to trigger panic alert. Please call emergency services directly!');
        }
      });
  }
}

// PANIC DIALOG
@Component({
  selector: 'app-panic-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title style="color: #ff0000; text-align: center;">
      🚨 PANIC ALERT
    </h2>
    <div mat-dialog-content style="text-align: center; padding: 20px;">
      <p style="font-size: 16px; margin-bottom: 20px;">
        Are you sure you want to trigger a panic alert?
      </p>
      <p style="font-size: 14px; color: #666;">
        This will immediately notify emergency services and administrators.
      </p>
    </div>
    <div mat-dialog-actions style="justify-content: center; gap: 12px;">
      <button 
        mat-raised-button 
        [mat-dialog-close]="false"
        style="background: #f0f0f0;">
        Cancel
      </button>
      <button 
        mat-raised-button 
        [mat-dialog-close]="true"
        style="background: #ff0000; color: white;">
        Confirm Panic
      </button>
    </div>
  `
})
export class PanicDialogComponent {}