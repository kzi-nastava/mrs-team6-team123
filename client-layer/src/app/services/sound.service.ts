import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class SoundService {
    private audio = new Audio('new-notification.mp3');
    play() {
        this.audio.play().catch(() => {});
    }
}