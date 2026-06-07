import {ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../../services/auth-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-forgot-password-component',
  imports: [
    FormsModule,
    MatButton,
    MatCard,
    MatError,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatProgressSpinner,
    NgOptimizedImage,
    ReactiveFormsModule
  ],
  templateUrl: './forgot-password-component.html',
  styleUrl: './forgot-password-component.css',
})
export class ForgotPasswordComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {
    // Initialisation du Reactive Form avec validations
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      const { email } = this.loginForm.value;

      this.authService.forgotPassword(email).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log(response);
          // this.router.navigate(['/auth/reset-password']);
          this.router.navigate(['/auth/reset-password'], {
            queryParams: { email: email }
          });

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
