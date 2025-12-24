import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

import { ScheduleRideComponent } from './schedule-ride';

describe('ScheduleRideComponent', () => {
  let component: ScheduleRideComponent;
  let fixture: ComponentFixture<ScheduleRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduleRideComponent, FormsModule, MatSlideToggleModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleRideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.startAddress).toBe('');
    expect(component.endAddress).toBe('');
    expect(component.passengers).toBe(1);
    expect(component.vehicleType).toBe('economy');
    expect(component.hasPet).toBe(false);
    expect(component.hasBaby).toBe(false);
    expect(component.scheduleType).toBe('now');
    expect(component.additionalInstructions).toBe('');
  });

  it('should add a stop', () => {
    component.addStop();
    expect(component.stops.length).toBe(1);
  });

  it('should remove a stop', () => {
    component.addStop();
    const stopId = component.stops[0].id;
    component.removeStop(stopId);
    expect(component.stops.length).toBe(0);
  });

  it('should add a passenger', () => {
    component.addPassenger();
    expect(component.passengers).toBe(2);
  });

  it('should remove a passenger', () => {
    component.addPassenger();
    component.removePassenger();
    expect(component.passengers).toBe(1);
  });

  it('should not remove passenger if count is 1', () => {
    component.removePassenger();
    expect(component.passengers).toBe(1);
  });

  it('should update stop address', () => {
    component.addStop();
    const stopId = component.stops[0].id;
    component.updateStop(stopId, 'New Address');
    expect(component.stops[0].address).toBe('New Address');
  });

  it('should render the form with main heading', () => {
    const heading = fixture.nativeElement.querySelector('h2');
    expect(heading.textContent).toContain('Schedule a ride');
  });

  it('should have two columns layout', () => {
    const formLayout = fixture.nativeElement.querySelector('.form-layout');
    expect(formLayout).toBeTruthy();
  });
});
