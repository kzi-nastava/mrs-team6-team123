import { validateDriverForm, DriverForm } from './driver-registration-validation';

fdescribe('validateDriverForm', () => {
  const baseForm: DriverForm = {
    email: 'john@doe.com',
    firstName: 'John',
    lastName: 'Doe',
    address: 'Main Street',
    phone: '+381601234567',
    vehicleModel: 'Model X',
    vehicleType: 'LUXURY',
    licensePlate: 'NS-123-AB',
    seats: 4,
    babyFriendly: false,
    petFriendly: false,
  };

  it('should be valid for correct data', () => {
    const result = validateDriverForm(baseForm);
    expect(result.valid).toBeTrue();
  });

  it('should require all fields', () => {
    const form = { ...baseForm, firstName: '' };
    const result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toBe('All fields are required');
  });

  it('should validate first name length and pattern', () => {
    let form = { ...baseForm, firstName: 'J' };
    let result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('at least');

    form = { ...baseForm, firstName: 'J'.repeat(51) };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('not exceed');

    form = { ...baseForm, firstName: 'J0hn' };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('only contain letters');
  });

  it('should validate last name length and pattern', () => {
    let form = { ...baseForm, lastName: 'D' };
    let result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('at least');

    form = { ...baseForm, lastName: 'D'.repeat(51) };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('not exceed');

    form = { ...baseForm, lastName: 'D0e' };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('only contain letters');
  });

  it('should validate email format', () => {
    const form = { ...baseForm, email: 'invalid' };
    const result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toBe('Invalid email format');
  });

  it('should validate phone format', () => {
    const form = { ...baseForm, phone: '123' };
    const result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('Invalid phone number format');
  });

  it('should validate address length', () => {
    let form = { ...baseForm, address: 'abcd' };
    let result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('at least');

    form = { ...baseForm, address: 'a'.repeat(51) };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('not exceed');
  });

  it('should validate vehicle model length', () => {
    let form = { ...baseForm, vehicleModel: 'a' };
    let result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('at least');

    form = { ...baseForm, vehicleModel: 'a'.repeat(51) };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('not exceed');
  });

  it('should require vehicle type', () => {
    const form = { ...baseForm, vehicleType: '' };
    const result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toBe('Vehicle type is required');
  });

  it('should validate license plate length and pattern', () => {
    let form = { ...baseForm, licensePlate: 'A' };
    let result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('at least');

    form = { ...baseForm, licensePlate: 'A'.repeat(21) };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('not exceed');

    form = { ...baseForm, licensePlate: '!!!' };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('Invalid license plate format');
  });

  it('should validate seats range', () => {
    let form = { ...baseForm, seats: 0 };
    let result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('at least');

    form = { ...baseForm, seats: 16 };
    result = validateDriverForm(form);
    expect(result.valid).toBeFalse();
    expect(result.message).toContain('not exceed');
  });
});
