import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth-service';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {MatDivider, MatListItem, MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {MatLine} from '@angular/material/core';
import {NgOptimizedImage} from '@angular/common';
import {MatToolbar} from '@angular/material/toolbar';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-main-layout-component',
  imports: [
    MatSidenav,
    MatNavList,
    MatIcon,
    MatListItem,
    RouterLink,
    MatLine,
    RouterLinkActive,
    MatSidenavContainer,
    MatDivider,
    MatSidenavContent,
    MatToolbar,
    RouterOutlet,
    MatIconButton,
    MatTooltip
  ],
  templateUrl: './main-layout-component.html',
  styleUrl: './main-layout-component.css',
})
export class MainLayoutComponent implements OnInit {
  userName: string | null = ''; // Valeur par défaut
  userRole: string | null = '';

  constructor(private authService: AuthService) {

  }

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();
    this.userName = this.authService.getUserName();
  }


  onLogout() {
    this.authService.logOut();
  }
}
