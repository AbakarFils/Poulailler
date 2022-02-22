import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VentilateurComponent } from './list/ventilateur.component';
import { VentilateurDetailComponent } from './detail/ventilateur-detail.component';
import { VentilateurUpdateComponent } from './update/ventilateur-update.component';
import { VentilateurDeleteDialogComponent } from './delete/ventilateur-delete-dialog.component';
import { VentilateurRoutingModule } from './route/ventilateur-routing.module';

@NgModule({
  imports: [SharedModule, VentilateurRoutingModule],
  declarations: [VentilateurComponent, VentilateurDetailComponent, VentilateurUpdateComponent, VentilateurDeleteDialogComponent],
  entryComponents: [VentilateurDeleteDialogComponent],
})
export class VentilateurModule {}
