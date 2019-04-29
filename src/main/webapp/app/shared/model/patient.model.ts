import { IRequest } from 'app/shared/model/request.model';
import { IDoctor } from 'app/shared/model/doctor.model';

export interface IPatient {
    id?: number;
    name?: string;
    phoneNumber?: number;
    cin?: number;
    email?: string;
    requests?: IRequest[];
    doctors?: IDoctor[];
}

export class Patient implements IPatient {
    constructor(
        public id?: number,
        public name?: string,
        public phoneNumber?: number,
        public cin?: number,
        public email?: string,
        public requests?: IRequest[],
        public doctors?: IDoctor[]
    ) {}
}
