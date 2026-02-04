import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ChatService } from '../../../services/chat.service';
import { MessageRequest, MessageResponse } from '../../../models/chat.model';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-window.html',
  styleUrls: ['./chat-window.css'],
})
export class ChatWindowComponent implements OnInit {
  @Input() chatId: number | null = null;

  @Output() close = new EventEmitter<void>();

  messages: MessageResponse[] = [];
  newMessage = '';
  userId: number | null = null;

  constructor(
    private auth: AuthService,
    private chatService: ChatService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadChat();
  }

  loadChat() {
    this.userId = this.auth.getCurrentUserId();
    if (this.userId === null) {
      console.error('You must be logged in.');
      return;
    }
    if (this.chatId !== null) {
      this.loadMessages();
      return;
    }
    this.chatService.getMyChat(this.userId).subscribe({
      next: (response) => {
        this.chatId = response.chatId;
        this.cdr.detectChanges();
        this.loadMessages();
      },
      error: (err) => 
        console.error('Error finding chat: ', err)
    });
  }

  loadMessages() {
    this.chatService.getMessages(this.chatId!, this.userId!).subscribe({
      next: (messages) => {
        this.messages = messages;
        this.cdr.detectChanges();
      },
      error: (err) => 
        console.error('Error loading messages: ', err)
    });
  }

  sendMessage() {
    const content = this.newMessage.trim();
    if (!content || this.chatId === null) {
      return;
    }
    const newMessageRequest: MessageRequest = {
      content: content,
      senderId: this.auth.getCurrentUserId()!,
      chatId: this.chatId
    }
    this.chatService.sendMessage(newMessageRequest).subscribe({
      next: () => {
        this.newMessage = '';
        this.loadMessages();
      },
      error: (err) => 
        console.error('Error sending message: ', err)
    });
  }

  closeChat() {
    this.close.emit();
  }
}
