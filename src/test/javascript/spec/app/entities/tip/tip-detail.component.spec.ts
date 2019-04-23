/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DoctorsPlatformTestModule } from '../../../test.module';
import { TipDetailComponent } from 'app/entities/tip/tip-detail.component';
import { Tip } from 'app/shared/model/tip.model';

describe('Component Tests', () => {
    describe('Tip Management Detail Component', () => {
        let comp: TipDetailComponent;
        let fixture: ComponentFixture<TipDetailComponent>;
        const route = ({ data: of({ tip: new Tip(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DoctorsPlatformTestModule],
                declarations: [TipDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(TipDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TipDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.tip).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
