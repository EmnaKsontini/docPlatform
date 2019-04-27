import { Moment } from 'moment';

export interface IAppointment {
    id?: number;
    dateAndHour?: Moment;
    requestAppointement?: string;
    requestId?: number;
}

export class Appointment implements IAppointment {
    constructor(public id?: number, public dateAndHour?: Moment, public requestAppointement?: string, public requestId?: number) {}
}
