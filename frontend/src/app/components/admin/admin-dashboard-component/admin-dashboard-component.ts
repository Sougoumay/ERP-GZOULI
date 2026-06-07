import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import {DashboardService} from '../../../services/dashboard-service';
import {DashboardStats} from '../../../models/daily-report-status';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';

@Component({
  selector: 'app-admin-dashboard-component',
  imports: [
    CommonModule,
    MatIconModule,
    MatTableModule,
    MatButtonModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './admin-dashboard-component.html',
  styleUrl: './admin-dashboard-component.css',
})
export class AdminDashboardComponent implements OnInit {
  today = new Date();
  stats: DashboardStats | null = null;
  isLoading = true; // État de chargement initial

  displayedColumns = ['employee', 'project', 'status', 'action'];

  constructor(
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef // Injection pour forcer la détection
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading = true; // Début du chargement

    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false; // Fin du chargement
        this.cdr.detectChanges(); // ⚡ Force la mise à jour de la vue immédiatement

        // setTimeout(() => {
        //   window.dispatchEvent(new Event('resize'));
        // }, 50);
      },
      error: (err) => {
        console.error("Erreur chargement dashboard", err);
        this.isLoading = false;
        this.cdr.detectChanges(); // Force la MAJ même en cas d'erreur
      }
    });
  }
}
