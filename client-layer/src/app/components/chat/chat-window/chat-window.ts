import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

export interface Message {
  text: string;
  type: 'sent' | 'received';
}

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-window.html',
  styleUrls: ['./chat-window.css'],
})
export class ChatWindowComponent {
  @Input() chatId: string | null = null;
  @Input() initialMessages: Message[] = [];

  @Output() close = new EventEmitter<void>();

  messages: Message[] = [];
  newMessage = '';

  ngOnInit() {
    this.messages = [...this.initialMessages];
  }

  sendMessage() {
    const text = this.newMessage.trim();
    if (!text) return;

    this.messages.push({ text, type: 'sent' });
    this.newMessage = '';

    setTimeout(() => {
      const chatContainer = document.querySelector('.chat-messages');
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    }, 0);
  }

  closeChat() {
    this.close.emit();
  }
}
