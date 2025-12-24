import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryFilterComponent } from './ride-history-filter';

describe('RideHistoryFilter', () => {
  let component: RideHistoryFilterComponent;
  let fixture: ComponentFixture<RideHistoryFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryFilterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryFilterComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
