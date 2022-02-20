import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISerrure } from '../serrure.model';

@Component({
  selector: 'jhi-serrure-detail',
  templateUrl: './serrure-detail.component.html',
})
export class SerrureDetailComponent implements OnInit {
  serrure: ISerrure | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ serrure }) => {
      this.serrure = serrure;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
