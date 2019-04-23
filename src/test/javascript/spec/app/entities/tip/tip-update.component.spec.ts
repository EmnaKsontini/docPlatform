/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { DoctorsPlatformTestModule } from '../../../test.module';
import { TipUpdateComponent } from 'app/entities/tip/tip-update.component';
import { TipService } from 'app/entities/tip/tip.service';
import { Tip } from 'app/shared/model/tip.model';

describe('Component Tests', () => {
    describe('Tip Management Update Component', () => {
        let comp: TipUpdateComponent;
        let fixture: ComponentFixture<TipUpdateComponent>;
        let service: TipService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DoctorsPlatformTestModule],
                declarations: [TipUpdateComponent]
            })
                .overrideTemplate(TipUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(TipUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TipService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Tip(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.tip = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Tip();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.tip = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
