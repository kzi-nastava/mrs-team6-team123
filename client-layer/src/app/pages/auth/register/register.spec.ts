import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register';
import { AuthService } from '../../../services/auth.service';

describe('RegisterComponent (Funkcionalnost 2.2.2: Registracija korisnika)', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  const fillValidForm = () => {
    component.form = {
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
      firstName: 'Marko',
      lastName: 'Markovic',
      address: 'Bulevar Oslobodjenja 1',
      phoneNumber: '+381601234567',
    };
  };

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthService', ['register']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, FormsModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ─── Component create ───────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty fields', () => {
    expect(component.form).toEqual({
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      address: '',
      phoneNumber: '',
    });
  });

  it('should initialize with no error or success messages', () => {
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('');
  });

  // ─── Form validation ───────────────────────────────────────────────────────

  describe('Form validation', () => {

    it('should show error if email is empty', () => {
      fillValidForm();
      component.form.email = '';
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if password is empty', () => {
      fillValidForm();
      component.form.password = '';
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if firstName is empty', () => {
      fillValidForm();
      component.form.firstName = '';
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if lastName is empty', () => {
      fillValidForm();
      component.form.lastName = '';
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if address is empty', () => {
      fillValidForm();
      component.form.address = '';
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if phoneNumber is empty', () => {
      fillValidForm();
      component.form.phoneNumber = '';
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if all fields are empty', () => {
      component.register();
      expect(component.errorMessage).toBe('All fields are required');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should show error if passwords do not match', () => {
      fillValidForm();
      component.form.confirmPassword = 'differentPassword';
      component.register();
      expect(component.errorMessage).toBe('Passwords do not match');
      expect(authService.register).not.toHaveBeenCalled();
    });

    // Min password lentgh
    it('should show error if password is exactly 5 characters (boundary: below minimum)', () => {
      fillValidForm();
      component.form.password = '12345';
      component.form.confirmPassword = '12345';
      component.register();
      expect(component.errorMessage).toBe('Password must be at least 6 characters');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should NOT show password length error if password is exactly 6 characters (boundary: minimum)', () => {
      fillValidForm();
      component.form.password = '123456';
      component.form.confirmPassword = '123456';
      authService.register.and.returnValue(of({ message: 'Success', userId: 1, email: 'test@example.com' }));
      component.register();
      expect(component.errorMessage).not.toBe('Password must be at least 6 characters');
      expect(authService.register).toHaveBeenCalled();
    });

    it('should show error if password is 1 character', () => {
      fillValidForm();
      component.form.password = 'a';
      component.form.confirmPassword = 'a';
      component.register();
      expect(component.errorMessage).toBe('Password must be at least 6 characters');
      expect(authService.register).not.toHaveBeenCalled();
    });

    it('should clear previous error message before new validation', () => {
      component.errorMessage = 'Some old error';
      fillValidForm();
      authService.register.and.returnValue(of({ message: 'Success', userId: 1, email: 'test@example.com' }));
      component.register();
      expect(component.errorMessage).toBe('');
    });
  });

  // ─── Succes registration ───────────────────────────────────────────────────

  describe('Successful registration', () => {

    it('should call authService.register with correct data', fakeAsync(() => {
      fillValidForm();
      authService.register.and.returnValue(of({ message: 'Registration successful', userId: 42, email: 'test@example.com' }));

      component.register();
      tick(3000);

      expect(authService.register).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        firstName: 'Marko',
        lastName: 'Markovic',
        address: 'Bulevar Oslobodjenja 1',
        phoneNumber: '+381601234567',
      });
    }));

    it('should set successMessage from response on success', fakeAsync(() => {
      fillValidForm();
      authService.register.and.returnValue(of({ message: 'Registration successful! Please check your email.', userId: 42, email: 'test@example.com' }));

      component.register();
      tick(3000);

      expect(component.successMessage).toBe('Registration successful! Please check your email.');
    }));

    it('should clear errorMessage on success', fakeAsync(() => {
      fillValidForm();
      component.errorMessage = 'Some previous error';
      authService.register.and.returnValue(of({ message: 'OK', userId: 1, email: 'test@example.com' }));

      component.register();
      tick(3000);

      expect(component.errorMessage).toBe('');
    }));

    it('should redirect to /login after 3 seconds on success', fakeAsync(() => {
      fillValidForm();
      authService.register.and.returnValue(of({ message: 'OK', userId: 1, email: 'test@example.com' }));

      component.register();
      tick(2999);
      expect(router.navigate).not.toHaveBeenCalled();
      tick(1);
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    }));
  });

  // ─── Failed reg ─────────────────────────────────────────────────

  describe('Failed registration', () => {

    it('should set errorMessage from error.error on service failure', () => {
      fillValidForm();
      authService.register.and.returnValue(throwError(() => ({ error: 'Email already in use' })));

      component.register();

      expect(component.errorMessage).toBe('Email already in use');
    });

    it('should set fallback errorMessage if error.error is missing', () => {
      fillValidForm();
      authService.register.and.returnValue(throwError(() => ({})));

      component.register();

      expect(component.errorMessage).toBe('Registration failed. Please try again.');
    });

    it('should not set successMessage on service failure', () => {
      fillValidForm();
      authService.register.and.returnValue(throwError(() => ({ error: 'Some error' })));

      component.register();

      expect(component.successMessage).toBe('');
    });

    it('should NOT redirect to /login on service failure', fakeAsync(() => {
      fillValidForm();
      authService.register.and.returnValue(throwError(() => ({ error: 'Some error' })));

      component.register();
      tick(3000);

      expect(router.navigate).not.toHaveBeenCalled();
    }));
  });

  // ─── Navigation to login  ─────────────────────────────────────────────────────────────

  describe('goToLogin()', () => {

    it('should navigate to /login', () => {
      component.goToLogin();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });
});