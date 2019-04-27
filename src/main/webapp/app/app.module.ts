import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { Ng2Webstorage } from 'ngx-webstorage';
import { NgJhipsterModule } from 'ng-jhipster';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { DoctorsPlatformSharedModule } from 'app/shared';
import { DoctorsPlatformCoreModule } from 'app/core';
import { DoctorsPlatformAppRoutingModule } from './app-routing.module';
import { DoctorsPlatformHomeModule } from './home/home.module';
import { DoctorsPlatformAccountModule } from './account/account.module';
import { DoctorsPlatformEntityModule } from './entities/entity.module';
import * as moment from 'moment';
import { ChatbotRasaModule } from 'angular-chat-widget-rasa';
import { JhiMainComponent, NavbarComponent, FooterComponent, PageRibbonComponent, ActiveMenuDirective, ErrorComponent } from './layouts';
import { SidebarComponent } from './layouts/sidebar/sidebar.component';
import { SimpleCalendarComponent } from './simple-calendar/simple-calendar.component';
import { ChatModule } from 'app/chat/chat.module';

import { CalendarDocComponent } from './calendar/calendar-doc.component';
import { CalendarDocModule } from 'app/calendar/calendar-doc.module';
import { AgmCoreModule } from '@agm/core';

@NgModule({
    imports: [
        BrowserModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
        NgJhipsterModule.forRoot({
            // set below to true to make alerts look like toast
            alertAsToast: false,
            alertTimeout: 5000,
            i18nEnabled: true,
            defaultI18nLang: 'en'
        }),
        DoctorsPlatformSharedModule.forRoot(),
        DoctorsPlatformCoreModule,
        DoctorsPlatformHomeModule,
        DoctorsPlatformAccountModule,
        ChatbotRasaModule,
        ChatModule,

        // jhipster-needle-angular-add-module JHipster will add new module here
        DoctorsPlatformEntityModule,
        DoctorsPlatformAppRoutingModule,
        BrowserAnimationsModule,
        CalendarDocModule
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent,
        SidebarComponent,
        SimpleCalendarComponent
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class DoctorsPlatformAppModule {
    constructor(private dpConfig: NgbDatepickerConfig) {
        this.dpConfig.minDate = { year: moment().year() - 100, month: 1, day: 1 };
    }
}
