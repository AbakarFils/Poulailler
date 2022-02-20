import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INH3, NH3 } from '../nh-3.model';
import { NH3Service } from '../service/nh-3.service';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

@Component({
  selector: 'jhi-nh-3-update',
  templateUrl: './nh-3-update.component.html',
})
export class NH3UpdateComponent implements OnInit {
  isSaving = false;

  variablesCollection: IVariable[] = [];

  editForm = this.fb.group({
    id: [],
    volume: [],
    variable: [null, Validators.required],
  });

  constructor(
    protected nH3Service: NH3Service,
    protected variableService: VariableService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nH3 }) => {
      this.updateForm(nH3);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const nH3 = this.createFromForm();
    if (nH3.id !== undefined) {
      this.subscribeToSaveResponse(this.nH3Service.update(nH3));
    } else {
      this.subscribeToSaveResponse(this.nH3Service.create(nH3));
    }
  }

  trackVariableById(index: number, item: IVariable): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INH3>>): void {
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

  protected updateForm(nH3: INH3): void {
    this.editForm.patchValue({
      id: nH3.id,
      volume: nH3.volume,
      variable: nH3.variable,
    });

    this.variablesCollection = this.variableService.addVariableToCollectionIfMissing(this.variablesCollection, nH3.variable);
  }

  protected loadRelationshipsOptions(): void {
    this.variableService
      .query({ filter: 'nh3-is-null' })
      .pipe(map((res: HttpResponse<IVariable[]>) => res.body ?? []))
      .pipe(
        map((variables: IVariable[]) =>
          this.variableService.addVariableToCollectionIfMissing(variables, this.editForm.get('variable')!.value)
        )
      )
      .subscribe((variables: IVariable[]) => (this.variablesCollection = variables));
  }

  protected createFromForm(): INH3 {
    return {
      ...new NH3(),
      id: this.editForm.get(['id'])!.value,
      volume: this.editForm.get(['volume'])!.value,
      variable: this.editForm.get(['variable'])!.value,
    };
  }
}
