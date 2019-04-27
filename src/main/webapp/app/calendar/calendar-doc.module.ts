import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { DemoUtilsModule } from '../calendar-util/calendar-util.module';
import { CalendarDocComponent } from './calendar-doc.component';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { RouterModule } from '@angular/router';
import { calendarRoute } from 'app/calendar/calendar-doc.route';
const ENTITY_STATES = calendarRoute;

@NgModule({
    declarations: [CalendarDocComponent],
    exports: [CalendarDocComponent],
    imports: [
        CommonModule,
        RouterModule.forChild([ENTITY_STATES]),
        DemoUtilsModule,
        CalendarModule.forRoot({
            provide: DateAdapter,
            useFactory: adapterFactory
        })
    ]
})
export class CalendarDocModule {}
