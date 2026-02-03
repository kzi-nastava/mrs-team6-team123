import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ChatResponse, MessageRequest, MessageResponse } from "../models/chat.model";
import { environment } from "../../enviroment";

@Injectable({providedIn: 'root'})
export class ChatService{
    constructor(
        private http: HttpClient
    ) {}

    getMyChat(userId: number) {
        return this.http.get<ChatResponse>(`${environment.apiUrl}/chats/my/${userId}`);
    }

    getMessages(chatId: number) {
        return this.http.get<MessageResponse[]>(`${environment.apiUrl}/messages/chat/${chatId}`);
    }

    sendMessage(request: MessageRequest) {
        return this.http.post<MessageRequest>(`${environment.apiUrl}/messages/send`, request);
    }
}