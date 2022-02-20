import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOeuf } from '../oeuf.model';

@Component({
  selector: 'jhi-oeuf-detail',
  templateUrl: './oeuf-detail.component.html',
})
export class OeufDetailComponent implements OnInit {
  oeuf: IOeuf | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ oeuf }) => {
      this.oeuf = oeuf;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
