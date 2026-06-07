import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import {CarService} from '../../../../services/car-service';
import {Car} from '../../../../models/car';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';

@Component({
  selector: 'app-car-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './car-dialog-component.html',
  styleUrl: './car-dialog-component.css',
})
export class CarDialogComponent  implements OnInit {
  form: FormGroup;
  isEditMode = false;
  states = ['EN_SERVICE', 'EN_PANNE', 'VENDU', 'HORS_SERVICE'];

  constructor(
    private fb: FormBuilder,
    private carService: CarService,
    private dialogRef: MatDialogRef<CarDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Car | null
  ) {
    this.form = this.fb.group({
      brand: ['', Validators.required],
      model: ['', Validators.required],
      registrationNumber: ['', Validators.required],
      monthlyCost: [0, [Validators.required, Validators.min(0)]],
      state: ['EN_SERVICE', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data) {
      this.isEditMode = true;
      this.form.patchValue(this.data);
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const val = this.form.value;

    if (this.isEditMode && this.data) {
      this.carService.updateCar(this.data.id, val).subscribe({
        next: () => this.dialogRef.close(true),
        error: (err) => alert(err.error || "Erreur modif")
      });
    } else {
      this.carService.createCar(val).subscribe({
        next: () => this.dialogRef.close(true),
        error: (err) => alert(err.error || "Erreur création")
      });
    }
  }
}
