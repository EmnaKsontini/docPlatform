import { Component, OnInit, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiDataUtils } from 'ng-jhipster';
import { ITip } from 'app/shared/model/tip.model';
import { TipService } from './tip.service';

@Component({
    selector: 'jhi-tip-update',
    templateUrl: './tip-update.component.html'
})
export class TipUpdateComponent implements OnInit {
    tip: ITip;
    isSaving: boolean;

    constructor(
        protected dataUtils: JhiDataUtils,
        protected tipService: TipService,
        protected elementRef: ElementRef,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ tip }) => {
            this.tip = tip;
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    clearInputImage(field: string, fieldContentType: string, idInput: string) {
        this.dataUtils.clearInputImage(this.tip, this.elementRef, field, fieldContentType, idInput);
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.tip.id !== undefined) {
            this.subscribeToSaveResponse(this.tipService.update(this.tip));
        } else {
            this.subscribeToSaveResponse(this.tipService.create(this.tip));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ITip>>) {
        result.subscribe((res: HttpResponse<ITip>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
