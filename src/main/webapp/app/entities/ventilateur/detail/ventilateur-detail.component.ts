import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVentilateur } from '../ventilateur.model';

@Component({
  selector: 'jhi-ventilateur-detail',
  templateUrl: './ventilateur-detail.component.html',
})
export class VentilateurDetailComponent implements OnInit {
  ventilateur: IVentilateur | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ventilateur }) => {
      this.ventilateur = ventilateur;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
