import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {AuthService} from '../services/auth-service';
import {inject} from '@angular/core';
import {catchError, throwError} from 'rxjs';
import {Router} from '@angular/router';

export const authenticationInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const router = inject(Router); // Injecter le Router
  const token = authService.getToken(); // On récupère le token du localStorage via votre service

  let request = req;

  // NOUVEAU : On contourne l'intercepteur pour les requêtes vers AWS S3
  if (request.url.includes('s3.amazonaws.com')) {
    return next(request); // On laisse passer la requête telle quelle, sans modifier les headers
  }

  // Si on a un token, on l'ajoute à la requête
  if (token) {
    request = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` // Format standard pour Spring Boot (Bearer + Espace + Token)
      }
    });
  } else {
    console.warn('--- Aucun token trouvé dans le LocalStorage');
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si le backend répond 401 (Non autorisé / Token expiré)
      if (error.status === 401) {
        authService.logOut(); // On déconnecte proprement
        router.navigate(['/auth/login']);
      }
      return throwError(() => error);
    })
  );
};
