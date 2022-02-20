import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOeuf } from '../oeuf.model';
import { OeufService } from '../service/oeuf.service';

@Component({
  templateUrl: './oeuf-delete-dialog.component.html',
})
export class OeufDeleteDialogComponent {
  oeuf?: IOeuf;

  constructor(protected oeufService: OeufService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.oeufService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
