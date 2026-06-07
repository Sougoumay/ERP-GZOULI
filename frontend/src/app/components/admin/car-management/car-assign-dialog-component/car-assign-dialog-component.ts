import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

// Material Modules
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {EmployeeSummary} from '../../../../models/employee-summary';
import {CarService} from '../../../../services/car-service';
import {EmployeeService} from '../../../../services/employee-service';
import {Car} from '../../../../models/car';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';



@Component({
  selector: 'app-car-assign-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule,
    MatFormFieldModule, MatSelectModule, MatDatepickerModule, MatInputModule, MatButtonModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './car-assign-dialog-component.html',
  styleUrl: './car-assign-dialog-component.css',
})
export class CarAssignDialogComponent  implements OnInit {
  form: FormGroup;
  employees: EmployeeSummary[] = [];
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private carService: CarService,
    private employeeService: EmployeeService,
    public dialogRef: MatDialogRef<CarAssignDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public car: Car // On reçoit la voiture sélectionnée
  ) {
    this.form = this.fb.group({
      employeeId: [null, Validators.required],
      startDate: [new Date(), Validators.required] // Par défaut aujourd'hui
    });
  }

  ngOnInit(): void {
    // On charge la liste des employés pour le select
    this.employeeService.getAllEmployees().subscribe(data => {
      // On ne propose que les employés actifs
      this.employees = data.filter(e => e.active);
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.isLoading = true;

    // Construction de l'objet DTO attendu par le Back
    const assignmentData = {
      carId: this.car.id,
      employeeId: this.form.value.employeeId,
      startDate: this.form.value.startDate
    };

    this.carService.assignCar(assignmentData).subscribe({
      next: () => {
        this.isLoading = false;
        this.dialogRef.close(true); // Ferme et signale le succès
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        alert(err.error || "Erreur lors de l'affectation du véhicule.");
      }
    });
  }
}
