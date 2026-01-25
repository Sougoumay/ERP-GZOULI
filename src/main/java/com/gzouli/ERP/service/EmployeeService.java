package com.gzouli.ERP.service;

import com.gzouli.ERP.dto.employee.EmployeeDetailDTO;
import com.gzouli.ERP.dto.employee.EmployeeRegistrationDTO;
import com.gzouli.ERP.dto.employee.EmployeeSummaryDTO;
import com.gzouli.ERP.entity.Car;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.entity.SalaryAdvance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    // ========================================================================
    // 1. GESTION DU CYCLE DE VIE (CRUD & COGNITO SYNC)
    // ========================================================================

    /**
     * Crée un employé dans la BDD locale (et orchestre l'appel Cognito).
     * Doit vérifier l'unicité de l'email et du CIN.
     * @param registrationDTO Les données du formulaire d'inscription.
     * @return L'employé créé avec son ID et son cognitoId.
     */
    EmployeeSummaryDTO createEmployee(EmployeeRegistrationDTO registrationDTO);

    /**
     * Met à jour les informations personnelles (Adresse, Tel, Nom, Prénom).
     * @param id L'identifiant de l'employé.
     * @param employeeSummaryDTO Les nouvelles informations.
     * @return L'employé mis à jour.
     */
    EmployeeSummaryDTO updateEmployeeDetails(Long id, EmployeeSummaryDTO employeeSummaryDTO);

    /**
     * Active ou Désactive un employé (Soft Delete).
     * Si active = false :
     * 1. Met à jour le champ 'active' en BDD.
     * 2. Appelle Cognito pour désactiver le compte (AdminDisableUser).
     * @param id L'identifiant de l'employé.
     * @param isActive Le nouveau statut souhaité.
     */
    void toggleEmployeeStatus(Long id, boolean isActive);

    boolean isEmailExist(String email);

    // ========================================================================
    // 2. LECTURE ET RECHERCHE
    // ========================================================================

    /**
     * Récupère un employé par son ID technique.
     */
    EmployeeDetailDTO getEmployeeById(Long id);

    /**
     * Récupère le profil de l'utilisateur connecté via son jeton (sub).
     * Utilisé pour l'endpoint "/me".
     * @param cognitoId L'UUID fourni par AWS Cognito.
     */
//    EmployeeSummaryDTO getEmployeeByCognitoId(String cognitoId);

    /**
     * Liste tous les employés (filtrage possible par statut actif/inactif).
     */
    List<EmployeeSummaryDTO> getAllEmployees();

    /**
     * Liste les employés selon leur rôle (ex: Récupérer tous les TECHNICIENS).
     * Utile pour assigner des tâches ou filtrer dans l'Admin.
     */
    //List<EmployeeSummaryDTO> getEmployeesByRole(Role role);

    // ========================================================================
    // 3. GESTION RH & FINANCES (SALAIRES & AVANCES)
    // ========================================================================

    /**
     * Met à jour le salaire de base de l'employé.
     * @param id L'identifiant de l'employé.
     * @param newSalary Le nouveau montant.
     */
    void updateBaseSalary(Long id, Double newSalary);

    /**
     * Ajoute une avance sur salaire pour un mois donné.
     * Impactera le calcul de rentabilité des projets (charges).
     * @param employeeId L'employé concerné.
     * @param amount Le montant de l'avance.
     * @param date La date de l'avance (souvent aujourd'hui).
     * @param notes Commentaire optionnel (motif).
     * @return L'entité SalaryAdvance créée.
     */
    SalaryAdvance addSalaryAdvance(Long employeeId, Double amount, LocalDate date, String notes);

    /**
     * Récupère l'historique des avances sur salaire d'un employé.
     */
    List<SalaryAdvance> getSalaryHistory(Long employeeId);

    // ========================================================================
    // 4. LOGISTIQUE & VÉHICULES (CAR ASSIGNMENTS)
    // ========================================================================

    /**
     * Affecte un véhicule à un employé.
     * Doit fermer (mettre une date de fin) à l'affectation précédente si elle existe.
     * @param employeeId L'employé.
     * @param carId Le véhicule à assigner.
     * @param startDate Date de début d'affectation.
     */
    void assignVehicleToEmployee(Long employeeId, Long carId, LocalDate startDate);

    /**
     * Retire le véhicule actuellement assigné (Retour au parc).
     * Met à jour la date de fin dans CarAssignment.
     * @param employeeId L'employé qui rend le véhicule.
     * @param returnDate Date de restitution.
     */
    void unassignVehicle(Long employeeId, LocalDate returnDate);

    /**
     * Récupère le véhicule actuellement actif de l'employé.
     * Utilise la méthode helper getActiveEquipments() de l'entité.
     * @return Le véhicule ou null/Optional.empty.
     */
    Optional<Car> getCurrentVehicle(Long employeeId);

    // ========================================================================
    // 5. GESTION DE PROJETS & TÂCHES
    // ========================================================================

    /**
     * Associe un employé à un projet (Monitoring).
     * Met à jour la relation ManyToMany `monitoredProjects`.
     * @param employeeId L'employé.
     * @param projectId Le projet.
     */
    void assignToProject(Long employeeId, Long projectId);

    /**
     * Retire un employé d'un projet.
     * @param employeeId L'employé.
     * @param projectId Le projet.
     */
    void removeFromProject(Long employeeId, Long projectId);

    /**
     * Récupère la liste des projets sur lesquels l'employé travaille actuellement.
     */
    List<Project> getAssignedProjects(Long employeeId);

    // ========================================================================
    // 6. SUIVI OPÉRATIONNEL (DASHBOARD & JOURNAUX)
    // ========================================================================

    /**
     * Vérifie si l'employé a soumis son "Journal de Chantier" pour une date donnée.
     * Sert à alimenter la colonne "OUI/NON" du Dashboard Admin (La Météo du jour).
     * @param employeeId L'employé.
     * @param date La date à vérifier (souvent LocalDate.now()).
     * @return true si un SiteJournal existe pour cette date.
     */
    boolean hasSubmittedJournalForDate(Long employeeId, LocalDate date);

    /**
     * Calcule le nombre de jours travaillés (basé sur les journaux soumis) pour un mois donné.
     * Sert à vérifier le minimum de 22 jours mentionné par le client.
     * @param employeeId L'employé.
     * @param month Le mois concerné.
     * @param year L'année concernée.
     * @return Le nombre de jours.
     */
    int countWorkingDaysInMonth(Long employeeId, int month, int year);
}
