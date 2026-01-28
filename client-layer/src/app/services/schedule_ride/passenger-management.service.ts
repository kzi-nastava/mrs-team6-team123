import { Injectable } from '@angular/core';
import { UserService } from '../user.service';

export interface Passenger {
  input: string; // Can be email or name
}

@Injectable({
  providedIn: 'root'
})
export class PassengerManagementService {
  passengers: Passenger[] = [];
  private emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  constructor(private userService: UserService) {}

  addPassenger() {
    this.passengers.push({ input: '' });
  }

  removePassenger() {
    this.passengers.pop();
  }

  updatePassenger(index: number, input: string) {
    if (this.passengers[index]) {
      this.passengers[index].input = input;
    }
  }

  async resolvePassengerIds(currentUserId: number): Promise<number[]> {
    const passengerIds: number[] = [currentUserId];
    const passengerInputs = this.passengers
      .map(p => p.input.trim())
      .filter(input => input.length > 0);

    const emails = passengerInputs.filter(input => this.emailPattern.test(input));

    if (emails.length > 0) {
      try {
        const users = await Promise.all(
          emails.map(email => this.userService.getUserByEmail(email).toPromise())
        );
        users.forEach(u => {
          if (u && u.id) {
            passengerIds.push(u.id);
          }
        });
      } catch (err) {
        throw new Error('One or more passenger emails not found');
      }
    }

    return passengerIds;
  }

  clear() {
    this.passengers = [];
  }
}
