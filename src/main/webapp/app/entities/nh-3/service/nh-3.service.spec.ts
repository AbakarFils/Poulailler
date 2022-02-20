import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INH3, NH3 } from '../nh-3.model';

import { NH3Service } from './nh-3.service';

describe('NH3 Service', () => {
  let service: NH3Service;
  let httpMock: HttpTestingController;
  let elemDefault: INH3;
  let expectedResult: INH3 | INH3[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NH3Service);
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

    it('should create a NH3', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new NH3()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NH3', () => {
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

    it('should partial update a NH3', () => {
      const patchObject = Object.assign(
        {
          volume: 1,
        },
        new NH3()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NH3', () => {
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

    it('should delete a NH3', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNH3ToCollectionIfMissing', () => {
      it('should add a NH3 to an empty array', () => {
        const nH3: INH3 = { id: 123 };
        expectedResult = service.addNH3ToCollectionIfMissing([], nH3);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nH3);
      });

      it('should not add a NH3 to an array that contains it', () => {
        const nH3: INH3 = { id: 123 };
        const nH3Collection: INH3[] = [
          {
            ...nH3,
          },
          { id: 456 },
        ];
        expectedResult = service.addNH3ToCollectionIfMissing(nH3Collection, nH3);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NH3 to an array that doesn't contain it", () => {
        const nH3: INH3 = { id: 123 };
        const nH3Collection: INH3[] = [{ id: 456 }];
        expectedResult = service.addNH3ToCollectionIfMissing(nH3Collection, nH3);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nH3);
      });

      it('should add only unique NH3 to an array', () => {
        const nH3Array: INH3[] = [{ id: 123 }, { id: 456 }, { id: 7852 }];
        const nH3Collection: INH3[] = [{ id: 123 }];
        expectedResult = service.addNH3ToCollectionIfMissing(nH3Collection, ...nH3Array);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const nH3: INH3 = { id: 123 };
        const nH32: INH3 = { id: 456 };
        expectedResult = service.addNH3ToCollectionIfMissing([], nH3, nH32);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nH3);
        expect(expectedResult).toContain(nH32);
      });

      it('should accept null and undefined values', () => {
        const nH3: INH3 = { id: 123 };
        expectedResult = service.addNH3ToCollectionIfMissing([], null, nH3, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nH3);
      });

      it('should return initial array if no NH3 is added', () => {
        const nH3Collection: INH3[] = [{ id: 123 }];
        expectedResult = service.addNH3ToCollectionIfMissing(nH3Collection, undefined, null);
        expect(expectedResult).toEqual(nH3Collection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
