import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { DriverRegistration } from './driver-registration';
import { DriverService } from '../../../services/driver.service';
import { DriverRegistrationRequest } from '../../../models/driver.model';

// TODO: remove fdescribe and use describe to run all tests in this suite
fdescribe('DriverRegistration (Funkcionalnost 2.2.3: Registracija vozača)', () => {  
  let component: DriverRegistration;
  let fixture: ComponentFixture<DriverRegistration>;
  let driverService: any;
  let alertSpy: jasmine.Spy;

  beforeEach(async () => {
    driverService = {
      registerDriver: jasmine.createSpy('registerDriver')
    };
    alertSpy = spyOn(window, 'alert');

    await TestBed.configureTestingModule({ 
      imports: [DriverRegistration, FormsModule],
      providers: [
        { provide: DriverService, useValue: driverService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DriverRegistration);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form validation', () => {
    it('should show alert if any required field is empty', () => {
      component.driverForm = {
        email: '',
        firstName: '',
        lastName: '',
        address: '',
        phone: '',
        vehicleModel: '',
        vehicleType: 'STANDARD',
        licensePlate: '',
        seats: 4,
        babyFriendly: false,
        petFriendly: false
      };
      component.submit();
      expect(alertSpy).toHaveBeenCalledWith('All fields are required');
      expect(driverService.registerDriver).not.toHaveBeenCalled();
    });

    it('should show alert if seats is more than 15', () => {
      component.driverForm = {
        email: 'a@b.com',
        firstName: 'Anna',
        lastName: 'Brown',
        address: 'Central Street',
        phone: '+381601234567',
        vehicleModel: 'ModelX',
        vehicleType: 'STANDARD',
        licensePlate: 'XYZ123',
        seats: 16,
        babyFriendly: false,
        petFriendly: false
      };
      // Ensure registerDriver is a spy that returns an observable with a dummy subscribe
      driverService.registerDriver.and.returnValue({ subscribe: () => {} });
      component.submit();
      expect(alertSpy).toHaveBeenCalledWith('Number of seats must not exceed 15');
      expect(driverService.registerDriver).not.toHaveBeenCalled();
    });
  });

  describe('Service call and success', () => {
    it('should call driverService.registerDriver with correct data and reset form on success', fakeAsync(() => {
      const request: DriverRegistrationRequest = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        address: 'Main St',
        phone: '+381601234567',
        vehicleModel: 'Model X',
        vehicleType: 'LUXURY',
        licensePlate: 'XYZ123',
        seats: 4,
        babyTransport: true,
        petTransport: false
      };
      component.driverForm = {
        email: request.email,
        firstName: request.firstName,
        lastName: request.lastName,
        address: request.address,
        phone: request.phone,
        vehicleModel: request.vehicleModel,
        vehicleType: request.vehicleType,
        licensePlate: request.licensePlate,
        seats: request.seats,
        babyFriendly: true,
        petFriendly: false
      };
      driverService.registerDriver.and.returnValue(of({ ...request }));
      const resetSpy = spyOn(component, 'resetForm');
      component.submit();
      tick();
      expect(driverService.registerDriver).toHaveBeenCalledWith(request);
      expect(alertSpy).toHaveBeenCalledWith(
        `Driver registered: John Doe\nEmail: john@doe.com`
      );
      expect(resetSpy).toHaveBeenCalled();
    }));
  });

  describe('Error handling', () => {
    it('should show error alert if service fails with error message', fakeAsync(() => {
      const request: DriverRegistrationRequest = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        address: 'Main St',
        phone: '+381601234567',
        vehicleModel: 'Model X',
        vehicleType: 'LUXURY',
        licensePlate: 'XYZ123',
        seats: 4,
        babyTransport: true,
        petTransport: false
      };
      component.driverForm = {
        email: request.email,
        firstName: request.firstName,
        lastName: request.lastName,
        address: request.address,
        phone: request.phone,
        vehicleModel: request.vehicleModel,
        vehicleType: request.vehicleType,
        licensePlate: request.licensePlate,
        seats: request.seats,
        babyFriendly: true,
        petFriendly: false
      };
      driverService.registerDriver.and.returnValue(throwError(() => ({ error: { message: 'Already exists' } })));
      component.submit();
      tick();
      expect(alertSpy).toHaveBeenCalledWith('Failed to register driver: Already exists');
    }));

    it('should show generic error alert if service fails without error message', fakeAsync(() => {
      const request: DriverRegistrationRequest = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        address: 'Main St',
        phone: '+381601234567',
        vehicleModel: 'Model X',
        vehicleType: 'LUXURY',
        licensePlate: 'XYZ123',
        seats: 4,
        babyTransport: true,
        petTransport: false
      };
      component.driverForm = {
        email: request.email,
        firstName: request.firstName,
        lastName: request.lastName,
        address: request.address,
        phone: request.phone,
        vehicleModel: request.vehicleModel,
        vehicleType: request.vehicleType,
        licensePlate: request.licensePlate,
        seats: request.seats,
        babyFriendly: true,
        petFriendly: false
      };
      driverService.registerDriver.and.returnValue(throwError(() => ({})));
      component.submit();
      tick();
      expect(alertSpy).toHaveBeenCalledWith('Failed to register driver: Unknown error');
    }));
  });

  describe('Form reset', () => {
    it('should reset the form to default values', () => {
      component.driverForm = {
        email: 'a',
        firstName: 'b',
        lastName: 'c',
        address: 'd',
        phone: 'e',
        vehicleModel: 'f',
        vehicleType: 'LUXURY',
        licensePlate: 'g',
        seats: 2,
        babyFriendly: true,
        petFriendly: true
      };
      component.resetForm();
      expect(component.driverForm).toEqual({
        email: '',
        firstName: '',
        lastName: '',
        address: '',
        phone: '',
        vehicleModel: '',
        vehicleType: 'STANDARD',
        licensePlate: '',
        seats: 4,
        babyFriendly: false,
        petFriendly: false
      });
    });
  });
});
