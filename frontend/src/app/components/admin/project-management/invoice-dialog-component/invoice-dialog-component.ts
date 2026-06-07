import {Component, Inject, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import {InvoiceService} from '../../../../services/invoice-service';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import {Invoice} from '../../../../models/invoice';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-invoice-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatProgressSpinnerModule,
    MatInputModule, MatDatepickerModule, MatCheckboxModule, MatButtonModule, MatIconModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './invoice-dialog-component.html',
  styleUrl: './invoice-dialog-component.css',
})
export class InvoiceDialogComponent implements OnInit{
  form: FormGroup;
  selectedFile: File | null = null;
  isSubmitting = false;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private invoiceService: InvoiceService,
    private dialogRef: MatDialogRef<InvoiceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: InvoiceDialogData,
    // @Inject(MAT_DIALOG_DATA) public projectId: number // On reçoit l'ID du projet
  ) {
    this.form = this.fb.group({
      invoiceNumber: ['', Validators.required],
      submissionDate: [new Date(), Validators.required],
      amount: [null, [Validators.required, Validators.min(0)]],
      isCertified: [false]
    });
  }

  ngOnInit() {
    // Détection du mode
    if (this.data.invoice) {
      this.isEditMode = true;
      const inv = this.data.invoice;
      // Remplissage du formulaire
      this.form.patchValue({
        invoiceNumber: inv.invoiceNumber,
        submissionDate: inv.submissionDate,
        amount: inv.amount,
        isCertified: inv.isCertified
      });
    }
  }

  onFileSelected(event: any) {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
    console.log(this.selectedFile);
  }

  onSubmit() {
    if (this.form.invalid) return;
    // En édition, le fichier n'est pas obligatoire (on garde l'ancien)
    if (!this.isEditMode && !this.selectedFile) return;

    this.isSubmitting = true; // Bloque l'UI
    const formVal = this.form.value;

    if (this.isEditMode && this.data.invoice) {
      // APPEL UPDATE
      // Note: selectedFile peut être null ici, le service le gère
      this.invoiceService.updateInvoice(this.data.projectId, this.data.invoice.id!, formVal, this.selectedFile || undefined)
        .subscribe({
          next: () => {
            this.dialogRef.close({ action: 'RELOAD_INVOICES' });
          },
          error: (e) => {
            this.isSubmitting = false; // Débloque en cas d'erreur
            console.error(e);
          }
        });
    } else {
      // APPEL CREATE (Existant)
      this.invoiceService.addInvoice(this.data.projectId, formVal, this.selectedFile!)
        .subscribe({
          next: () => {
            this.dialogRef.close({ action: 'RELOAD_INVOICES' });
          },
          error: (e) => {
            this.isSubmitting = false;
            console.error(e);
          }
        });
    }
  }
}

export interface InvoiceDialogData {
  projectId: number;
  invoice?: Invoice; // Optionnel : Si présent, c'est une édition
}
