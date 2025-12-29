import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideActionsComponent } from './ride-actions';

describe('RideActions', () => {
  let component: RideActionsComponent;
  let fixture: ComponentFixture<RideActionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideActionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideActionsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
