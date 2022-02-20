import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NH3Component } from '../list/nh-3.component';
import { NH3DetailComponent } from '../detail/nh-3-detail.component';
import { NH3UpdateComponent } from '../update/nh-3-update.component';
import { NH3RoutingResolveService } from './nh-3-routing-resolve.service';

const nH3Route: Routes = [
  {
    path: '',
    component: NH3Component,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NH3DetailComponent,
    resolve: {
      nH3: NH3RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NH3UpdateComponent,
    resolve: {
      nH3: NH3RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NH3UpdateComponent,
    resolve: {
      nH3: NH3RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(nH3Route)],
  exports: [RouterModule],
})
export class NH3RoutingModule {}
