import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IVariable } from '../variable.model';

import { VariableService } from './variable.service';

describe('Variable Service', () => {
  let service: VariableService;
  let httpMock: HttpTestingController;
  let elemDefault: IVariable;
  let expectedResult: IVariable | IVariable[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VariableService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      plageMax: 0,
      dateCreation: currentDate,
      lue: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateCreation: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should return a list of Variable', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          plageMax: 1,
          dateCreation: currentDate.format(DATE_TIME_FORMAT),
          lue: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateCreation: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    describe('addVariableToCollectionIfMissing', () => {
      it('should add a Variable to an empty array', () => {
        const variable: IVariable = { id: 123 };
        expectedResult = service.addVariableToCollectionIfMissing([], variable);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(variable);
      });

      it('should not add a Variable to an array that contains it', () => {
        const variable: IVariable = { id: 123 };
        const variableCollection: IVariable[] = [
          {
            ...variable,
          },
          { id: 456 },
        ];
        expectedResult = service.addVariableToCollectionIfMissing(variableCollection, variable);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Variable to an array that doesn't contain it", () => {
        const variable: IVariable = { id: 123 };
        const variableCollection: IVariable[] = [{ id: 456 }];
        expectedResult = service.addVariableToCollectionIfMissing(variableCollection, variable);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(variable);
      });

      it('should add only unique Variable to an array', () => {
        const variableArray: IVariable[] = [{ id: 123 }, { id: 456 }, { id: 98875 }];
        const variableCollection: IVariable[] = [{ id: 123 }];
        expectedResult = service.addVariableToCollectionIfMissing(variableCollection, ...variableArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const variable: IVariable = { id: 123 };
        const variable2: IVariable = { id: 456 };
        expectedResult = service.addVariableToCollectionIfMissing([], variable, variable2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(variable);
        expect(expectedResult).toContain(variable2);
      });

      it('should accept null and undefined values', () => {
        const variable: IVariable = { id: 123 };
        expectedResult = service.addVariableToCollectionIfMissing([], null, variable, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(variable);
      });

      it('should return initial array if no Variable is added', () => {
        const variableCollection: IVariable[] = [{ id: 123 }];
        expectedResult = service.addVariableToCollectionIfMissing(variableCollection, undefined, null);
        expect(expectedResult).toEqual(variableCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
