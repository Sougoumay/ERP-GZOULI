import {Injectable, NgZone} from '@angular/core';
import {environment} from '../../environments/environment';
import {AuthenticationDetails, CognitoUser, CognitoUserPool} from "amazon-cognito-identity-js";
import {Router} from '@angular/router';
import {Observable, Observer, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private userPool: CognitoUserPool;
  private cognitoUser: any;
  public userAttributes: any = {}; // Stockage temporaire des attributs pour le challenge [5, 6]

  // Subject pour notifier les composants des erreurs d'authentification
  public authError$ = new Subject<string>();

  constructor(private router: Router, private ngZone: NgZone) {
    const poolData = {
      UserPoolId: environment.cognitoUserPoolId,
      ClientId: environment.cognitoAppClientId,
    };
    this.userPool = new CognitoUserPool(poolData);
  }

  /**
   * Récupère le token JWT brut pour l'envoyer au Backend
   * (A utiliser dans votre HttpInterceptor)
   */
  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  // Méthode utilitaire pour savoir si on est connecté
  isAuthenticated(): boolean {
    const token = this.getToken();
    // On pourrait ajouter ici une vérification de l'expiration du token si nécessaire
    return !!token;
  }

  getUserRole(): string | null {
    return localStorage.getItem('user_role');
  }

  getUserName(): string | null {
    return localStorage.getItem('user_name');
  }

  /**
   * Méthode privée pour sauvegarder la session dans le LocalStorage
   * Elle est appelée après le Login OU après le Changement de MDP
   */
  private setSession(authResult: any, role: string) {
    // 1. Récupération du token (IdToken contient les infos utilisateur + signature)
    // Note : Selon votre backend, vous voudrez peut-être getAccessToken() à la place.
    // Pour Cognito, IdToken est souvent utilisé pour l'identité, AccessToken pour les droits AWS.
    const idToken = authResult.getIdToken().getJwtToken();

    // 2. Stockage dans le navigateur
    localStorage.setItem('auth_token', idToken);
    localStorage.setItem('user_role', role);
    localStorage.setItem('user_name', `${this.userAttributes.given_name} ${this.userAttributes.family_name || ''}`);
  }

  // Login
  login(emailaddress: string, password: string) {

    return new Observable((observer: Observer<any>) => {
      let authenticationDetails = new AuthenticationDetails({
        Username: emailaddress,
        Password: password,
      });

      const userData = { Username: emailaddress, Pool: this.userPool };
      this.cognitoUser = new CognitoUser(userData);

      this.cognitoUser.authenticateUser(authenticationDetails, {

        // CAS 1 : Connexion réussie
        onSuccess: (result: any) => {
          console.log("Connexion réussie");
          const payload = result.getIdToken().decodePayload();
          console.log("L'objet retourn est " + JSON.stringify(payload));

          this.userAttributes = payload;

          // Récupération sécurisée
          const groups = payload['cognito:groups'] || [];
          const customRole = payload['custom:role'];

          // On définit le rôle : soit le custom:role, soit le premier groupe, soit 'PERSONNEL'
          const role : string = customRole || (Array.isArray(groups) ? groups[0] : groups);

          console.log("Valeur brute du rôle :", role);
          console.log("Est Admin ?", role === 'ADMIN');

          this.setSession(result, role);

          // ON RENVOIE LE RÔLE AU COMPOSANT
          observer.next({
            status: 'SUCCESS',
            role: role
          });
          observer.complete();

        },

        // CAS 2 : Gestion de la première connexion (Exigence de changement de MDP)
        newPasswordRequired: (userAttributes: any, requiredAttributes: any) => {
          console.log("Changement de mot de passe requis pour :", userAttributes);

          // Stockage et nettoyage des attributs (Suppression des champs immuables/inutiles) [6]
          this.userAttributes = userAttributes;
          delete this.userAttributes.email_verified;
          delete this.userAttributes.email;

          // ON PREVIENT LE COMPOSANT QU'IL FAUT CHANGER LE MDP
          observer.next({
            status: 'NEW_PASSWORD_REQUIRED',
            role: null
          });
          observer.complete();
        },

        // CAS 3 : Erreur
        onFailure: (error: any) => {
          this.ngZone.run(() => { // <--- On enveloppe ici
            console.error("Erreur de connexion :", error);
            observer.error(error);
          });

        },
      });
    })
  }

  // Finalisation du challenge de première connexion
  forceChangePassword(newPassword: string) {

    return new Observable(observer => {
      this.cognitoUser.completeNewPasswordChallenge(
        newPassword,
        this.userAttributes,
        {
          onSuccess: (result: any) => {
            console.log("Mot de passe mis à jour avec succès");

            // IMPORTANT : Après le changement de MDP, Cognito nous connecte automatiquement.
            // Il faut donc aussi sauvegarder le token ici, sinon l'utilisateur devra se reconnecter.

            // On refait l'extraction du rôle car on vient de recevoir un nouveau token valide
            const payload = result.getIdToken().decodePayload();
            const groups = payload['cognito:groups'] || [];
            const customRole = payload['custom:role'];
            const role = customRole || (Array.isArray(groups) ? groups[0] : groups);

            // --- SAUVEGARDE DU TOKEN ET DU ROLE ---
            this.setSession(result, role);
            // --------------------------------------

            observer.next(result);
            observer.complete();
          },
          onFailure: (error: any) => {
            this.authError$.next(error.message || "Erreur lors du changement de mot de passe");
            console.error("Erreur lors de la modification du mot de passe :", error);
            observer.error(error);
          },
        }
      );
    })
  }


  // Logout
  logOut() {
    this.cognitoUser = this.userPool.getCurrentUser();
    if (this.cognitoUser) {
      this.cognitoUser.signOut();
    }

    // 2. Nettoyage de NOTRE cache local
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_role');

    // 3. Redirection
    this.router.navigate(["/auth/login"]);
  }

  forgotPassword(email : string) {
    return new Observable((observer: Observer<any>) => {
      // 1. On instancie le CognitoUser
      const userData = { Username: email, Pool: this.userPool };
      this.cognitoUser = new CognitoUser(userData);

      // 2. Appel de la méthode du SDK
      this.cognitoUser.forgotPassword({
        // Cas A : Le code a été envoyé avec succès
        // Note: C'est "inputVerificationCode" qui est appelé, pas "onSuccess" dans ce cas précis
        inputVerificationCode: (data: any) => {
          console.log('Code envoyé ! Vérifiez vos emails.', data);
          // On renvoie l'info au composant pour qu'il affiche le formulaire du code
          observer.next({
            status: 'CODE_SENT',
            destination: data // Contient souvent une partie de l'email masqué
          });
          observer.complete();
        },

        // Cas B : Erreur (ex: User not found, Limit exceeded)
        onFailure: (err: any) => {
          console.error('Erreur forgotPassword', err);
          this.ngZone.run(() => {
            observer.error(err);
          });
        }
      });
    })
  }

  resetPassword(email : string, newPassword: string, code : string) {
    return new Observable(observer => {
      // 1. On s'assure d'avoir l'instance user (Stateless safety)
      const userData = { Username: email, Pool: this.userPool };
      const cognitoUser = new CognitoUser(userData);

      // 2. Appel de la confirmation
      cognitoUser.confirmPassword(code, newPassword, {
        onSuccess: () => {
          console.log('Mot de passe réinitialisé avec succès !');
          observer.next({ status: 'SUCCESS' });
          observer.complete();
        },
        onFailure: (err: any) => {
          console.error('Erreur lors du reset password', err);
          this.ngZone.run(() => {
            observer.error(err);
          });
        }
      });
    });
  }
}
