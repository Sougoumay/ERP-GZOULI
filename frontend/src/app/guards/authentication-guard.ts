import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth-service';

export const authenticationGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = ['ADMIN', 'INGENIEUR', 'TECHNICIEN'];

  // 1. Vérifier si l'utilisateur est connecté
  if (!authService.isAuthenticated()) {
    return router.navigate(['/auth/login']);
  }

  // 2. Récupérer le rôle attendu pour cette route (défini dans le routing)
  const expectedRole = route.data['expectedRole'];

  console.log('Authentication Guard: ', expectedRole);

  // 3. Si aucun rôle spécifique n'est requis, on laisse passer
  if (!expectedRole) {
    return true;
  }

  const userRole = authService.getUserRole();
  console.log('user role: ', userRole);

  if (userRole !== expectedRole || !roles.includes(expectedRole)) {
    return router.navigate(['/auth/login']);
  }

  // 4. Vérifier si le rôle correspond
  return true;
};
