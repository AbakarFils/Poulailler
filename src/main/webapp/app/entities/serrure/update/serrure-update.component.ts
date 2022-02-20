import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISerrure, Serrure } from '../serrure.model';
import { SerrureService } from '../service/serrure.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

@Component({
  selector: 'jhi-serrure-update',
  templateUrl: './serrure-update.component.html',
})
export class SerrureUpdateComponent implements OnInit {
  isSaving = false;

  equipementsCollection: IEquipement[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [],
    dimension: [],
    equipement: [],
  });

  constructor(
    protected serrureService: SerrureService,
    protected equipementService: EquipementService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ serrure }) => {
      this.updateForm(serrure);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const serrure = this.createFromForm();
    if (serrure.id !== undefined) {
      this.subscribeToSaveResponse(this.serrureService.update(serrure));
    } else {
      this.subscribeToSaveResponse(this.serrureService.create(serrure));
    }
  }

  trackEquipementById(index: number, item: IEquipement): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISerrure>>): void {
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

  protected updateForm(serrure: ISerrure): void {
    this.editForm.patchValue({
      id: serrure.id,
      libelle: serrure.libelle,
      dimension: serrure.dimension,
      equipement: serrure.equipement,
    });

    this.equipementsCollection = this.equipementService.addEquipementToCollectionIfMissing(this.equipementsCollection, serrure.equipement);
  }

  protected loadRelationshipsOptions(): void {
    this.equipementService
      .query({ 'serrureId.specified': 'false' })
      .pipe(map((res: HttpResponse<IEquipement[]>) => res.body ?? []))
      .pipe(
        map((equipements: IEquipement[]) =>
          this.equipementService.addEquipementToCollectionIfMissing(equipements, this.editForm.get('equipement')!.value)
        )
      )
      .subscribe((equipements: IEquipement[]) => (this.equipementsCollection = equipements));
  }

  protected createFromForm(): ISerrure {
    return {
      ...new Serrure(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      dimension: this.editForm.get(['dimension'])!.value,
      equipement: this.editForm.get(['equipement'])!.value,
    };
  }
}
