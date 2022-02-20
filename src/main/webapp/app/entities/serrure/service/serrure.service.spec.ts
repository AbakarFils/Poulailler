import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISerrure, Serrure } from '../serrure.model';

import { SerrureService } from './serrure.service';

describe('Serrure Service', () => {
  let service: SerrureService;
  let httpMock: HttpTestingController;
  let elemDefault: ISerrure;
  let expectedResult: ISerrure | ISerrure[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SerrureService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      libelle: 'AAAAAAA',
      dimension: 0,
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

    it('should create a Serrure', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Serrure()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Serrure', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
          dimension: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Serrure', () => {
      const patchObject = Object.assign(
        {
          dimension: 1,
        },
        new Serrure()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Serrure', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
          dimension: 1,
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

    it('should delete a Serrure', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSerrureToCollectionIfMissing', () => {
      it('should add a Serrure to an empty array', () => {
        const serrure: ISerrure = { id: 123 };
        expectedResult = service.addSerrureToCollectionIfMissing([], serrure);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(serrure);
      });

      it('should not add a Serrure to an array that contains it', () => {
        const serrure: ISerrure = { id: 123 };
        const serrureCollection: ISerrure[] = [
          {
            ...serrure,
          },
          { id: 456 },
        ];
        expectedResult = service.addSerrureToCollectionIfMissing(serrureCollection, serrure);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Serrure to an array that doesn't contain it", () => {
        const serrure: ISerrure = { id: 123 };
        const serrureCollection: ISerrure[] = [{ id: 456 }];
        expectedResult = service.addSerrureToCollectionIfMissing(serrureCollection, serrure);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serrure);
      });

      it('should add only unique Serrure to an array', () => {
        const serrureArray: ISerrure[] = [{ id: 123 }, { id: 456 }, { id: 78610 }];
        const serrureCollection: ISerrure[] = [{ id: 123 }];
        expectedResult = service.addSerrureToCollectionIfMissing(serrureCollection, ...serrureArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const serrure: ISerrure = { id: 123 };
        const serrure2: ISerrure = { id: 456 };
        expectedResult = service.addSerrureToCollectionIfMissing([], serrure, serrure2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serrure);
        expect(expectedResult).toContain(serrure2);
      });

      it('should accept null and undefined values', () => {
        const serrure: ISerrure = { id: 123 };
        expectedResult = service.addSerrureToCollectionIfMissing([], null, serrure, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(serrure);
      });

      it('should return initial array if no Serrure is added', () => {
        const serrureCollection: ISerrure[] = [{ id: 123 }];
        expectedResult = service.addSerrureToCollectionIfMissing(serrureCollection, undefined, null);
        expect(expectedResult).toEqual(serrureCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
