import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryTableComponent } from './ride-history-table';

describe('RideHistoryTable', () => {
  let component: RideHistoryTableComponent;
  let fixture: ComponentFixture<RideHistoryTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
