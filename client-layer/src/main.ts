
import { appConfig } from './app.config';
import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  .catch((err: any) => console.error(err));
