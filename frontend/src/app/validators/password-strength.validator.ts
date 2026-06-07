import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const passwordStrengthValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const value: string = control.value;

  if (!value) {
    return null; // Validators.required gère le vide
  }

  const hasNumber = /[0-9]/.test(value);
  const hasUppercase = /[A-Z]/.test(value);
  const hasLowercase = /[a-z]/.test(value);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>_\-+=~`[\]\\;/]/.test(value);

  const valid =
    hasNumber &&
    hasUppercase &&
    hasLowercase &&
    hasSpecialChar;

  return valid
    ? null
    : {
      passwordStrength: {
        hasNumber,
        hasUppercase,
        hasLowercase,
        hasSpecialChar
      }
    };
};
