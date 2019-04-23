import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITip } from 'app/shared/model/tip.model';
import { TipService } from './tip.service';

@Component({
    selector: 'jhi-tip-delete-dialog',
    templateUrl: './tip-delete-dialog.component.html'
})
export class TipDeleteDialogComponent {
    tip: ITip;

    constructor(protected tipService: TipService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.tipService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'tipListModification',
                content: 'Deleted an tip'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-tip-delete-popup',
    template: ''
})
export class TipDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ tip }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(TipDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.tip = tip;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/tip', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/tip', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
