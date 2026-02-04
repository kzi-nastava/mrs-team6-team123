import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatWindowComponent } from '../chat-window/chat-window';
import { ChatListResponse, MessageResponse } from '../../../models/chat.model';
import { AuthService } from '../../../services/auth.service';
import { ChatService } from '../../../services/chat.service';

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ChatWindowComponent],
  templateUrl: './chat-list.html',
  styleUrls: ['./chat-list.css'],
})
export class ChatListComponent implements OnInit {
  @Output() chatSelected = new EventEmitter<number>();

  open(chatId: number) {
    this.chatSelected.emit(chatId);
  }

  chatList: ChatListResponse[] = [];

  constructor(
    private auth: AuthService,
    private chatService: ChatService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadChats();
  }

  loadChats() {
    const userId = this.auth.getCurrentUserId();
    if (userId === null) {
      console.error('You must be logged in.');
      return;
    }

    this.chatService.getAdminChats(userId).subscribe({
      next: (chats) => {
        this.chatList = chats;
        this.cdr.detectChanges();
      },
      error: (err) => 
        console.error('Error loading chats: ', err),  
    });
  }

  selectedChatId: number | null = null;

  openChat(chatId: number) {
    this.selectedChatId = chatId;
  }

  closeChat() {
    this.selectedChatId = null;
  }
}
