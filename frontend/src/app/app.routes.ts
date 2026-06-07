import {Router, Routes} from '@angular/router';
import {AdminDashboardComponent} from './components/admin/admin-dashboard-component/admin-dashboard-component';
import {LoginComponent} from './components/login/login-component/login-component';
import {PersonnelDashboardComponent} from './components/personnel/personnel-dashboard-component/personnel-dashboard-component';
import {authenticationGuard} from './guards/authentication-guard';
import {MainLayoutComponent} from './components/main-layout-component/main-layout-component';
import {ForgotPasswordComponent} from './components/login/forgot-password-component/forgot-password-component';
import {ResetPasswordComponent} from './components/login/reset-password-component/reset-password-component';
import {
  ForceChangePasswordComponent
} from './components/login/force-change-password-component/force-change-password-component';
import {UserListComponent} from './components/admin/user-management/user-list-component/user-list-component';
import {
  ProjectListComponent
} from './components/admin/project-management/project-list-component/project-list-component';
import {
  ProjectDetailComponent
} from './components/admin/project-management/project-detail-component/project-detail-component';
import {
  InventoryListComponent
} from './components/admin/equipment-management/inventory-list-component/inventory-list-component';
import {CarListComponent} from './components/admin/car-management/car-list-component/car-list-component';
import {UserDetailComponent} from './components/admin/user-management/user-detail-component/user-detail-component';
import {inject} from '@angular/core';
import {AuthService} from './services/auth-service';
import {
  JournalDetailComponent
} from './components/admin/project-management/journal-detail-component/journal-detail-component';
import {ReportGeneratorComponent} from './components/admin/report-generator-component/report-generator-component';


export const routes: Routes = [

  {
    path: '',
    children: [
      {
        path: 'projects',
        component: MainLayoutComponent,
        children: [
          // 1. La liste des projets
          { path: '', component: ProjectListComponent },

          // 2. Le détail d'un projet
          { path: ':id', component: ProjectDetailComponent },

          // 3. Le détail d'un journal (Devient un "frère" de ProjectDetailComponent)
          { path: ':projectId/journals/:journalId', component: JournalDetailComponent }
        ],
      },
    ],
    canActivate: [authenticationGuard]
  },
  {
    path: 'admin',
    component: MainLayoutComponent,
    children: [
      {path: 'dashboard', component: AdminDashboardComponent},
      {
        path: 'users',
        children: [
          {path: '', component: UserListComponent},
          {path: ':id', component: UserDetailComponent},
        ]
      },
      {
        path: 'materials',
        children: [
          { path: '', component: InventoryListComponent },
        ]
      },
      {
        path: 'cars',
        children: [
          { path: '', component: CarListComponent },
          //{ path: ':id', component: CarListComponent },
        ]
      },
      {
        path: 'reporting',
        children: [
          { path: '', component: ReportGeneratorComponent },
        ]
      }
    ],
    canActivate: [authenticationGuard],
    data: { expectedRole: 'ADMIN' }
  },
  {
    path: 'auth',
    children : [
      { path: 'login', component: LoginComponent},
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent },
      { path: 'force-change-password', component: ForceChangePasswordComponent },
    ]
  },

  {
    path: '',
    pathMatch: 'full',
    canActivate: [() => {
      const authService = inject(AuthService); // On a importé les services
      const router = inject(Router);

      // S'il n'est pas connecté => Go Login
      if (!authService.isAuthenticated()) {
        return router.parseUrl('/auth/login');
      }

      // S'il est connecté, on vérifie son rôle !
      const role = authService.getUserRole();
      if (role === 'ADMIN') {
        return router.parseUrl('/admin/dashboard');
      } else {
        return router.parseUrl('/projects');
      }
    }],
    children: [] // Nécessaire pour éviter une erreur Angular sur une route sans composant
  },

  // { path: '', redirectTo:'/auth/login', pathMatch:'full' },
];
