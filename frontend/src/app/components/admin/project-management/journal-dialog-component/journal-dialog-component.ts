import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

// Imports Material classiques...
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import {JournalPhotoData, SiteJournalService} from '../../../../services/site-journal-service';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-journal-dialog-component',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatDatepickerModule, MatButtonModule, MatIconModule, MatTooltip
  ],
  providers: [provideNativeDateAdapter(), { provide: MAT_DATE_LOCALE, useValue: "fr-FR" }],
  templateUrl: './journal-dialog-component.html',
  styleUrl: './journal-dialog-component.css',
})
export class JournalDialogComponent {
  form: FormGroup;
  isUploading = false;

  // Fichiers physiques stockés en dehors du formulaire réactif
  pvFiles: File[] = [];
  photoItems: JournalPhotoData[] = [];

  constructor(
    private fb: FormBuilder,
    private journalService: SiteJournalService,
    private dialogRef: MatDialogRef<JournalDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public projectId: number
  ) {
    this.form = this.fb.group({
      workDate: [new Date(), Validators.required],
      location: ['', Validators.required],
      taskDescription: ['', Validators.required]
    });
  }

  // --- GESTION DES PVs ---
  onPvSelected(event: any) {
    console.log("PV selected");
    const files = event.target.files;

    if (files) {
      for (let i = 0; i < files.length; i++) {
        console.log(files[i]);
        this.pvFiles.push(files[i]);
      }
    }
  }

  removePv(index: number) {
    this.pvFiles.splice(index, 1);
  }

  // --- GESTION DES PHOTOS LÉGENDÉES ---
  onPhotoSelected(event: any) {
    const file = event.target.files[0]; // On prend une photo à la fois pour lui lier une légende
    if (file) {
      console.log(file);
      this.photoItems.push({ file: file, description: '' });
    }
  }

  updatePhotoDescription(index: number, desc: string) {
    this.photoItems[index].description = desc;
  }

  removePhoto(index: number) {
    this.photoItems.splice(index, 1);
  }

  // --- SOUMISSION ---
  onSubmit() {
    if (this.form.invalid) {
      alert("Veuillez remplir les informations générales.");
      return;
    }

    // Validation métier : Au moins 1 PV ou 1 photo recommandé, mais vous pouvez ajuster
    if (this.pvFiles.length === 0 && this.photoItems.length === 0) {
      if(!confirm("Êtes-vous sûr de vouloir envoyer un compte rendu sans aucune pièce jointe ?")) {
        return;
      }
    }

    this.isUploading = true;
    const formValues = this.form.value;

    // Appel à notre service surpuissant !
    this.journalService.createJournal(this.projectId, formValues, this.pvFiles, this.photoItems)
      .subscribe({
        next: () => {
          this.isUploading = false;
          this.dialogRef.close(true); // Ferme et signale le succès
        },
        error: (err) => {
          console.error(err);
          this.isUploading = false;
          alert("Erreur lors de l'enregistrement du compte rendu.");
        }
      });
  }
}
