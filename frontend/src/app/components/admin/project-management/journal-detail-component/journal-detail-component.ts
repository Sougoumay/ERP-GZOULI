import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router'; // <-- NOUVEAU
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import {SiteJournalDetailDTO, SiteJournalService} from '../../../../services/site-journal-service';
@Component({
  selector: 'app-journal-detail-component',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  templateUrl: './journal-detail-component.html',
  styleUrl: './journal-detail-component.css',
})
export class JournalDetailComponent  implements OnInit {

  journalDetail: SiteJournalDetailDTO | null = null;
  isLoading = true;

  projectId!: number;
  journalId!: number;

  constructor(
    private route: ActivatedRoute, // Pour lire l'URL
    private location: Location, // Pour le bouton "Retour"
    private journalService: SiteJournalService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // 1. Récupération des IDs depuis l'URL
    this.projectId = Number(this.route.snapshot.paramMap.get('projectId'));
    this.journalId = Number(this.route.snapshot.paramMap.get('journalId'));

    // 2. Chargement des données
    this.loadDetails();
  }

  loadDetails() {
    this.journalService.getJournalDetails(this.projectId, this.journalId).subscribe({
      next: (data) => {
        console.log(data);
        this.journalDetail = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur lors du chargement des détails", err);
        this.isLoading = false;
        alert("Impossible de charger les détails de ce compte rendu.");
        this.goBack();
      }
    });
  }

  // Bouton retour vers la page du projet
  goBack() {
    this.location.back();
  }

  openImage(url: string) {
    window.open(url, '_blank');
  }
}
