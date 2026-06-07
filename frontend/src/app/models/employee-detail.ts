import {SalaryAdvance} from './salary-advance';
import {EmployeeSummary} from './employee-summary';

export interface EmployeeDetail extends EmployeeSummary {
    projects: ProjectHistory[];
  vehicles: CarHistory[];
  advances: SalaryAdvance[];
}

export interface ProjectHistory {
  projectId: number;
  projectName: string;
  roleOnProject: string; // Ex: "Superviseur" ou "Technicien"
  startDate: string;     // YYYY-MM-DD
  endDate?: string;
}

export interface CarHistory {
  carModel: string;
  registrationNumber: string;
  startDate: string;
  endDate?: string; // null si en cours
}
