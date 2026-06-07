import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import {Equipment} from '../../../../models/equipment';
import {InventoryDialogComponent} from '../inventory-dialog-component/inventory-dialog-component';
import {EquipmentService} from '../../../../services/equipment-service';

@Component({
  selector: 'app-inventory-list-component',
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, MatTooltipModule
  ],
  templateUrl: './inventory-list-component.html',
  styleUrl: './inventory-list-component.css',
})
export class InventoryListComponent  implements OnInit {
  displayedColumns: string[] = ['ref', 'label', 'purchaseDate', 'state', 'status', 'actions'];
  dataSource = new MatTableDataSource<Equipment>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private equipmentService: EquipmentService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory() {
    this.equipmentService.getAllInventory().subscribe(data => {
      this.dataSource.data = data;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openDialog(item?: Equipment) {
    const dialogRef = this.dialog.open(InventoryDialogComponent, {
      width: '500px',
      data: item || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadInventory();
    });
  }

  deleteItem(item: Equipment) {
    // Si l'objet n'est pas disponible, c'est qu'il est sur un chantier : interdire suppression
    if (!item.available) {
      alert("Impossible de supprimer ce matériel : il est actuellement affecté à un projet.");
      return;
    }

    if (confirm(`Supprimer définitivement "${item.label}" du stock ?`)) {
      this.equipmentService.deleteEquipment(item.id).subscribe({
        next: () => this.loadInventory(),
        error: (err) => alert(err.error || "Erreur lors de la suppression")
      });
    }
  }
}
