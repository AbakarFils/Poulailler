import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVentilateur } from '../ventilateur.model';
import { VentilateurService } from '../service/ventilateur.service';

@Component({
  templateUrl: './ventilateur-delete-dialog.component.html',
})
export class VentilateurDeleteDialogComponent {
  ventilateur?: IVentilateur;

  constructor(protected ventilateurService: VentilateurService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ventilateurService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
