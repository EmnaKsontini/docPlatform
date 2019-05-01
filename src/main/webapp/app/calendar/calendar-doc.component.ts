import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CalendarEvent, CalendarEventAction, CalendarEventTimesChangedEvent, CalendarView } from 'angular-calendar';
import { startOfDay, endOfDay, subDays, addDays, endOfMonth, isSameDay, isSameMonth, addHours } from 'date-fns';
import { colors } from '../calendar-util/colors';
import { Subject } from 'rxjs';
import { Appointment } from 'app/shared/model/appointment.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { AppointmentService } from 'app/entities/appointment';
import { JhiAlertService } from 'ng-jhipster';
import { User } from 'app/core';
import { IDoctor } from 'app/shared/model/doctor.model';
import { HttpResponse } from '@angular/common/http';

@Component({
    selector: 'jhi-calendar',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './calendar-doc.component.html',
    styles: []
})
export class CalendarDocComponent {
    @ViewChild('dataTable')
    table;
    dataTable: any;
    @ViewChild('modalContent')
    modalContent: TemplateRef<any>;
    num: number;
    view: String = 'month';

    viewDate: Date = new Date();

    modalData: {
        action: string;
        event: CalendarEvent;
    };
    date: Date = new Date();

    actions: CalendarEventAction[] = [
        {
            label: '<i class="fa fa-fw fa-pencil"></i>',
            onClick: ({ event }: { event: CalendarEvent }): void => {
                this.handleEvent('edit', event);
            }
        },
        {
            label: '<i class="fa fa-fw fa-times"></i> ',
            onClick: ({ event }: { event: CalendarEvent }): void => {
                this.handleEvent('delete', event);
            }
        }
    ];

    refresh: Subject<any> = new Subject();
    event: CalendarEvent;

    events: CalendarEvent[] = [];

    activeDayIsOpen: any = false;
    appointments: Appointment[];
    appointment: Appointment;
    myappointments: Appointment[];
    user: User;
    myDoctors: IDoctor[];
    constructor(
        private modal: NgbModal,
        private activatedRoute: ActivatedRoute,
        private appointmentService: AppointmentService,
        private router: Router,
        private jhiAlertService: JhiAlertService
    ) {}

    ngOnInit() {
        this.appointments = [];

        this.appointmentService.getCurrentUser().subscribe((res: HttpResponse<User>) => {
            console.log('login:' + res.body.login);
            if (res.body.login == 'admin') {
                console.log('admin');
                this.appointmentService.getAllAppointmentList().subscribe(appointmentsList => {
                    this.appointments = appointmentsList;
                    this.onSucc1();
                });
            } else {
                console.log('patient');
                this.appointmentService.getAppointmentList().subscribe(appointmentsList => {
                    this.myappointments = appointmentsList;
                });
                this.appointmentService.getAllDoctorNamesAppointments().subscribe(myDoctors => {
                    this.myDoctors = myDoctors;
                    this.onSucc();
                });
            }
        });
    }

    onSucc() {
        for (let i = 0; i < this.myappointments.length; i++) {
            console.log(this.myappointments[i].dateAndHour.toString());
            this.date = new Date(this.myappointments[i].dateAndHour.toString());
            (this.event = {
                id: this.myappointments[i].id,
                start: this.date,
                end: this.date,
                title: this.myappointments[i].dateAndHour.toString() + ' Doctor ' + this.myDoctors[i].name,
                color: {
                    primary: '#' + (0x1000000 + Math.random() * 0xffffff).toString(16).substr(1, 6),
                    secondary: '#' + (0x1000000 + Math.random() * 0xffffff).toString(16).substr(1, 6)
                },
                actions: this.actions,
                allDay: true,
                resizable: {
                    beforeStart: true,
                    afterEnd: true
                },
                draggable: true
            }),
                this.addAnEvent(this.event);
        }
    }

    onSucc1() {
        for (let i = 0; i < this.appointments.length; i++) {
            console.log(this.appointments[i].dateAndHour.toString());
            this.date = new Date(this.appointments[i].dateAndHour.toString());
            (this.event = {
                id: this.appointments[i].id,
                start: this.date,
                end: this.date,
                title: this.appointments[i].dateAndHour.toString(),
                color: {
                    primary: '#' + (0x1000000 + Math.random() * 0xffffff).toString(16).substr(1, 6),
                    secondary: '#' + (0x1000000 + Math.random() * 0xffffff).toString(16).substr(1, 6)
                },
                actions: this.actions,
                allDay: true,
                resizable: {
                    beforeStart: true,
                    afterEnd: true
                },
                draggable: true
            }),
                this.addAnEvent(this.event);
        }
    }

    dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
        if (isSameMonth(date, this.viewDate)) {
            this.viewDate = date;
            if ((isSameDay(this.viewDate, date) && this.activeDayIsOpen === true) || events.length === 0) {
                this.activeDayIsOpen = false;
            } else {
                this.activeDayIsOpen = true;
            }
        }
        console.log('111111111111');
    }

    eventTimesChanged({ event, newStart, newEnd }: CalendarEventTimesChangedEvent): void {
        event.start = newStart;
        event.end = newEnd;
        this.handleEvent('Dropped or resized', event);
        this.refresh.next();
        console.log('22222222222');
    }

    handleEvent(action: string, event: CalendarEvent): void {
        action = action === 'Clicked' ? 'edit' : action;
        this.modalData = { event, action };
        let url = this.router.createUrlTree(['/', 'appointment', { outlets: { popup: event + '/delete' } }]);

        if (action === 'edit') {
            url = this.router.createUrlTree(['/appointment', event.id, 'edit']);
        }
        this.router.navigateByUrl(url.toString());
        if (action === 'delete') {
            this.appointmentService.event.subscribe(data => {
                if (data) {
                    this.events.indexOf(event, this.num);
                    this.events.splice(this.num, 1);
                    this.refresh.next();
                }
            });
        }
    }

    addEvent(): void {
        this.events.push({
            title: 'New event',
            start: startOfDay(new Date()),
            end: endOfDay(new Date()),
            color: colors.red,
            draggable: true,
            resizable: {
                beforeStart: true,
                afterEnd: true
            }
        });
        this.refresh.next();
        console.log('444444444444');
    }

    addAnEvent(event: CalendarEvent): void {
        this.events.push(event);
        this.refresh.next();
        console.log('55555555555');
    }
}
