import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDirecteur } from '../directeur.model';
import { DirecteurService } from '../service/directeur.service';

@Component({
  templateUrl: './directeur-delete-dialog.component.html',
})
export class DirecteurDeleteDialogComponent {
  directeur?: IDirecteur;

  constructor(protected directeurService: DirecteurService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.directeurService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
