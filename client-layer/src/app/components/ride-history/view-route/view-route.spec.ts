import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewRouteComponent } from './view-route';

describe('ViewRoute', () => {
  let component: ViewRouteComponent;
  let fixture: ComponentFixture<ViewRouteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewRouteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewRouteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
