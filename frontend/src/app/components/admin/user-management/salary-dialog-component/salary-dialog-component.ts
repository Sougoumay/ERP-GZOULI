import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

// Material Imports
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {SalaryAdvance} from '../../../../models/salary-advance';
import {EmployeeService} from '../../../../services/employee-service';
import {EmployeeSummary} from '../../../../models/employee-summary';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import {MatDivider} from '@angular/material/list';


@Component({
  selector: 'app-salary-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatTableModule,
    MatFormFieldModule, MatInputModule, MatDatepickerModule, MatButtonModule, MatIconModule, MatDivider
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './salary-dialog-component.html',
  styleUrl: './salary-dialog-component.css',
})
export class SalaryDialogComponent  implements OnInit {
  form: FormGroup;
  dataSource = new MatTableDataSource<SalaryAdvance>([]);
  displayedColumns = ['date', 'amount', 'note'];

  totalAdvances = 0; // Pour afficher le total dû

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    public dialogRef: MatDialogRef<SalaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EmployeeSummary // On reçoit l'employé
  ) {
    this.form = this.fb.group({
      amount: [null, [Validators.required, Validators.min(1)]],
      date: [new Date(), Validators.required],
      note: ['']
    });
  }

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory() {
    if (!this.data.id) return;

    this.employeeService.getAdvances(this.data.id).subscribe(list => {
      this.dataSource.data = list;
      // Calcul du total cumulé
      this.totalAdvances = list.reduce((acc, curr) => acc + curr.amount, 0);
    });
  }

  onSubmit() {
    if (this.form.invalid || !this.data.id) return;

    this.employeeService.addAdvance(this.data.id, this.form.value).subscribe({
      next: () => {
        // On recharge la liste pour voir l'ajout immédiatement
        this.loadHistory();
        // On reset le formulaire sauf la date
        this.form.reset({ date: new Date(), amount: null, note: '' });
        this.dialogRef.close(true);
      },
      error: (err) => alert("Erreur lors de l'enregistrement")
    });
  }
}
