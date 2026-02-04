import { Component, computed } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { ChatListComponent } from '../chat-list/chat-list';
import { ChatWindowComponent } from '../chat-window/chat-window';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [ChatListComponent, ChatWindowComponent, CommonModule],
  templateUrl: './chat-widget.html',
  styleUrls: ['./chat-widget.css'],
})
export class ChatWidgetComponent {
  isOpen = false;
  selectedChatId: number | null = null;

  constructor(public auth: AuthService) {}

  userType = computed(() => this.auth.getUserType());

  toggle() {
    this.isOpen = !this.isOpen;
    this.selectedChatId = null;
  }

  openChat(chatId: number) {
    this.selectedChatId = chatId;
  }

  close() {
    this.isOpen = false;
    this.selectedChatId = null;
  }

  get showChatList() {
    return this.userType() === 'ADMIN' && !this.selectedChatId;
  }

  get showChatWindow() {
    return this.userType() !== 'ADMIN' || this.selectedChatId;
  }
}
