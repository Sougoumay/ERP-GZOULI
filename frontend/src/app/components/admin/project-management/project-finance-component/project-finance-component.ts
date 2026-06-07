import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog'
import {ProjectDetail} from '../../../../models/project-detail';
import {Invoice} from '../../../../models/invoice';
import {InvoiceService} from '../../../../services/invoice-service';
import {InvoiceDialogComponent} from '../invoice-dialog-component/invoice-dialog-component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {Expense} from '../../../../models/expense';
import {ExpenseService} from '../../../../services/expense-service';
import {ExpenseDialogComponent} from '../expense-dialog-component/expense-dialog-component';
import {Router} from '@angular/router';
import {ProjectService} from '../../../../services/project-service';

@Component({
  selector: 'app-project-finance-component',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatProgressSpinnerModule],
  templateUrl: './project-finance-component.html',
  styleUrl: './project-finance-component.css',
})
export class ProjectFinanceComponent implements OnInit {
  @Input() project!: ProjectDetail;
  isLoading = true;

  invoices: Invoice[] = [];
  expenses: Expense[] = [];

  // Colonnes Factures
  invoiceColumns  = ['date', 'ref', 'amount', 'status', 'actions'];

  // Colonnes Dépenses
  expenseColumns = ['date', 'type', 'label', 'employee', 'amount', 'actions'];

  constructor(
    private invoiceService: InvoiceService,
    private expenseService: ExpenseService,
    private projectService: ProjectService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.project) {
      this.loadInvoices();
      this.loadExpenses();

      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  loadInvoices() {
    this.invoiceService.getInvoices(this.project.id).subscribe(data => {
      this.invoices = data;
      this.cdr.detectChanges();
    });
  }

  loadExpenses() {
    this.expenseService.getExpenses(this.project.id).subscribe(data => {
      this.expenses = data;
      this.cdr.detectChanges();
    });
  }

  deleteExpense(expense: Expense) {
    if(confirm('Supprimer cette dépense ? Cette action est irréversible.')) {
      this.expenseService.deleteExpense(this.project.id, expense.id).subscribe(() => {
        this.loadExpenses();
        this.refreshProjectDetails();
      });
    }
  }

  toggleInvoiceStatus(invoice: Invoice) {
    // Optimistic UI : On change visuellement tout de suite pour la réactivité
    const oldStatus = invoice.isCertified;
    invoice.isCertified = !oldStatus;

    this.invoiceService.toggleCertify(this.project.id, invoice.id!).subscribe({
      next: () => {
        // C'est validé côté serveur, on recharge les KPIs globaux si besoin
        // car le montant certifié a changé !
        this.refreshProjectDetails();
      },
      error: () => {
        // Si erreur, on remet comme avant et on alerte
        invoice.isCertified = oldStatus;
        alert("Erreur lors de la mise à jour du statut");
      }
    });
  }

  deleteInvoice(invoice: Invoice) {
    if(confirm(`Supprimer la facture ${invoice.invoiceNumber} ?`)) {
      this.invoiceService.deleteInvoice(this.project.id, invoice.id!).subscribe(() => {
        this.loadInvoices();
        this.refreshProjectDetails();
      });
    }
  }

  openInvoiceDialog(invoiceToEdit?: Invoice) {
    const dialogRef = this.dialog.open(InvoiceDialogComponent, {
      width: '500px',
      data: {
        projectId: this.project.id,
        invoice: invoiceToEdit // Si undefined = Création, Si objet = Édition
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.action === 'RELOAD_INVOICES') {
        // 2. Correction : setTimeout décale l'exécution et évite l'erreur ExpressionChanged...
        setTimeout(() => {
          this.loadInvoices();
          this.refreshProjectDetails();
        });
      }
    });
  }

  // NOUVELLE MÉTHODE
  openExpenseDialog() {
    const dialogRef = this.dialog.open(ExpenseDialogComponent, {
      width: '600px',
      data: this.project.id
    });

    dialogRef.afterClosed().subscribe(result => {
      // if (result) this.loadExpenses();
      if (result && result.action === 'RELOAD_EXPENSES') {
        // Correction ici aussi
        setTimeout(() => {
          this.loadExpenses();
          this.refreshProjectDetails();
        });
      }
    });
  }

  refreshProjectDetails() {
    // On rappelle le backend pour avoir les nouveaux totaux (Marges, Sommes...)
    this.projectService.getProjectById(this.project.id).subscribe({
      next: (updatedProject) => {
        this.project = updatedProject; // Mise à jour de l'objet local
        this.cdr.detectChanges();      // Force Angular à rafraîchir les KPIs en haut de page
      },
      error: (err) => console.error("Erreur lors du rafraîchissement du projet", err)
    });
  }
}
