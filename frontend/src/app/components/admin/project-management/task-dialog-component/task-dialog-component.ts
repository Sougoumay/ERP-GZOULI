import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

import { TaskService } from '../../../../services/task-service';
import { EmployeeService } from '../../../../services/employee-service'; // Pour la liste des gens
import { EmployeeSummary } from '../../../../models/employee-summary';
import { Task } from '../../../../models/task';

// Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';

export interface TaskDialogData {
  projectId: number;
  task?: Task; // Si présent = Mode Édition
}


@Component({
  selector: 'app-task-dialog-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatDatepickerModule
  ],
  providers: [
    provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: "fr-FR" },
  ],
  templateUrl: './task-dialog-component.html',
  styleUrl: './task-dialog-component.css',
})
export class TaskDialogComponent  implements OnInit {
  form: FormGroup;
  employees: EmployeeSummary[] = []; // Liste pour l'assignation
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private employeeService: EmployeeService,
    private dialogRef: MatDialogRef<TaskDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaskDialogData
  ) {
    this.form = this.fb.group({
      label: ['', Validators.required],
      taskWeight: ['', Validators.required],
      startDate: [''],
      scheduledEndDate: [''],
      assigneeId: [null] // Optionnel
    });
  }

  ngOnInit(): void {
    // 1. Charger la liste des employés actifs
    this.employeeService.getAllEmployees().subscribe(users => {
      this.employees = users.filter(u => u.active); // On n'assigne qu'aux actifs
    });

    // 2. Si édition, remplir le form
    if (this.data.task) {
      this.isEditMode = true;
      this.form.patchValue({
        label: this.data.task.label,
        taskWeight: this.data.task.taskWeight,
        startDate: this.data.task.startDate,
        scheduledEndDate: this.data.task.scheduledEndDate,
        assigneeId: this.data.task.assigneeId
      });
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const val = this.form.value;

    if (this.isEditMode && this.data.task) {
      // UPDATE
      this.taskService.updateTask(this.data.projectId, this.data.task.id, val).subscribe({
        next: () => this.dialogRef.close(true),
        error: (err) => alert("Erreur modification")
      });
    } else {
      // CREATE
      this.taskService.createTask(this.data.projectId, val).subscribe({
        next: () => this.dialogRef.close(true),
        error: (err) => alert("Erreur création")
      });
    }
  }
}
