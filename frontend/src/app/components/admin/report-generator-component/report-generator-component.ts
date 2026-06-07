import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, formatDate } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { provideNativeDateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';

import { QuillModule } from 'ngx-quill';
import {ReportService} from '../../../services/report-service';
import {ProjectService} from '../../../services/project-service'; // L'éditeur de texte

@Component({
  selector: 'app-report-generator-component',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatDatepickerModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, QuillModule // Import du module Quill
  ],
  providers: [provideNativeDateAdapter(), { provide: MAT_DATE_LOCALE, useValue: "fr-FR" }],
  templateUrl: './report-generator-component.html',
  styleUrl: './report-generator-component.css'
})
export class ReportGeneratorComponent implements OnInit {

  form: FormGroup;
  projects: any[] = [];
  isGenerating = false;

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private reportService: ReportService
  ) {
    this.form = this.fb.group({
      projectId: [null, Validators.required],
      startDate: [null, Validators.required],
      endDate: [null, Validators.required],
      introduction: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Remplacer par votre vraie méthode pour récupérer la liste des projets
    this.projectService.getAllProjects().subscribe(data => {
      this.projects = data;
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.isGenerating = true;
    const formValues = this.form.value;

    // Formatage des dates pour Spring Boot (YYYY-MM-DD)
    const payload = {
      startDate: formatDate(formValues.startDate, 'yyyy-MM-dd', 'en-US'),
      endDate: formatDate(formValues.endDate, 'yyyy-MM-dd', 'en-US'),
      introductionHtml: formValues.introduction // Le texte enrichi (gras, listes, etc.)
    };

    this.reportService.generateWordReport(formValues.projectId, payload).subscribe({
      next: (blob: Blob) => {
        this.isGenerating = false;

        // --- MAGIE DU TÉLÉCHARGEMENT ---
        // On crée une URL locale pour le fichier reçu
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        // Nom du fichier avec la date du jour
        a.download = `Rapport_Chantier_${formatDate(new Date(), 'yyyyMMdd', 'en-US')}.docx`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error("Erreur génération", err);
        this.isGenerating = false;
        alert("Une erreur est survenue lors de la génération du rapport.");
      }
    });
  }
}
