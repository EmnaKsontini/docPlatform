import { IRequest } from 'app/shared/model/request.model';
import { IDoctor } from 'app/shared/model/doctor.model';

export interface IPatient {
    id?: number;
    cin?: number;
    name?: string;
    email?: string;
    phoneNumber?: number;
    requests?: IRequest[];
    doctors?: IDoctor[];
}

export class Patient implements IPatient {
    constructor(
        public id?: number,
        public cin?: number,
        public name?: string,
        public email?: string,
        public phoneNumber?: number,
        public requests?: IRequest[],
        public doctors?: IDoctor[]
    ) {}
}
