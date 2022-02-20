import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IHumidite, Humidite } from '../humidite.model';
import { HumiditeService } from '../service/humidite.service';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

@Component({
  selector: 'jhi-humidite-update',
  templateUrl: './humidite-update.component.html',
})
export class HumiditeUpdateComponent implements OnInit {
  isSaving = false;

  variablesCollection: IVariable[] = [];

  editForm = this.fb.group({
    id: [],
    niveau: [],
    variable: [null, Validators.required],
  });

  constructor(
    protected humiditeService: HumiditeService,
    protected variableService: VariableService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ humidite }) => {
      this.updateForm(humidite);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const humidite = this.createFromForm();
    if (humidite.id !== undefined) {
      this.subscribeToSaveResponse(this.humiditeService.update(humidite));
    } else {
      this.subscribeToSaveResponse(this.humiditeService.create(humidite));
    }
  }

  trackVariableById(index: number, item: IVariable): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHumidite>>): void {
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

  protected updateForm(humidite: IHumidite): void {
    this.editForm.patchValue({
      id: humidite.id,
      niveau: humidite.niveau,
      variable: humidite.variable,
    });

    this.variablesCollection = this.variableService.addVariableToCollectionIfMissing(this.variablesCollection, humidite.variable);
  }

  protected loadRelationshipsOptions(): void {
    this.variableService
      .query({ filter: 'humidite-is-null' })
      .pipe(map((res: HttpResponse<IVariable[]>) => res.body ?? []))
      .pipe(
        map((variables: IVariable[]) =>
          this.variableService.addVariableToCollectionIfMissing(variables, this.editForm.get('variable')!.value)
        )
      )
      .subscribe((variables: IVariable[]) => (this.variablesCollection = variables));
  }

  protected createFromForm(): IHumidite {
    return {
      ...new Humidite(),
      id: this.editForm.get(['id'])!.value,
      niveau: this.editForm.get(['niveau'])!.value,
      variable: this.editForm.get(['variable'])!.value,
    };
  }
}
