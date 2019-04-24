/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { DoctorsPlatformTestModule } from '../../../test.module';
import { TipDeleteDialogComponent } from 'app/entities/tip/tip-delete-dialog.component';
import { TipService } from 'app/entities/tip/tip.service';

describe('Component Tests', () => {
    describe('Tip Management Delete Component', () => {
        let comp: TipDeleteDialogComponent;
        let fixture: ComponentFixture<TipDeleteDialogComponent>;
        let service: TipService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DoctorsPlatformTestModule],
                declarations: [TipDeleteDialogComponent]
            })
                .overrideTemplate(TipDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TipDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TipService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
