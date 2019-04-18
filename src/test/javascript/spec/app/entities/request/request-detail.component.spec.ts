/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DoctorsPlatformTestModule } from '../../../test.module';
import { RequestDetailComponent } from 'app/entities/request/request-detail.component';
import { Request } from 'app/shared/model/request.model';

describe('Component Tests', () => {
    describe('Request Management Detail Component', () => {
        let comp: RequestDetailComponent;
        let fixture: ComponentFixture<RequestDetailComponent>;
        const route = ({ data: of({ request: new Request(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DoctorsPlatformTestModule],
                declarations: [RequestDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(RequestDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(RequestDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.request).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
