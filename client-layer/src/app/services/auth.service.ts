import { Injectable, signal } from '@angular/core';

export type UserType = 'driver' | 'passenger' | 'guest' | 'admin';

export interface User {
  id: string;
  name: string;
  email: string;
  type: UserType;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  currentUser = signal<User | null>(null);
  userType = signal<UserType>('guest');

  login(user: User) {
    this.currentUser.set(user);
    this.userType.set(user.type);
  }

  logout() {
    this.currentUser.set(null);
    this.userType.set('guest');
  }

  getCurrentUser() {
    return this.currentUser();
  }

  getUserType() {
    return this.userType();
  }
}
