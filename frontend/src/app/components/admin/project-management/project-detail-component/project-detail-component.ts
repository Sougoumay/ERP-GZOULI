import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { Location } from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {MatIconButton} from '@angular/material/button';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import {ActivatedRoute} from '@angular/router';
import {ProjectService} from '../../../../services/project-service';
import {ProjectDetail} from '../../../../models/project-detail';
import {ProjectTeamComponent} from '../project-team-component/project-team-component';
import {delay} from 'rxjs';
import {ProjectInfoComponent} from '../project-info-component/project-info-component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {ProjectFinanceComponent} from '../project-finance-component/project-finance-component';
import {ProjectEquipmentComponent} from '../project-equipment-component/project-equipment-component';
import {ProjectTaskComponent} from '../project-task-component/project-task-component';
import {AuthService} from '../../../../services/auth-service';
import {JournalDialogComponent} from '../journal-dialog-component/journal-dialog-component';
import {ProjectJournalComponent} from '../project-journal-component/project-journal-component';

@Component({
  selector: 'app-project-detail-component',
  imports: [
    MatIcon,
    MatTooltip,
    MatIconButton,
    MatTabGroup,
    MatTab,
    ProjectTeamComponent,
    ProjectInfoComponent,
    MatProgressSpinnerModule,
    ProjectFinanceComponent,
    ProjectEquipmentComponent,
    ProjectTaskComponent,
    ProjectJournalComponent
  ],
  templateUrl: './project-detail-component.html',
  styleUrl: './project-detail-component.css',
})
export class ProjectDetailComponent implements OnInit {
  project: ProjectDetail | null = null;
  isLoading = true;
  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private authService: AuthService,
    private location: Location,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userRole = this.authService.getUserRole();

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.loadProjectData(Number(id));
    }
  }

  loadProjectData(id: number) {
    this.projectService.getProjectById(id).pipe(delay(0)).subscribe({
      next: (data: ProjectDetail) => { // Note: Ajustez le type de retour selon votre DTO
        this.project = data;
        console.log(this.project);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement projet', err);
        this.isLoading = false;
      }
    });
  }

  goBack() {
    this.location.back();
  }
}
