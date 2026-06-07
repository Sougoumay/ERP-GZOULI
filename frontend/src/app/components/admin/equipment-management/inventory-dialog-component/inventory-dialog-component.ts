import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

// Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import {EquipmentService} from '../../../../services/equipment-service';
import {Equipment} from '../../../../models/equipment';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';

@Component({
  selector: 'app-inventory-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule,
    MatFormFieldModule, MatInputModule, MatDatepickerModule, MatSelectModule, MatButtonModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './inventory-dialog-component.html',
  styleUrl: './inventory-dialog-component.css',
})
export class InventoryDialogComponent  implements OnInit {
  form: FormGroup;
  isEditMode = false;
  states = ['NEUF', 'BON', 'MOYEN', 'EN_PANNE', 'HORS_SERVICE'];

  constructor(
    private fb: FormBuilder,
    private equipmentService: EquipmentService,
    private dialogRef: MatDialogRef<InventoryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Equipment | null // Null = Création, Objet = Edition
  ) {
    this.form = this.fb.group({
      label: ['', Validators.required],
      reference: ['', Validators.required], // Ex: Numéro de série
      purchaseDate: [new Date(), Validators.required],
      state: ['NEUF', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data) {
      this.isEditMode = true;
      this.form.patchValue(this.data);
    }
  }

  onSubmit() {
    console.log(JSON.stringify(this.form.value));
    if (this.form.invalid) return;

    const val = this.form.value;

    if (this.isEditMode && this.data) {
      // UPDATE
      this.equipmentService.updateEquipment(this.data.id, val).subscribe({
        next: () => this.dialogRef.close(true),
        error: (err) => alert(err.error || "Erreur modification")
      });
    } else {
      // CREATE
      this.equipmentService.createEquipment(val).subscribe({
        next: () => this.dialogRef.close(true),
        error: (err) => alert(err.error || "Erreur création")
      });
    }
  }
}
