import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'doctor',
                loadChildren: './doctor/doctor.module#DoctorsPlatformDoctorModule'
            },
            {
                path: 'patient',
                loadChildren: './patient/patient.module#DoctorsPlatformPatientModule'
            },
            {
                path: 'patient',
                loadChildren: './patient/patient.module#DoctorsPlatformPatientModule'
            },
            {
                path: 'request',
                loadChildren: './request/request.module#DoctorsPlatformRequestModule'
            },
            {
                path: 'appointment',
                loadChildren: './appointment/appointment.module#DoctorsPlatformAppointmentModule'
            },
            {
                path: 'request',
                loadChildren: './request/request.module#DoctorsPlatformRequestModule'
            },
            {
                path: 'request',
                loadChildren: './request/request.module#DoctorsPlatformRequestModule'
            },
            {
                path: 'request',
                loadChildren: './request/request.module#DoctorsPlatformRequestModule'
            },
            {
                path: 'doctor',
                loadChildren: './doctor/doctor.module#DoctorsPlatformDoctorModule'
            },
            {
                path: 'patient',
                loadChildren: './patient/patient.module#DoctorsPlatformPatientModule'
            }
        ]),
        RouterModule.forChild([
            {
                path: 'tip',
                loadChildren: './tip/tip.module#DoctorsPlatformTipModule'
            }
        ]),
        RouterModule.forChild([
            {
                path: 'tip',
                loadChildren: './tip/tip.module#DoctorsPlatformTipModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DoctorsPlatformEntityModule {}
