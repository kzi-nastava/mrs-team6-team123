import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPricingComponent } from './admin-pricing';

describe('AdminPricing', () => {
  let component: AdminPricingComponent;
  let fixture: ComponentFixture<AdminPricingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPricingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPricingComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
