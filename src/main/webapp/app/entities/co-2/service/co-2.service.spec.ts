import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICo2, Co2 } from '../co-2.model';

import { Co2Service } from './co-2.service';

describe('Co2 Service', () => {
  let service: Co2Service;
  let httpMock: HttpTestingController;
  let elemDefault: ICo2;
  let expectedResult: ICo2 | ICo2[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(Co2Service);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      volume: 0,
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

    it('should create a Co2', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Co2()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Co2', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          volume: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Co2', () => {
      const patchObject = Object.assign({}, new Co2());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Co2', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          volume: 1,
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

    it('should delete a Co2', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCo2ToCollectionIfMissing', () => {
      it('should add a Co2 to an empty array', () => {
        const co2: ICo2 = { id: 123 };
        expectedResult = service.addCo2ToCollectionIfMissing([], co2);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(co2);
      });

      it('should not add a Co2 to an array that contains it', () => {
        const co2: ICo2 = { id: 123 };
        const co2Collection: ICo2[] = [
          {
            ...co2,
          },
          { id: 456 },
        ];
        expectedResult = service.addCo2ToCollectionIfMissing(co2Collection, co2);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Co2 to an array that doesn't contain it", () => {
        const co2: ICo2 = { id: 123 };
        const co2Collection: ICo2[] = [{ id: 456 }];
        expectedResult = service.addCo2ToCollectionIfMissing(co2Collection, co2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(co2);
      });

      it('should add only unique Co2 to an array', () => {
        const co2Array: ICo2[] = [{ id: 123 }, { id: 456 }, { id: 72053 }];
        const co2Collection: ICo2[] = [{ id: 123 }];
        expectedResult = service.addCo2ToCollectionIfMissing(co2Collection, ...co2Array);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const co2: ICo2 = { id: 123 };
        const co22: ICo2 = { id: 456 };
        expectedResult = service.addCo2ToCollectionIfMissing([], co2, co22);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(co2);
        expect(expectedResult).toContain(co22);
      });

      it('should accept null and undefined values', () => {
        const co2: ICo2 = { id: 123 };
        expectedResult = service.addCo2ToCollectionIfMissing([], null, co2, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(co2);
      });

      it('should return initial array if no Co2 is added', () => {
        const co2Collection: ICo2[] = [{ id: 123 }];
        expectedResult = service.addCo2ToCollectionIfMissing(co2Collection, undefined, null);
        expect(expectedResult).toEqual(co2Collection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
