import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../../services/auth-service';
import {ActivatedRoute, Router} from '@angular/router';
import {passwordStrengthValidator} from '../../../validators/password-strength.validator';

@Component({
  selector: 'app-reset-password-component',
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
  templateUrl: './reset-password-component.html',
  styleUrl: './reset-password-component.css',
})
export class ResetPasswordComponent implements OnInit {
  passwordForm: FormGroup;
  hidePassword = true;
  isLoading = false;

  email: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService,
              private route: ActivatedRoute, private router : Router) {
    this.passwordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(12), passwordStrengthValidator]],
      confirmPassword: ['', [Validators.required, Validators.minLength(12)]],
      code: [''],
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit() {
    // On récupère l'email passé dans l'URL
    this.email = this.route.snapshot.queryParams['email'];
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value
      ? null : { 'mismatch': true };
  }

  onSubmit() {
    if (this.passwordForm.valid) {
      this.isLoading = true;
      const { newPassword, confirmPassword, code } = this.passwordForm.value;

      this.authService.resetPassword(this.email,newPassword, code).subscribe({
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
