import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-project-dialog-component',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './project-dialog-component.html',
  styleUrl: './project-dialog-component.css',
})
export class ProjectDialogComponent implements OnInit {
  projectForm!: FormGroup;
  isEditMode: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ProjectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any // Données reçues en modif
  ) {}

  ngOnInit(): void {
    this.isEditMode = !!this.data;

    this.projectForm = this.fb.group({
      projectOwner: [this.data?.projectOwner || '', Validators.required],
      name: [this.data?.name || '', Validators.required],
      description: [this.data?.description || '', Validators.required],
      amountExTax: [this.data?.amountExTax || '', [Validators.required, Validators.min(0)]],
      // amountIncTax: [this.data?.amountIncTax || '', [Validators.required, Validators.min(0)]],
      durationMonths: [this.data?.durationMonths || 12, [Validators.required, Validators.min(1)]],
      projectWinDate: [this.data?.projectWinDate ? new Date(this.data.projectWinDate) : new Date()],
      startDate: [this.data?.startDate ? new Date(this.data.startDate) : new Date(), Validators.required],
      generalObjectives: [this.data?.generalObjectives || ''],
      specificObjectives: [this.data?.specificObjectives || ''],
    });
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const rawValues = this.projectForm.getRawValue();

      // CONVERSION CRITIQUE DE LA DATE -> 'YYYY-MM-DD' pour Spring Boot
      if (rawValues.projectWinDate) {
        const date = new Date(rawValues.projectWinDate);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        rawValues.projectWinDate = `${year}-${month}-${day}`;
      }

      if (rawValues.startDate) {
        const date = new Date(rawValues.startDate);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        rawValues.startDate = `${year}-${month}-${day}`;
      }

      this.dialogRef.close(rawValues);
    }
  }
}
