import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackRidePageComponent } from './track-ride-page';

describe('TrackRidePage', () => {
  let component: TrackRidePageComponent;
  let fixture: ComponentFixture<TrackRidePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackRidePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackRidePageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
