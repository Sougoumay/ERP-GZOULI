import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { EquipmentService } from '../../../../services/equipment-service';
import { Equipment } from '../../../../models/equipment';
import { CommonModule } from '@angular/common';

// Material Imports
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';


@Component({
  selector: 'app-equipment-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule,
    MatFormFieldModule, MatSelectModule, MatDatepickerModule, MatInputModule, MatButtonModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './equipment-dialog-component.html',
  styleUrl: './equipment-dialog-component.css',
})
export class EquipmentDialogComponent  implements OnInit {
  form: FormGroup;
  availableStock: Equipment[] = []; // Liste pour le select
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private equipmentService: EquipmentService,
    private dialogRef: MatDialogRef<EquipmentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public projectId: number
  ) {
    this.form = this.fb.group({
      equipmentId: [null, Validators.required],
      startDate: [new Date(), Validators.required] // Par défaut aujourd'hui
    });
  }

  ngOnInit(): void {
    // Charger uniquement le matériel disponible
    this.equipmentService.getAvailableEquipment().subscribe(data => {
      this.availableStock = data;
    });
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.isLoading = true;

    this.equipmentService.assignEquipment(this.projectId, this.form.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        alert(err.error || "Impossible d'affecter ce matériel.");
      }
    });
  }

}
