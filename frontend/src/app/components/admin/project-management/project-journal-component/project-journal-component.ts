import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {JournalDialogComponent} from '../journal-dialog-component/journal-dialog-component';
import {SiteJournalService, SiteJournalSummaryDTO} from '../../../../services/site-journal-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-project-journal-component',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './project-journal-component.html',
  styleUrl: './project-journal-component.css',
})
export class ProjectJournalComponent implements OnInit {

  @Input() project!: any; // L'objet projet passé par le composant parent

  journals: SiteJournalSummaryDTO[] = [];
  isLoading = true;

  // Colonnes du tableau de l'historique
  displayedColumns = ['workDate', 'location', 'author', 'actions'];

  constructor(
    private dialog: MatDialog,
    private journalService: SiteJournalService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.project) {
      this.loadJournals();
    }
  }

  loadJournals() {
    this.isLoading = true;
    this.journalService.getJournalsByProject(this.project.id).subscribe({
      next: (data) => {
        this.journals = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur lors du chargement des journaux", err);
        this.isLoading = false;
      }
    });
  }

  // VOTRE FONCTION INTÉGRÉE :
  openJournalDialog() {
    const dialogRef = this.dialog.open(JournalDialogComponent, {
      width: '900px', // Largeur idéale pour PC
      disableClose: true, // Empêche de fermer en cliquant à côté pendant un gros upload
      data: this.project.id
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        alert('Compte rendu sauvegardé avec succès !');
        this.loadJournals(); // Recharger l'historique pour afficher le nouveau rapport
      }
    });
  }

  // (Optionnel) Pour visualiser le détail d'un rapport existant
  viewJournalDetails(journal: SiteJournalSummaryDTO) {
    console.log("Détails du journal :", journal);
    this.router.navigate(['/projects', this.project.id, 'journals', journal.id]);
  }
}
