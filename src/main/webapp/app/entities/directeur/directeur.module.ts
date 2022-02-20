import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DirecteurComponent } from './list/directeur.component';
import { DirecteurDetailComponent } from './detail/directeur-detail.component';
import { DirecteurUpdateComponent } from './update/directeur-update.component';
import { DirecteurDeleteDialogComponent } from './delete/directeur-delete-dialog.component';
import { DirecteurRoutingModule } from './route/directeur-routing.module';

@NgModule({
  imports: [SharedModule, DirecteurRoutingModule],
  declarations: [DirecteurComponent, DirecteurDetailComponent, DirecteurUpdateComponent, DirecteurDeleteDialogComponent],
  entryComponents: [DirecteurDeleteDialogComponent],
})
export class DirecteurModule {}
