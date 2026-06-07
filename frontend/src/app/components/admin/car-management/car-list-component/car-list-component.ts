import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import {Car} from '../../../../models/car';
import {CarAssignDialogComponent} from '../car-assign-dialog-component/car-assign-dialog-component';
import {CarService} from '../../../../services/car-service';
import {CarDialogComponent} from '../car-dialog-component/car-dialog-component';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-car-list-component',
  imports: [
    CommonModule, MatTableModule, MatButtonModule,
    MatIconModule, MatMenuModule, MatTooltipModule, MatSort
  ],
  templateUrl: './car-list-component.html',
  styleUrl: './car-list-component.css',
})
export class CarListComponent  implements OnInit {
  displayedColumns = ['car', 'matricule', 'cost', 'state', 'driver', 'actions'];
  dataSource = new MatTableDataSource<Car>([]);

  constructor(
    private carService: CarService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadCars();
  }

  loadCars() {
    this.carService.getAllCars().subscribe(data => {
      this.dataSource.data = data;
    });
  }

  // 1. Créer ou Modifier Voiture
  openCarDialog(car?: Car) {
    const dialogRef = this.dialog.open(CarDialogComponent, {
      width: '500px',
      data: car || null
    });

    dialogRef.afterClosed().subscribe(res => {
      if (res) this.loadCars();
    });
  }

  // 2. Assigner à un employé
  openAssignDialog(car: Car) {
    const dialogRef = this.dialog.open(CarAssignDialogComponent, {
      width: '400px',
      data: car // On passe la voiture pour savoir laquelle affecter
    });

    dialogRef.afterClosed().subscribe(res => {
      if (res) this.loadCars();
    });
  }

  // 3. Libérer (Récupérer clés)
  releaseCar(car: Car) {
    if (confirm(`Confirmer la récupération du véhicule ${car.brand} de ${car.currentDriverName} ?`)) {
      const today = new Date().toISOString().split('T')[0];
      this.carService.releaseCar(car.id, today).subscribe(() => this.loadCars());
    }
  }

  deleteCar(car: Car) {
    if (confirm("Supprimer définitivement ce véhicule ?")) {
      this.carService.deleteCar(car.id).subscribe({
        next: () => this.loadCars(),
        error: (err) => alert(err.error || "Impossible de supprimer")
      });
    }
  }
}
