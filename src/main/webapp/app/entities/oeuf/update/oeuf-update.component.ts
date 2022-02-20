import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOeuf, Oeuf } from '../oeuf.model';
import { OeufService } from '../service/oeuf.service';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

@Component({
  selector: 'jhi-oeuf-update',
  templateUrl: './oeuf-update.component.html',
})
export class OeufUpdateComponent implements OnInit {
  isSaving = false;

  variablesCollection: IVariable[] = [];

  editForm = this.fb.group({
    id: [],
    nombreJournalier: [],
    variable: [null, Validators.required],
  });

  constructor(
    protected oeufService: OeufService,
    protected variableService: VariableService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ oeuf }) => {
      this.updateForm(oeuf);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const oeuf = this.createFromForm();
    if (oeuf.id !== undefined) {
      this.subscribeToSaveResponse(this.oeufService.update(oeuf));
    } else {
      this.subscribeToSaveResponse(this.oeufService.create(oeuf));
    }
  }

  trackVariableById(index: number, item: IVariable): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOeuf>>): void {
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

  protected updateForm(oeuf: IOeuf): void {
    this.editForm.patchValue({
      id: oeuf.id,
      nombreJournalier: oeuf.nombreJournalier,
      variable: oeuf.variable,
    });

    this.variablesCollection = this.variableService.addVariableToCollectionIfMissing(this.variablesCollection, oeuf.variable);
  }

  protected loadRelationshipsOptions(): void {
    this.variableService
      .query({ filter: 'oeuf-is-null' })
      .pipe(map((res: HttpResponse<IVariable[]>) => res.body ?? []))
      .pipe(
        map((variables: IVariable[]) =>
          this.variableService.addVariableToCollectionIfMissing(variables, this.editForm.get('variable')!.value)
        )
      )
      .subscribe((variables: IVariable[]) => (this.variablesCollection = variables));
  }

  protected createFromForm(): IOeuf {
    return {
      ...new Oeuf(),
      id: this.editForm.get(['id'])!.value,
      nombreJournalier: this.editForm.get(['nombreJournalier'])!.value,
      variable: this.editForm.get(['variable'])!.value,
    };
  }
}
