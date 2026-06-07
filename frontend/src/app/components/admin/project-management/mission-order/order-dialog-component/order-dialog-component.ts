import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
  MatDialogActions,
  MatDialogClose, MatDialogContent, MatDialogTitle
} from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import { MatFormField, MatInput, MatInputModule, MatLabel, MatSuffix } from '@angular/material/input';
import { MAT_DATE_LOCALE, MatOption, provideNativeDateAdapter } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ProjectMissionOrderService } from '../../../../../services/project-mission-order-service';
import { ProjectMissionOrder } from '../../../../../models/project-mission-order';

@Component({
  selector: 'app-order-dialog-component',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatIcon,
    MatSelect,
    MatOption
  ],
  providers: [
    provideNativeDateAdapter(),
    { provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './order-dialog-component.html',
  styleUrl: './order-dialog-component.css',
})
export class OrderDialogComponent {
  form: FormGroup;
  selectedFile: File | null = null;
  isUploading = false;
  orderTypes = ['DEMARRAGE', 'ARRET', 'REPRISE'];
  projectId: number;
  order?: ProjectMissionOrder;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<OrderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private orderService: ProjectMissionOrderService
  ) {
    if (typeof data === 'number') {
      this.projectId = data;
    } else {
      this.projectId = data.projectId;
      this.order = data.order;
    }

    this.form = this.fb.group({
      type: [this.order?.type || '', Validators.required],
      effectiveDate: [this.order?.effectiveDate ? new Date(this.order.effectiveDate) : new Date(), Validators.required]
    });
  }

  onFileSelected(event: any) {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
  }

  onSubmit() {
    if (this.form.invalid || (!this.order && !this.selectedFile)) {
      alert("Veuillez remplir tous les champs et joindre obligatoirement le document PDF pour un nouvel ordre.");
      return;
    }

    this.isUploading = true;
    const formValues = this.form.value;

    if (this.order && this.order.id) {
      this.orderService.updateOrder(this.projectId, this.order.id, formValues, this.selectedFile || undefined)
        .subscribe({
          next: () => {
            this.isUploading = false;
            this.dialogRef.close(true);
          },
          error: (err) => {
            console.error('Erreur lors de la mise à jour', err);
            this.isUploading = false;
            alert("Une erreur est survenue lors de la mise à jour.");
            this.dialogRef.close();
          }
        });
    } else {
      this.orderService.createOrder(this.projectId, formValues, this.selectedFile as File)
        .subscribe({
          next: () => {
            this.isUploading = false;
            this.dialogRef.close(true);
          },
          error: (err) => {
            console.error('Erreur lors de la création', err);
            this.isUploading = false;
            alert("Une erreur est survenue lors de la création.");
            this.dialogRef.close();
          }
        });
    }
  }

  deleteOrder() {
    if (this.order && this.order.id && confirm("Êtes-vous sûr de vouloir supprimer cet ordre ?")) {
      this.orderService.deleteOrder(this.projectId, this.order.id).subscribe({
        next: () => {
          this.dialogRef.close(true);
        },
        error: (err) => {
          console.error("Erreur lors de la suppression", err);
          alert("Une erreur est survenue lors de la suppression.");
          this.dialogRef.close();
        }
      });
    }
  }
}
