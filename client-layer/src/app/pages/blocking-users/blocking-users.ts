import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/user';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';



@Component({
  selector: 'app-blocking-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './blocking-users.html',
  styleUrls: ['./blocking-users.css']
})
export class BlockingUsersComponent implements OnInit {
  users: UserProfile[] = [];
  filteredUsers: UserProfile[] = [];
  searchTerm: string = '';


  constructor(private userService: UserService, private authService: AuthService, private cdr: ChangeDetectorRef) {}
ngOnInit(): void {
  const currentUser = this.authService.currentUser();
  if (currentUser && currentUser.userId !== undefined) {
    this.userService.listUsers(currentUser.userId).subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading users:', err);
      }
    });
  } else {
    console.error('Current user or userId is undefined.');
  }
}


  searchUsers(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.email.toLowerCase().includes(term) ||
      user.firstName.toLowerCase().includes(term) ||
      user.lastName.toLowerCase().includes(term)
    );
  }

  toggleBlockUser(user: UserProfile): void {
    if (user.accountBlocked) {
      this.userService.unblockUser(user.id).subscribe(() => {
        user.accountBlocked = false;
        this.cdr.detectChanges();
      });
    } else {
      this.userService.blockUser(user.id).subscribe(() => {
        user.accountBlocked = true;
        this.cdr.detectChanges();
      });
    }
  }
}