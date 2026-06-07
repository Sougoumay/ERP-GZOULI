import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatNoDataRow, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {EmployeeSummary} from '../../../../models/employee-summary';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {EmployeeService} from '../../../../services/employee-service';
import {MatDialog} from '@angular/material/dialog';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {NgClass, UpperCasePipe} from '@angular/common';
import {MatTooltip} from '@angular/material/tooltip';
import {UserDialogComponent} from '../user-dialog-component/user-dialog-component';
import {SalaryDialogComponent} from '../salary-dialog-component/salary-dialog-component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-list-component',
  imports: [
    MatButton,
    MatIcon,
    MatFormField,
    MatLabel,
    MatTable,
    MatHeaderCell,
    MatColumnDef,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    NgClass,
    MatIconButton,
    MatTooltip,
    MatHeaderRow,
    MatRow,
    MatPaginator,
    UpperCasePipe,
    MatInput,
    MatSort,
    MatRowDef,
    MatHeaderRowDef,
    MatNoDataRow
  ],
  templateUrl: './user-list-component.html',
  styleUrl: './user-list-component.css',
})
export class UserListComponent implements OnInit {
  // Colonnes à afficher dans le tableau
  displayedColumns: string[] = ['name', 'email', 'role', 'status', 'actions'];
  dataSource : MatTableDataSource<EmployeeSummary> = new MatTableDataSource<EmployeeSummary>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private employeeService: EmployeeService,
    private dialog: MatDialog,
    private router : Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        console.log(data);
        this.dataSource.data = data;

        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => console.error('Erreur chargement employés', err)
    });
  }

  // Filtrer la liste (Recherche rapide)
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      disableClose: true, // Empêche de fermer en cliquant à côté
      data: null // Pas de données = Mode Création
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log("utilisateur à créer " + result.toString());
        // result contient le JSON du formulaire (EmployeeRegistration)
        this.createEmployee(result);
      }
    });
  }

  openEditDialog(employee: EmployeeSummary) {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      disableClose: true,
      data: employee // On passe l'objet complet au formulaire
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateEmployee(employee.id, result);
      }
    });

  }

// 3. Appels API
  createEmployee(data: EmployeeSummary) {
    this.employeeService.createEmployee(data).subscribe({
      next: () => {
        alert('Employé créé avec succès !'); // Ou utilisez un SnackBar/Toast
        this.loadEmployees(); // Recharger le tableau
      },
      error: (err) => console.error(err)
    });
  }

  updateEmployee(id: number | undefined, data: EmployeeSummary) {
    data.id = id;
    this.employeeService.updateEmployee(id, data).subscribe({
      next: () => {
        alert('Mise à jour réussie');
        this.loadEmployees();
      },
      error: (err) => console.error(err)
    });
  }

  viewDetails(id: number) {
    console.log("Voir détails ID:", id);

    this.router.navigate(['/admin/users', id]);
  }

  toggleStatus(element: EmployeeSummary) {
    const newStatus = !element.active;
    const actionLabel = newStatus ? 'réactiver' : 'désactiver';
    if (confirm(`Voulez-vous vraiment ${actionLabel} l'accès de ${element.firstName} ${element.lastName} ?`)) {
      // 3. Appel au service
      this.employeeService.toggleStatus(element.id, newStatus).subscribe({
        next: () => {
          element.active = newStatus;
          this.cdr.detectChanges();
          console.log(`Utilisateur ${newStatus ? 'activé' : 'désactivé'} avec succès.`);
        },
        error: (err) => {
          console.error('Erreur lors du changement de statut', err);
          alert("Impossible de changer le statut. Vérifiez que l'utilisateur existe dans Cognito.");
          element.active = !newStatus;
          this.loadEmployees();
        }
      });
    }
  }

  openAdvanceDialog(employee: EmployeeSummary) {
    this.dialog.open(SalaryDialogComponent, {
      width: '650px',
      data: employee
    });
  }
}
