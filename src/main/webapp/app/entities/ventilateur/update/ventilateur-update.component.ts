import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IVentilateur, Ventilateur } from '../ventilateur.model';
import { VentilateurService } from '../service/ventilateur.service';

@Component({
  selector: 'jhi-ventilateur-update',
  templateUrl: './ventilateur-update.component.html',
})
export class VentilateurUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [],
    vitesse: [],
  });

  constructor(protected ventilateurService: VentilateurService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ventilateur }) => {
      this.updateForm(ventilateur);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ventilateur = this.createFromForm();
    if (ventilateur.id !== undefined) {
      this.subscribeToSaveResponse(this.ventilateurService.update(ventilateur));
    } else {
      this.subscribeToSaveResponse(this.ventilateurService.create(ventilateur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVentilateur>>): void {
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

  protected updateForm(ventilateur: IVentilateur): void {
    this.editForm.patchValue({
      id: ventilateur.id,
      libelle: ventilateur.libelle,
      vitesse: ventilateur.vitesse,
    });
  }

  protected createFromForm(): IVentilateur {
    return {
      ...new Ventilateur(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      vitesse: this.editForm.get(['vitesse'])!.value,
    };
  }
}
