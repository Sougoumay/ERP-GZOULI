import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox'; // Important pour cocher
import { MatProgressBarModule } from '@angular/material/progress-bar'; // Barre de progression
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

import { TaskService } from '../../../../services/task-service';
import { Task } from '../../../../models/task';
import {TaskDialogComponent} from '../task-dialog-component/task-dialog-component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AuthService} from '../../../../services/auth-service';

@Component({
  selector: 'app-project-task-component',
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule,
    MatCheckboxModule, MatProgressBarModule, MatTooltipModule,
    MatFormFieldModule, MatInputModule, MatProgressSpinnerModule
  ],
  templateUrl: './project-task-component.html',
  styleUrl: './project-task-component.css',
})
export class ProjectTaskComponent  implements OnInit {
  @Input() projectId!: number;
  isLoading = true;
  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  tasks: Task[] = [];
  dataSource = new MatTableDataSource<Task>([]);
  displayedColumns = ['status', 'label', 'taskWeight', 'assignee', 'startDate', 'scheduledEndDate', 'date', 'actions'];

  progress = 0; // Pour la barre (0 à 100)

  constructor(
    private taskService: TaskService,
    private authService: AuthService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userRole = this.authService.getUserRole();

    if (this.userRole !== 'ADMIN') {
      this.displayedColumns = this.displayedColumns.filter(col => col !== 'actions');
    }

    if (this.projectId) {
      this.loadData();

      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  loadData() {
    // 1. Charger les tâches
    this.taskService.getTasks(this.projectId).subscribe(data => {
      this.tasks = data;
      this.dataSource.data = data;

      this.taskService.getProgress(this.projectId).subscribe(res => {
          this.progress = res.progress;
        this.cdr.detectChanges();
        });
    });
  }

  // Filtrer la liste (utile si 200 tâches)
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openDialog(task?: Task) {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '500px',
      data: { projectId: this.projectId, task: task }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadData();
    });
  }

  // Action rapide : Cocher / Décocher
  toggleTask(task: Task) {

    this.taskService.toggleTask(this.projectId, task.id).subscribe({
      next: (updatedTask) => {
        // On met à jour avec la vraie date renvoyée par le serveur
        task.completionDate = updatedTask.completionDate;
        this.loadData();
      },
      error: () => {
        alert("Erreur de connexion");
      }
    });
  }

  deleteTask(task: Task) {
    if (!confirm("Supprimer cette tâche ?")) return;

    this.taskService.deleteTask(this.projectId, task.id).subscribe({
      next: () => this.loadData(),
      error: () => alert("Impossible de supprimer")
    });
  }
}
