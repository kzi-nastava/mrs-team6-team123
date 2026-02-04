export interface MessageRequest {
    content: string;
    senderId: number;
    chatId: number;
}

export interface MessageResponse {
    senderId: number;
    content: string;
    timestamp: string;
    mine: boolean;
}

export interface ChatResponse {
    chatId: number;
}

export interface ChatListResponse {
    chatId: number;
    userId: number;
    userName: string;
    lastMessage: string;
    lastMessageTimestamp: string;
}