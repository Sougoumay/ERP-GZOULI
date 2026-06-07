import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import {EmployeeSummary} from '../../../../models/employee-summary';
import {MAT_DATE_LOCALE, MatNativeDateModule, provideNativeDateAdapter} from '@angular/material/core';

@Component({
  selector: 'app-user-dialog-component',
  imports: [
    MatDialogTitle,
    MatIconButton,
    MatIcon,
    MatDialogContent,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatError,
    MatSelect,
    MatOption,
    MatDatepickerToggle,
    MatDialogActions,
    MatButton,
    MatNativeDateModule,
    MatDatepickerModule
  ],
  templateUrl: './user-dialog-component.html',
  styleUrl: './user-dialog-component.css',
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ]
})
export class UserDialogComponent implements OnInit {

  employeeForm!: FormGroup;
  isEditMode: boolean = false;
  roles: string[] = ['ADMIN', 'INGENIEUR', 'TECHNICIEN'];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EmployeeSummary | null // Données reçues
  ) {}

  ngOnInit(): void {
    // Détection du mode (Création ou Modification)
    this.isEditMode = !!this.data;

    this.employeeForm = this.fb.group({
      firstName: [this.data?.firstName || '', Validators.required],
      lastName: [this.data?.lastName || '', Validators.required],
      // L'email est l'ID Cognito, on ne le change pas facilement en mode Edit
      email: [{ value: this.data?.email || '', disabled: this.isEditMode }, [Validators.required, Validators.email]],
      phoneNumber: [this.data?.phoneNumber || '', Validators.required],
      idCardNumber: [this.data?.idCardNumber || '', Validators.required],
      address: [this.data?.address || ''],
      salary: [this.data?.salary || '', [Validators.required, Validators.min(0)]],
      birthday: [this.data?.birthday || '', Validators.required],
      role: [this.data?.role || 'TECHNICIEN', Validators.required],
      active: [this.data ? this.data.active : true]
    });
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onSubmit(): void {
    if (this.employeeForm.valid) {
      // Si le champ email est disabled, sa valeur n'est pas incluse dans .value
      // On utilise .getRawValue() pour tout récupérer
      console.log(this.employeeForm.value.birthday);

      const formData = this.employeeForm.getRawValue();
      this.dialogRef.close(formData);
    }
  }

}
