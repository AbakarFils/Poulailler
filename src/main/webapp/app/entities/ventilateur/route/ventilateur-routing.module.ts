import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VentilateurComponent } from '../list/ventilateur.component';
import { VentilateurDetailComponent } from '../detail/ventilateur-detail.component';
import { VentilateurUpdateComponent } from '../update/ventilateur-update.component';
import { VentilateurRoutingResolveService } from './ventilateur-routing-resolve.service';

const ventilateurRoute: Routes = [
  {
    path: '',
    component: VentilateurComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VentilateurDetailComponent,
    resolve: {
      ventilateur: VentilateurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VentilateurUpdateComponent,
    resolve: {
      ventilateur: VentilateurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VentilateurUpdateComponent,
    resolve: {
      ventilateur: VentilateurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ventilateurRoute)],
  exports: [RouterModule],
})
export class VentilateurRoutingModule {}
