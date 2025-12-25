import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideInfoComponent } from './ride-info';

describe('RideInfo', () => {
  let component: RideInfoComponent;
  let fixture: ComponentFixture<RideInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideInfoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideInfoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
