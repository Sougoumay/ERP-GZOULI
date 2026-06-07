import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProjectService } from '../../../../services/project-service';
import { MatDialog } from '@angular/material/dialog';
import { ProjectDetail } from '../../../../models/project-detail';
import { ProjectDialogComponent } from '../project-dialog-component/project-dialog-component';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { DatePipe, DecimalPipe, NgClass } from '@angular/common';
import { OrderDialogComponent } from '../mission-order/order-dialog-component/order-dialog-component';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ProjectMissionOrder } from '../../../../models/project-mission-order';
import { ProjectMissionOrderService } from '../../../../services/project-mission-order-service';
import {AuthService} from '../../../../services/auth-service';

@Component({
  selector: 'app-project-info-component',
  imports: [
    MatIcon, MatIconModule,
    MatButtonModule, MatCardModule, DecimalPipe, DatePipe, MatColumnDef, MatHeaderCell, MatCell, MatHeaderCellDef, MatCellDef, MatTable, MatTooltip, MatHeaderRow, MatHeaderRowDef, MatRowDef, MatRow, NgClass
  ],
  templateUrl: './project-info-component.html',
  styleUrl: './project-info-component.css',
})
export class ProjectInfoComponent implements OnInit {
  @Input() project!: ProjectDetail;
  @Output() refreshRequest = new EventEmitter<void>(); // Pour demander au parent de recharger

  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  orders: ProjectMissionOrder[] = [];

  constructor(
    private dialog: MatDialog,
    private projectService: ProjectService,
    private authService: AuthService,
    private orderService: ProjectMissionOrderService,
  ) { }

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userRole = this.authService.getUserRole();
    this.loadOrders();
  }

  /**
   * Calcule la date de fin théorique (Date début + Durée mois)
   */
  getEndDate(): Date | null {
    if (!this.project?.startDate || !this.project?.durationMonths) return null;
    const start = new Date(this.project.startDate);
    // Ajoute les mois
    start.setMonth(start.getMonth() + this.project.durationMonths);
    return start;
  }

  openEditDialog() {
    const dialogRef = this.dialog.open(ProjectDialogComponent, {
      width: '600px',
      data: this.project // On passe les données actuelles (Mode Modification)
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Si modification validée, on appelle l'API Update
        // Note: Votre dialog gère peut-être déjà l'appel update ?
        // Si oui, juste recharger. Sinon faire l'appel ici.

        // Supposons que le dialog renvoie l'objet modifié prêt à envoyer
        this.projectService.updateProject(this.project.id, result).subscribe(() => {
          this.refreshRequest.emit(); // On dit au parent "Recharge tout !"
        });
      }
    });
  }

  openOrderDialog(order?: ProjectMissionOrder) {
    const dialogRef = this.dialog.open(OrderDialogComponent, {
      width: '500px',
      data: { projectId: this.project.id, order: order }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadOrders();
        // Rafraîchir les détails du projet pour mettre à jour les jours effectifs
        this.refreshRequest.emit();
      }
    });
  }

  loadOrders() {
    this.orderService.getOrdersByProject(this.project.id).subscribe(data => {
      this.orders = data;
      console.log(this.orders)
    });
  }

  viewFile(url?: string) {
    if (url) {
      window.open(url, '_blank');
    }
  }

  deleteOrder(order: ProjectMissionOrder) {
    if (order.id && confirm('Êtes-vous sûr de vouloir supprimer cet ordre de service ?')) {
      this.orderService.deleteOrder(this.project.id, order.id).subscribe({
        next: () => {
          this.loadOrders();
          this.refreshRequest.emit();
        },
        error: (err) => {
          console.error("Erreur lors de la suppression", err);
          alert("Une erreur est survenue lors de la suppression de l'ordre.");
        }
      });
    }
  }
}
