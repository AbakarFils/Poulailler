import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IEquipement, Equipement } from '../equipement.model';
import { EquipementService } from '../service/equipement.service';
import { IEmploye } from 'app/entities/employe/employe.model';
import { EmployeService } from 'app/entities/employe/service/employe.service';
import { IDirecteur } from 'app/entities/directeur/directeur.model';
import { DirecteurService } from 'app/entities/directeur/service/directeur.service';
import { TypeEquipement } from 'app/entities/enumerations/type-equipement.model';

@Component({
  selector: 'jhi-equipement-update',
  templateUrl: './equipement-update.component.html',
})
export class EquipementUpdateComponent implements OnInit {
  isSaving = false;
  typeEquipementValues = Object.keys(TypeEquipement);

  employesSharedCollection: IEmploye[] = [];
  directeursSharedCollection: IDirecteur[] = [];

  editForm = this.fb.group({
    id: [],
    statut: [],
    refArduino: [null, [Validators.required]],
    dateCrea: [],
    libelle: [null, [Validators.required]],
    marque: [],
    type: [],
    employes: [],
    gestionnaires: [],
  });

  constructor(
    protected equipementService: EquipementService,
    protected employeService: EmployeService,
    protected directeurService: DirecteurService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ equipement }) => {
      if (equipement.id === undefined) {
        const today = dayjs().startOf('day');
        equipement.dateCrea = today;
      }

      this.updateForm(equipement);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const equipement = this.createFromForm();
    if (equipement.id !== undefined) {
      this.subscribeToSaveResponse(this.equipementService.update(equipement));
    } else {
      this.subscribeToSaveResponse(this.equipementService.create(equipement));
    }
  }

  trackEmployeById(index: number, item: IEmploye): number {
    return item.id!;
  }

  trackDirecteurById(index: number, item: IDirecteur): number {
    return item.id!;
  }

  getSelectedEmploye(option: IEmploye, selectedVals?: IEmploye[]): IEmploye {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedDirecteur(option: IDirecteur, selectedVals?: IDirecteur[]): IDirecteur {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEquipement>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(equipement: IEquipement): void {
    this.editForm.patchValue({
      id: equipement.id,
      statut: equipement.statut,
      refArduino: equipement.refArduino,
      dateCrea: equipement.dateCrea ? equipement.dateCrea.format(DATE_TIME_FORMAT) : null,
      libelle: equipement.libelle,
      marque: equipement.marque,
      type: equipement.type,
      employes: equipement.employes,
      gestionnaires: equipement.gestionnaires,
    });

    this.employesSharedCollection = this.employeService.addEmployeToCollectionIfMissing(
      this.employesSharedCollection,
      ...(equipement.employes ?? [])
    );
    this.directeursSharedCollection = this.directeurService.addDirecteurToCollectionIfMissing(
      this.directeursSharedCollection,
      ...(equipement.gestionnaires ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.employeService
      .query()
      .pipe(map((res: HttpResponse<IEmploye[]>) => res.body ?? []))
      .pipe(
        map((employes: IEmploye[]) =>
          this.employeService.addEmployeToCollectionIfMissing(employes, ...(this.editForm.get('employes')!.value ?? []))
        )
      )
      .subscribe((employes: IEmploye[]) => (this.employesSharedCollection = employes));

    this.directeurService
      .query()
      .pipe(map((res: HttpResponse<IDirecteur[]>) => res.body ?? []))
      .pipe(
        map((directeurs: IDirecteur[]) =>
          this.directeurService.addDirecteurToCollectionIfMissing(directeurs, ...(this.editForm.get('gestionnaires')!.value ?? []))
        )
      )
      .subscribe((directeurs: IDirecteur[]) => (this.directeursSharedCollection = directeurs));
  }

  protected createFromForm(): IEquipement {
    return {
      ...new Equipement(),
      id: this.editForm.get(['id'])!.value,
      statut: this.editForm.get(['statut'])!.value,
      refArduino: this.editForm.get(['refArduino'])!.value,
      dateCrea: this.editForm.get(['dateCrea'])!.value ? dayjs(this.editForm.get(['dateCrea'])!.value, DATE_TIME_FORMAT) : undefined,
      libelle: this.editForm.get(['libelle'])!.value,
      marque: this.editForm.get(['marque'])!.value,
      type: this.editForm.get(['type'])!.value,
      employes: this.editForm.get(['employes'])!.value,
      gestionnaires: this.editForm.get(['gestionnaires'])!.value,
    };
  }
}
