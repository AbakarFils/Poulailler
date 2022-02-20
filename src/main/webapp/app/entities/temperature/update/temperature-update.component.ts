import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITemperature, Temperature } from '../temperature.model';
import { TemperatureService } from '../service/temperature.service';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

@Component({
  selector: 'jhi-temperature-update',
  templateUrl: './temperature-update.component.html',
})
export class TemperatureUpdateComponent implements OnInit {
  isSaving = false;

  variablesCollection: IVariable[] = [];

  editForm = this.fb.group({
    id: [],
    dregree: [],
    variable: [null, Validators.required],
  });

  constructor(
    protected temperatureService: TemperatureService,
    protected variableService: VariableService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ temperature }) => {
      this.updateForm(temperature);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const temperature = this.createFromForm();
    if (temperature.id !== undefined) {
      this.subscribeToSaveResponse(this.temperatureService.update(temperature));
    } else {
      this.subscribeToSaveResponse(this.temperatureService.create(temperature));
    }
  }

  trackVariableById(index: number, item: IVariable): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITemperature>>): void {
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

  protected updateForm(temperature: ITemperature): void {
    this.editForm.patchValue({
      id: temperature.id,
      dregree: temperature.dregree,
      variable: temperature.variable,
    });

    this.variablesCollection = this.variableService.addVariableToCollectionIfMissing(this.variablesCollection, temperature.variable);
  }

  protected loadRelationshipsOptions(): void {
    this.variableService
      .query({ filter: 'temperature-is-null' })
      .pipe(map((res: HttpResponse<IVariable[]>) => res.body ?? []))
      .pipe(
        map((variables: IVariable[]) =>
          this.variableService.addVariableToCollectionIfMissing(variables, this.editForm.get('variable')!.value)
        )
      )
      .subscribe((variables: IVariable[]) => (this.variablesCollection = variables));
  }

  protected createFromForm(): ITemperature {
    return {
      ...new Temperature(),
      id: this.editForm.get(['id'])!.value,
      dregree: this.editForm.get(['dregree'])!.value,
      variable: this.editForm.get(['variable'])!.value,
    };
  }
}
