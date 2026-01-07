import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportDriverComponent } from './report-driver';

describe('ReportDriver', () => {
  let component: ReportDriverComponent;
  let fixture: ComponentFixture<ReportDriverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportDriverComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportDriverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
