import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDirecteur } from '../directeur.model';

@Component({
  selector: 'jhi-directeur-detail',
  templateUrl: './directeur-detail.component.html',
})
export class DirecteurDetailComponent implements OnInit {
  directeur: IDirecteur | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ directeur }) => {
      this.directeur = directeur;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
