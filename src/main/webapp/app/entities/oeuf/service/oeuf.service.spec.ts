import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOeuf, Oeuf } from '../oeuf.model';

import { OeufService } from './oeuf.service';

describe('Oeuf Service', () => {
  let service: OeufService;
  let httpMock: HttpTestingController;
  let elemDefault: IOeuf;
  let expectedResult: IOeuf | IOeuf[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OeufService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nombreJournalier: 0,
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

    it('should create a Oeuf', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Oeuf()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Oeuf', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nombreJournalier: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Oeuf', () => {
      const patchObject = Object.assign({}, new Oeuf());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Oeuf', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nombreJournalier: 1,
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

    it('should delete a Oeuf', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOeufToCollectionIfMissing', () => {
      it('should add a Oeuf to an empty array', () => {
        const oeuf: IOeuf = { id: 123 };
        expectedResult = service.addOeufToCollectionIfMissing([], oeuf);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(oeuf);
      });

      it('should not add a Oeuf to an array that contains it', () => {
        const oeuf: IOeuf = { id: 123 };
        const oeufCollection: IOeuf[] = [
          {
            ...oeuf,
          },
          { id: 456 },
        ];
        expectedResult = service.addOeufToCollectionIfMissing(oeufCollection, oeuf);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Oeuf to an array that doesn't contain it", () => {
        const oeuf: IOeuf = { id: 123 };
        const oeufCollection: IOeuf[] = [{ id: 456 }];
        expectedResult = service.addOeufToCollectionIfMissing(oeufCollection, oeuf);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(oeuf);
      });

      it('should add only unique Oeuf to an array', () => {
        const oeufArray: IOeuf[] = [{ id: 123 }, { id: 456 }, { id: 48566 }];
        const oeufCollection: IOeuf[] = [{ id: 123 }];
        expectedResult = service.addOeufToCollectionIfMissing(oeufCollection, ...oeufArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const oeuf: IOeuf = { id: 123 };
        const oeuf2: IOeuf = { id: 456 };
        expectedResult = service.addOeufToCollectionIfMissing([], oeuf, oeuf2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(oeuf);
        expect(expectedResult).toContain(oeuf2);
      });

      it('should accept null and undefined values', () => {
        const oeuf: IOeuf = { id: 123 };
        expectedResult = service.addOeufToCollectionIfMissing([], null, oeuf, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(oeuf);
      });

      it('should return initial array if no Oeuf is added', () => {
        const oeufCollection: IOeuf[] = [{ id: 123 }];
        expectedResult = service.addOeufToCollectionIfMissing(oeufCollection, undefined, null);
        expect(expectedResult).toEqual(oeufCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
