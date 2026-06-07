import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../../services/auth-service';
import {Router} from '@angular/router';
import {passwordStrengthValidator} from '../../../validators/password-strength.validator';

@Component({
  selector: 'app-force-change-password-component',
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
    NgOptimizedImage,
    ReactiveFormsModule
  ],
  templateUrl: './force-change-password-component.html',
  styleUrl: './force-change-password-component.css',
})
export class ForceChangePasswordComponent {
  passwordForm: FormGroup;
  hidePassword = true;
  isLoading = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router : Router) {
    this.passwordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(12), passwordStrengthValidator]],
      confirmPassword: ['', [Validators.required, Validators.minLength(12)]]
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value
      ? null : { 'mismatch': true };
  }

  onSubmit() {
    if (this.passwordForm.valid) {
      this.isLoading = true;
      const { newPassword, confirmPassword } = this.passwordForm.value;

      // Note: On passe une chaîne vide pour oldPassword si c'est une première connexion
      this.authService.forceChangePassword(newPassword).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/auth/login']);
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }
}
