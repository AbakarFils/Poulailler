import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICo2, Co2 } from '../co-2.model';
import { Co2Service } from '../service/co-2.service';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

@Component({
  selector: 'jhi-co-2-update',
  templateUrl: './co-2-update.component.html',
})
export class Co2UpdateComponent implements OnInit {
  isSaving = false;

  variablesCollection: IVariable[] = [];

  editForm = this.fb.group({
    id: [],
    volume: [],
    variable: [null, Validators.required],
  });

  constructor(
    protected co2Service: Co2Service,
    protected variableService: VariableService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ co2 }) => {
      this.updateForm(co2);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const co2 = this.createFromForm();
    if (co2.id !== undefined) {
      this.subscribeToSaveResponse(this.co2Service.update(co2));
    } else {
      this.subscribeToSaveResponse(this.co2Service.create(co2));
    }
  }

  trackVariableById(index: number, item: IVariable): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICo2>>): void {
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

  protected updateForm(co2: ICo2): void {
    this.editForm.patchValue({
      id: co2.id,
      volume: co2.volume,
      variable: co2.variable,
    });

    this.variablesCollection = this.variableService.addVariableToCollectionIfMissing(this.variablesCollection, co2.variable);
  }

  protected loadRelationshipsOptions(): void {
    this.variableService
      .query({ filter: 'co2-is-null' })
      .pipe(map((res: HttpResponse<IVariable[]>) => res.body ?? []))
      .pipe(
        map((variables: IVariable[]) =>
          this.variableService.addVariableToCollectionIfMissing(variables, this.editForm.get('variable')!.value)
        )
      )
      .subscribe((variables: IVariable[]) => (this.variablesCollection = variables));
  }

  protected createFromForm(): ICo2 {
    return {
      ...new Co2(),
      id: this.editForm.get(['id'])!.value,
      volume: this.editForm.get(['volume'])!.value,
      variable: this.editForm.get(['variable'])!.value,
    };
  }
}
