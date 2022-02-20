import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DirecteurComponent } from '../list/directeur.component';
import { DirecteurDetailComponent } from '../detail/directeur-detail.component';
import { DirecteurUpdateComponent } from '../update/directeur-update.component';
import { DirecteurRoutingResolveService } from './directeur-routing-resolve.service';

const directeurRoute: Routes = [
  {
    path: '',
    component: DirecteurComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DirecteurDetailComponent,
    resolve: {
      directeur: DirecteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DirecteurUpdateComponent,
    resolve: {
      directeur: DirecteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DirecteurUpdateComponent,
    resolve: {
      directeur: DirecteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(directeurRoute)],
  exports: [RouterModule],
})
export class DirecteurRoutingModule {}
