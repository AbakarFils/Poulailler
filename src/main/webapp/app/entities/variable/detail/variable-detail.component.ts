import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVariable } from '../variable.model';

@Component({
  selector: 'jhi-variable-detail',
  templateUrl: './variable-detail.component.html',
})
export class VariableDetailComponent implements OnInit {
  variable: IVariable | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ variable }) => {
      this.variable = variable;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
