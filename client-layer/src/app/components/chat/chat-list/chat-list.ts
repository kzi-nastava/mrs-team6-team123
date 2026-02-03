import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatWindowComponent } from '../chat-window/chat-window';
import { MessageResponse } from '../../../models/chat.model';

export interface ChatSummary {
  chatId: number;
  userName: string;
  lastMessage: string;
}

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ChatWindowComponent],
  templateUrl: './chat-list.html',
  styleUrls: ['./chat-list.css'],
})
export class ChatListComponent {
  @Output() chatSelected = new EventEmitter<number>();

  open(chatId: number) {
    this.chatSelected.emit(chatId);
  }

  @Input() chatSummaries: ChatSummary[] = [];
  @Input() chatMessagesMap: Record<string, MessageResponse[]> = {};

  /*
  chatSummaries: ChatSummary[] = [
    { chatId: 'chat1', userName: 'John Doe', lastMessage: 'Hello, I need help' },
    { chatId: 'chat2', userName: 'Jane Smith', lastMessage: 'Question about my order' }
  ];

  chatMessagesMap: Record<string, Message[]> = {
    chat1: [
      { text: 'Hello, I need help', type: 'received' },
      { text: 'Sure, how can I assist?', type: 'sent' }
    ],
    chat2: [
      { text: 'Question about my order', type: 'received' },
      { text: 'Please provide your order ID', type: 'sent' }
    ]
  };
  */

  selectedChatId: number | null = null;

  openChat(chatId: number) {
    this.selectedChatId = chatId;
  }

  closeChat() {
    this.selectedChatId = null;
  }

  get selectedChatMessages(): MessageResponse[] {
    return this.selectedChatId ? this.chatMessagesMap[this.selectedChatId] || [] : [];
  }
}
