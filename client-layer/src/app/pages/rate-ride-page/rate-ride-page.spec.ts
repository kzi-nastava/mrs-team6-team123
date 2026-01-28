import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RateRidePageComponent } from './rate-ride-page';

describe('RateRidePage', () => {
  let component: RateRidePageComponent;
  let fixture: ComponentFixture<RateRidePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RateRidePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RateRidePageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
