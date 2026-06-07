import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';

import { EmployeeSummary } from '../../../../models/employee-summary';
import {EmployeeService} from '../../../../services/employee-service';
import {ProjectService} from '../../../../services/project-service';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {ProjectDetail} from '../../../../models/project-detail';
import {TeamMember} from '../../../../models/team-member';
import {AuthService} from '../../../../services/auth-service';

@Component({
  selector: 'app-project-team-component',
  imports: [
    CommonModule, ReactiveFormsModule, MatCheckboxModule,
    MatTableModule, MatSelectModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatTooltipModule
  ],
  templateUrl: './project-team-component.html',
  styleUrl: './project-team-component.css',
})
export class ProjectTeamComponent implements OnInit {
  @Input() project!: ProjectDetail; // L'Input initial
  isLoading = true;
  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  // On utilise MatTableDataSource pour gérer le tableau proprement
  dataSource = new MatTableDataSource<TeamMember>([]);
  availableEmployees: EmployeeSummary[] = [];
  selectedEmployeesControl = new FormControl<number[]>([]);
  displayedColumns = ['name', 'role', 'actions'];

  constructor(
    private employeeService: EmployeeService,
    private projectService: ProjectService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userRole = this.authService.getUserRole();
    // Le @if dans le parent garantit que this.project existe ici
    if (this.project) {
      // 1. Initialisation rapide avec les données reçues du parent (affichage immédiat)
      this.dataSource.data = this.project.teamMembers || [];

      // 2. On lance le chargement des employés disponibles pour le select
      if (this.userRole === 'ADMIN') {
        this.loadAvailableEmployees();
      }
    }
  }

  /**
   * Charge uniquement la liste déroulante (Ingénieurs/Techniciens)
   */
  loadAvailableEmployees() {
    this.employeeService.getAllEmployees().subscribe(employees => {

      // 1. On récupère les IDs des membres actuels via le dataSource (qui est la source de vérité du tableau)
      // On utilise un Set pour que la vérification .has() soit instantanée
      const currentMemberEmails = new Set(this.dataSource.data.map(m => m.email));

      this.availableEmployees = employees.filter(e =>
        e.active &&
        e.role != 'ADMIN' &&
        !currentMemberEmails.has(e.email));
    });
  }

  /**
   * Recharge l'équipe depuis le serveur (Utile après un ajout/suppression)
   */
  reloadTeamData() {
    // IMPORTANT : On ne peut pas compter sur @Input project ici car il est "stale" (périmé)
    // On doit rappeler le backend pour avoir la liste à jour
    this.projectService.getProjectTeam(this.project.id).subscribe(members => {
      this.dataSource.data = members; // Met à jour le tableau visuellement
      console.log("Les membres sont : " + members);
      // Optionnel : Mettre à jour l'objet local aussi
      this.project.teamMembers = members;

      if (this.userRole === 'ADMIN') {
        this.loadAvailableEmployees();
      }
    });
  }

  assignSelected() {
    const selectedIds = this.selectedEmployeesControl.value;

    if (!selectedIds || selectedIds.length === 0) return;

    this.projectService.assignTeamMembers(this.project.id, selectedIds).subscribe({
      next: () => {
        // Succès : On vide la sélection
        this.selectedEmployeesControl.reset();

        // CRUCIAL : On recharge les données fraîches depuis le serveur
        this.reloadTeamData();
      },
      error: (err) => console.error('Erreur assignation', err)
    });
  }

  removeMember(member: TeamMember) {
    // On utilise la logique Bulk qu'on a vue précédemment
    if(confirm(`Retirer ${member.firstName} du projet ?`)) {
      this.projectService.removeTeamMembers(this.project.id, [member.id]).subscribe({
        next: () => {
          this.reloadTeamData(); // Rafraîchit la liste
        },
        error: (err) => console.error(err)
      });
    }
  }
}
