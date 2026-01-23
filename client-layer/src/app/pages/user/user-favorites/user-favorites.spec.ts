import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserFavoritesComponent } from './user-favorites';

describe('UserFavorites', () => {
  let component: UserFavoritesComponent;
  let fixture: ComponentFixture<UserFavoritesComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserFavoritesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserFavoritesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
