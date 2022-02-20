import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICo2 } from '../co-2.model';

@Component({
  selector: 'jhi-co-2-detail',
  templateUrl: './co-2-detail.component.html',
})
export class Co2DetailComponent implements OnInit {
  co2: ICo2 | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ co2 }) => {
      this.co2 = co2;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
