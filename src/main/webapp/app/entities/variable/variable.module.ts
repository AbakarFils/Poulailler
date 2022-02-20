import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VariableComponent } from './list/variable.component';
import { VariableDetailComponent } from './detail/variable-detail.component';
import { VariableRoutingModule } from './route/variable-routing.module';

@NgModule({
  imports: [SharedModule, VariableRoutingModule],
  declarations: [VariableComponent, VariableDetailComponent],
})
export class VariableModule {}
