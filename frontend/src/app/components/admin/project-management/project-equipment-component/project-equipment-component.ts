import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';

import { EquipmentService } from '../../../../services/equipment-service';
import { ProjectEquipment } from '../../../../models/project-equipment';
import {EquipmentDialogComponent} from '../equipment-dialog-component/equipment-dialog-component';
import {
  MatExpansionPanel,
  MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {AuthService} from '../../../../services/auth-service';

@Component({
  selector: 'app-project-equipment-component',
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatExpansionPanelDescription
  ],
  templateUrl: './project-equipment-component.html',
  styleUrl: './project-equipment-component.css',
})
export class ProjectEquipmentComponent  implements OnInit {

  @Input() projectId!: number;
  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  // Deux sources de données séparées
  activeEquipment: ProjectEquipment[] = [];
  historyEquipment: ProjectEquipment[] = [];

  // Colonnes spécifiques (pas besoin de date de fin pour l'actif, pas besoin d'actions pour l'historique)
  activeColumns = ['label', 'ref', 'startDate', 'actions'];
  historyColumns = ['label', 'ref', 'startDate', 'endDate'];

  constructor(
    private equipmentService: EquipmentService,
    private authService: AuthService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userRole = this.authService.getUserRole();
    if (this.projectId) {
      this.loadEquipment();
    }
  }

  loadEquipment() {
    this.equipmentService.getProjectEquipment(this.projectId).subscribe(data => {
      // FILTRAGE LOGIQUE : Actif vs Historique
      this.activeEquipment = data.filter(e => e.status === 'ACTIF');
      this.historyEquipment = data.filter(e => e.status !== 'ACTIF');

      this.cdr.detectChanges();
    });
  }

  openAssignDialog() {
    const dialogRef = this.dialog.open(EquipmentDialogComponent, {
      width: '500px',
      data: this.projectId
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadEquipment();
    });
  }

  releaseEquipment(item: ProjectEquipment) {
    if (!confirm(`Confirmez-vous le retour de "${item.label}" au stock ?`)) return;

    // Date du jour par défaut
    const today = new Date().toISOString().split('T')[0];

    this.equipmentService.releaseEquipment(this.projectId, item.assignmentId, today).subscribe({
      next: () => this.loadEquipment(),
      error: (err) => alert(err.error || "Erreur lors de la libération")
    });
  }

}
