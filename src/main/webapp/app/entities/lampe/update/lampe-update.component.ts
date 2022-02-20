import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILampe, Lampe } from '../lampe.model';
import { LampeService } from '../service/lampe.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

@Component({
  selector: 'jhi-lampe-update',
  templateUrl: './lampe-update.component.html',
})
export class LampeUpdateComponent implements OnInit {
  isSaving = false;

  equipementsCollection: IEquipement[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [],
    equipement: [],
  });

  constructor(
    protected lampeService: LampeService,
    protected equipementService: EquipementService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lampe }) => {
      this.updateForm(lampe);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lampe = this.createFromForm();
    if (lampe.id !== undefined) {
      this.subscribeToSaveResponse(this.lampeService.update(lampe));
    } else {
      this.subscribeToSaveResponse(this.lampeService.create(lampe));
    }
  }

  trackEquipementById(index: number, item: IEquipement): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILampe>>): void {
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

  protected updateForm(lampe: ILampe): void {
    this.editForm.patchValue({
      id: lampe.id,
      libelle: lampe.libelle,
      equipement: lampe.equipement,
    });

    this.equipementsCollection = this.equipementService.addEquipementToCollectionIfMissing(this.equipementsCollection, lampe.equipement);
  }

  protected loadRelationshipsOptions(): void {
    this.equipementService
      .query({ 'lampeId.specified': 'false' })
      .pipe(map((res: HttpResponse<IEquipement[]>) => res.body ?? []))
      .pipe(
        map((equipements: IEquipement[]) =>
          this.equipementService.addEquipementToCollectionIfMissing(equipements, this.editForm.get('equipement')!.value)
        )
      )
      .subscribe((equipements: IEquipement[]) => (this.equipementsCollection = equipements));
  }

  protected createFromForm(): ILampe {
    return {
      ...new Lampe(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      equipement: this.editForm.get(['equipement'])!.value,
    };
  }
}
