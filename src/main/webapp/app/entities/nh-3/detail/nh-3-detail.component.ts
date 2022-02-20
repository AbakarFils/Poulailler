import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INH3 } from '../nh-3.model';

@Component({
  selector: 'jhi-nh-3-detail',
  templateUrl: './nh-3-detail.component.html',
})
export class NH3DetailComponent implements OnInit {
  nH3: INH3 | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nH3 }) => {
      this.nH3 = nH3;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
