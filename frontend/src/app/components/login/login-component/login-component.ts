import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth-service';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgOptimizedImage} from '@angular/common';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login-component',
  imports: [
    FormsModule,
    MatButton,
    MatCard,
    MatError,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatProgressSpinner,
    ReactiveFormsModule,
    NgOptimizedImage,
    RouterLink
  ],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  hidePassword = true;
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {
    // Initialisation du Reactive Form avec validations
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // // On écoute le flux d'erreurs provenant du service d'auth
    // this.authService.authError$.subscribe((message) => {
    //   this.errorMessage = message;
    //   this.isLoading = false; // On arrête le chargement en cas d'échec
    // });
  }

  onLogin() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log(response);

          // Gestion selon la réponse du service
          if (response.status === 'NEW_PASSWORD_REQUIRED') {
            // Cas première connexion
            this.router.navigate(['/auth/force-change-password']);

          } else if (response.status === 'SUCCESS') {

            // Cas connexion normale : Redirection selon le rôle
            const role = response.role;

            if (role === 'ADMIN') {
              this.router.navigate(['/admin/dashboard']);
            } else {
              // Par défaut ou 'PERSONNEL'
              this.router.navigate(['/projects']);
            }
          }
        },
        error: (err) => {
          console.log("Une erreur est survenue");
          console.log(err.message);
          this.isLoading = false;
          console.log(this.isLoading);
          // Afficher l'erreur (ex: "Mot de passe incorrect")
          this.errorMessage = err.message || "Erreur de connexion";

          this.cd.detectChanges();
        }
      });
    }
  }
}
