import {ApplicationConfig, importProvidersFrom, LOCALE_ID, provideBrowserGlobalErrorListeners} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import {authenticationInterceptor} from './interceptors/authentication-interceptor';
import {registerLocaleData} from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import {QuillModule} from 'ngx-quill';

registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authenticationInterceptor])),
    { provide: LOCALE_ID, useValue: 'fr-FR' },
    importProvidersFrom(QuillModule.forRoot())
  ]
};
