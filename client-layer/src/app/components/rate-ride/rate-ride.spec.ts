import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RateRideComponent } from './rate-ride';
import { RideService } from '../../services/ride.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

describe('RateRideComponent - Rate Driver and Vehicle', () => {
  let component: RateRideComponent;
  let fixture: ComponentFixture<RateRideComponent>;
  let rideServiceMock: any;
  let authServiceMock: any;
  let dialogRefMock: any;

  beforeEach(async () => {
    rideServiceMock = {
      getRideForRating: jasmine.createSpy('getRideForRating').and.returnValue(
        of({ rideId: 1, driverId: 10, vehicleId: 20 })
      ),
      rateRide: jasmine.createSpy('rateRide').and.returnValue(of({}))
    };

    authServiceMock = {
      getCurrentUserId: jasmine.createSpy('getCurrentUserId').and.returnValue(100)
    };

    dialogRefMock = {
      close: jasmine.createSpy('close')
    };

    await TestBed.configureTestingModule({
      imports: [RateRideComponent],
      providers: [
        { provide: RideService, useValue: rideServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: MatDialogRef, useValue: dialogRefMock },
        { provide: MAT_DIALOG_DATA, useValue: { rideId: 1 } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RateRideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Set and Submit Data', () => {
    it('should set driver and vehicle ratings', () => {
      component.setDriverRating(4);
      component.setVehicleRating(5);

      expect(component.driverRating).toBe(4);
      expect(component.vehicleRating).toBe(5);
      expect(component.isDriverStarFilled(3)).toBe(true);
      expect(component.isVehicleStarFilled(5)).toBe(true);
      expect(component.isDriverStarFilled(5)).toBe(false);
    });

    it('should submit rating with correct data', () => {
      component.driverRating = 4;
      component.vehicleRating = 5;
      component.comment = 'Great ride!';

      component.submit();

      expect(rideServiceMock.rateRide).toHaveBeenCalledWith({
        rideId: 1,
        driverId: 10,
        vehicleId: 20,
        comment: 'Great ride!',
        driverRating: 4,
        vehicleRating: 5,
        authorId: 100
      });
    });
  })
  
  describe('Submit Data - User Not Logged In', () => {
    it('should not submit rating if user is not logged in', () => {
      authServiceMock.getCurrentUserId.and.returnValue(null);

      spyOn(window, 'alert');

      component.submit();

      expect(rideServiceMock.rateRide).not.toHaveBeenCalled();
      expect(dialogRefMock.close).not.toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith('You must be logged in!');
    });
  });

  describe('Close Dialog', () => {
    it('should close dialog when close() is called', () => {
      component.close();
      expect(dialogRefMock.close).toHaveBeenCalled();
    });

    it('should close dialog after submit', () => {
      component.driverRating = 5;
      component.vehicleRating = 5;
      component.comment = 'Awesome';

      component.submit();

      expect(rideServiceMock.rateRide).toHaveBeenCalled();
      expect(dialogRefMock.close).toHaveBeenCalled();
    });
  })

});
