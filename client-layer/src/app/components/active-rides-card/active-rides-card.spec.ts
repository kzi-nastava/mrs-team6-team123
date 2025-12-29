import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveRidesCardComponent } from './active-rides-card';

describe('ActiveRidesCard', () => {
  let component: ActiveRidesCardComponent;
  let fixture: ComponentFixture<ActiveRidesCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveRidesCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveRidesCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
