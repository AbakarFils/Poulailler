import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILampe } from '../lampe.model';

@Component({
  selector: 'jhi-lampe-detail',
  templateUrl: './lampe-detail.component.html',
})
export class LampeDetailComponent implements OnInit {
  lampe: ILampe | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lampe }) => {
      this.lampe = lampe;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
