import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEmploye, Employe } from '../employe.model';

import { EmployeService } from './employe.service';

describe('Employe Service', () => {
  let service: EmployeService;
  let httpMock: HttpTestingController;
  let elemDefault: IEmploye;
  let expectedResult: IEmploye | IEmploye[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EmployeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      numeroIdentite: 'AAAAAAA',
      adresse: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Employe', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Employe()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Employe', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numeroIdentite: 'BBBBBB',
          adresse: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Employe', () => {
      const patchObject = Object.assign(
        {
          adresse: 'BBBBBB',
        },
        new Employe()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Employe', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numeroIdentite: 'BBBBBB',
          adresse: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Employe', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEmployeToCollectionIfMissing', () => {
      it('should add a Employe to an empty array', () => {
        const employe: IEmploye = { id: 123 };
        expectedResult = service.addEmployeToCollectionIfMissing([], employe);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(employe);
      });

      it('should not add a Employe to an array that contains it', () => {
        const employe: IEmploye = { id: 123 };
        const employeCollection: IEmploye[] = [
          {
            ...employe,
          },
          { id: 456 },
        ];
        expectedResult = service.addEmployeToCollectionIfMissing(employeCollection, employe);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Employe to an array that doesn't contain it", () => {
        const employe: IEmploye = { id: 123 };
        const employeCollection: IEmploye[] = [{ id: 456 }];
        expectedResult = service.addEmployeToCollectionIfMissing(employeCollection, employe);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(employe);
      });

      it('should add only unique Employe to an array', () => {
        const employeArray: IEmploye[] = [{ id: 123 }, { id: 456 }, { id: 86179 }];
        const employeCollection: IEmploye[] = [{ id: 123 }];
        expectedResult = service.addEmployeToCollectionIfMissing(employeCollection, ...employeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const employe: IEmploye = { id: 123 };
        const employe2: IEmploye = { id: 456 };
        expectedResult = service.addEmployeToCollectionIfMissing([], employe, employe2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(employe);
        expect(expectedResult).toContain(employe2);
      });

      it('should accept null and undefined values', () => {
        const employe: IEmploye = { id: 123 };
        expectedResult = service.addEmployeToCollectionIfMissing([], null, employe, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(employe);
      });

      it('should return initial array if no Employe is added', () => {
        const employeCollection: IEmploye[] = [{ id: 123 }];
        expectedResult = service.addEmployeToCollectionIfMissing(employeCollection, undefined, null);
        expect(expectedResult).toEqual(employeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
