import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IHumidite } from '../humidite.model';

@Component({
  selector: 'jhi-humidite-detail',
  templateUrl: './humidite-detail.component.html',
})
export class HumiditeDetailComponent implements OnInit {
  humidite: IHumidite | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ humidite }) => {
      this.humidite = humidite;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
