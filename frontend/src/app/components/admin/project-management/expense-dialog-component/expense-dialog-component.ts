import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { ExpenseService } from '../../../../services/expense-service';
import { EmployeeService } from '../../../../services/employee-service'; // Source [1]
import { ExpenseType } from '../../../../models/expense';
import { CommonModule } from '@angular/common';

// Imports Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EmployeeSummary } from '../../../../models/employee-summary'
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
@Component({
  selector: 'app-expense-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatProgressSpinnerModule,
    MatInputModule, MatDatepickerModule, MatSelectModule, MatButtonModule, MatIconModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './expense-dialog-component.html',
  styleUrl: './expense-dialog-component.css',
})
export class ExpenseDialogComponent implements OnInit {
  form: FormGroup;
  selectedFile: File | null = null;
  isSubmitting = false;

  employees: EmployeeSummary[] = []; // Liste pour le select
  expenseTypes = Object.values(ExpenseType); // [GASOIL, MAINTENANCE, ...]

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService,
    private employeeService: EmployeeService,
    private dialogRef: MatDialogRef<ExpenseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public projectId: number
  ) {
    this.form = this.fb.group({
      type: [ExpenseType.GASOIL, Validators.required],
      label: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0)]],
      expenseDate: [new Date(), Validators.required],
      employeeId: [null, Validators.required] // Obligatoire : Qui a payé ?
    });
  }

  ngOnInit(): void {
    // Charger la liste des employés pour le dropdown
    this.employeeService.getAllEmployees().subscribe(data => {
      this.employees = data;
    });
  }

  // --- CORRECTION CRITIQUE ICI ---
  onFileSelected(event: any) {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0]; // On prend l'INDEX 0 !
    }
  }

  onSubmit() {
    if (this.form.invalid || !this.selectedFile) return;

    this.isSubmitting = true; // Bloque l'UI
    const formValues = this.form.value;

    this.expenseService.addExpense(this.projectId, formValues, this.selectedFile).subscribe({
      next: () => {
        this.dialogRef.close({ action: 'RELOAD_EXPENSES' });
      },
      error: (err) => {
        console.error(err);
        this.isSubmitting = false; // Réactivation pour permettre une nouvelle tentative
        alert("Erreur lors de l'ajout de la dépense. Vérifiez les champs.");
      }
    });
  }
}
