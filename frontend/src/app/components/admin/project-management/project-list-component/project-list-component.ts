import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import {ProjectSummary} from '../../../../models/project-summary';
import {ProjectDialogComponent} from '../project-dialog-component/project-dialog-component';
import {ProjectService} from '../../../../services/project-service';
import {Router} from '@angular/router';
import {AuthService} from '../../../../services/auth-service';



@Component({
  selector: 'app-project-list-component',
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, MatTooltipModule
  ],
  templateUrl: './project-list-component.html',
  styleUrl: './project-list-component.css',
})
export class ProjectListComponent implements OnInit {
  displayedColumns: string[] = ['projectOwner', 'name', 'amount', 'duration', 'status', 'actions'];
  dataSource = new MatTableDataSource<ProjectSummary>([]);
  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private projectService: ProjectService,
    private authService: AuthService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userRole = this.authService.getUserRole();

    // Si l'utilisateur n'est pas Admin, on retire la colonne 'amount'
    if (this.userRole !== 'ADMIN') {
      this.displayedColumns = this.displayedColumns.filter(col => col !== 'amount');
    }

    this.loadProjects();
  }

  loadProjects() {
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        console.log(data);
        this.dataSource.data = data;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => console.error('Erreur chargement projets', err)
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(ProjectDialogComponent, {
      width: '900',
      maxWidth: '95vw',
      maxHeight: '90vh',
      disableClose: true,
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.projectService.createProject(result).subscribe(() => {
          this.loadProjects();
          alert('Projet créé avec succès');
        });
      }
    });
  }

  openEditDialog(project: ProjectSummary) {
    const dialogRef = this.dialog.open(ProjectDialogComponent, {
      width: '900',
      maxWidth: '95vw',
      maxHeight: '90vh',
      disableClose: true,
      data: project
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.projectService.updateProject(project.id, result).subscribe(() => {
          this.loadProjects();
        });
      }
    });
  }

  toggleStatus(project: ProjectSummary) {
    const newStatus = !project.active;
    if (confirm(`Voulez-vous ${newStatus ? 'réactiver' : 'archiver'} le projet "${project.name}" ?`)) {
      this.projectService.toggleStatus(project.id, newStatus).subscribe(() => {
        project.active = newStatus;
        this.cdr.detectChanges(); // Mise à jour UI instantanée
      });
    }
  }

  viewDetails(project: ProjectSummary) {
    // Navigation vers la route de détail
    this.router.navigate(['/projects', project.id]);
  }
}
