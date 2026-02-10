import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-schedule-time',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './schedule-time.component.html',
  styleUrl: './schedule-time.component.css'
})
export class ScheduleTimeComponent implements OnInit, OnChanges {
  @Input() selectedHour = '00';
  @Input() selectedMinute = '00';
  @Output() selectedHourChange = new EventEmitter<string>();
  @Output() selectedMinuteChange = new EventEmitter<string>();

  hours: string[] = [];
  minutes: string[] = [];

  private currentHour = 0;
  private currentMinute = 0;

  ngOnInit() {
    this.initializeTimeOptions();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!this.hours.length) {
      return;
    }

    if (changes['selectedHour']) {
      this.updateAvailableMinutes();
      this.ensureMinuteIsValid();
    }
  }

  onHourChange() {
    this.selectedHourChange.emit(this.selectedHour);
    this.updateAvailableMinutes();
    this.ensureMinuteIsValid();
  }

  onMinuteChange() {
    this.selectedMinuteChange.emit(this.selectedMinute);
  }

  private initializeTimeOptions() {
    const now = new Date();
    this.currentHour = now.getHours();
    this.currentMinute = now.getMinutes();

    this.hours = this.generateAvailableHours();
    this.ensureHourIsValid();
    this.updateAvailableMinutes();
    this.ensureMinuteIsValid();
  }

  private generateAvailableHours(): string[] {
    const availableHours: string[] = [];
    for (let i = 0; i < 6; i++) {
      const hour = (this.currentHour + i) % 24;
      availableHours.push(hour.toString().padStart(2, '0'));
    }
    return availableHours;
  }

  private updateAvailableMinutes() {
    const selectedHourNum = parseInt(this.selectedHour, 10);

    if (selectedHourNum === this.currentHour) {
      this.minutes = [];
      for (let i = this.currentMinute; i < 60; i++) {
        this.minutes.push(i.toString().padStart(2, '0'));
      }
      return;
    }

    this.minutes = Array.from({ length: 60 }, (_, i) => i.toString().padStart(2, '0'));
  }

  private ensureHourIsValid() {
    if (!this.hours.includes(this.selectedHour)) {
      this.selectedHour = this.hours[0];
      this.selectedHourChange.emit(this.selectedHour);
    }
  }

  private ensureMinuteIsValid() {
    if (!this.minutes.includes(this.selectedMinute)) {
      this.selectedMinute = this.minutes[0];
      this.selectedMinuteChange.emit(this.selectedMinute);
    }
  }
}
