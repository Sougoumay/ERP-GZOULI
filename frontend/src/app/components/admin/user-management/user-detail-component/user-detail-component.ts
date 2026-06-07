import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import {EmployeeService} from '../../../../services/employee-service';
import {SalaryDialogComponent} from '../salary-dialog-component/salary-dialog-component';
import {MatIcon} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import {DatePipe, DecimalPipe, NgIf, UpperCasePipe} from '@angular/common';
// ... imports habituels (MatTabs, MatTable, etc.)



@Component({
  selector: 'app-user-detail-component',
  imports: [
    MatIcon,
    MatButton,
    MatTabGroup,
    MatTab,
    MatTable,
    MatHeaderCell,
    MatCell,
    MatColumnDef,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRowDef,
    DatePipe,
    UpperCasePipe,
    DecimalPipe,
    NgIf
  ],
  templateUrl: './user-detail-component.html',
  styleUrl: './user-detail-component.css',
})
export class UserDetailComponent implements OnInit {
  employee: any;
  totalAdvances = 0;

  constructor(
    private route: ActivatedRoute,
    private employeeService: EmployeeService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.loadEmployee(Number(id));
  }

  loadEmployee(id: number) {
    this.employeeService.getEmployeeDetail(id).subscribe(data => {
      this.employee = data;
      console.log(this.employee);
      this.cdr.detectChanges();
      // Recalcul du total des avances
      this.totalAdvances = data.advances?.reduce((acc: number, val: any) => acc + val.amount, 0) || 0;
    });
  }

  openAdvanceDialog() {
    // On ouvre le dialog MAIS on ne lui demande plus d'afficher l'historique
    // On peut modifier le composant Dialog pour qu'il ait un mode "InputOnly"
    const dialogRef = this.dialog.open(SalaryDialogComponent, {
      width: '400px',
      data: { id: this.employee.id, name: this.employee.firstName } // Juste l'ID nécessaire
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadEmployee(this.employee.id); // Recharger la page après ajout
    });
  }
}
