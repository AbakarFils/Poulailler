import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILampe, Lampe } from '../lampe.model';

import { LampeService } from './lampe.service';

describe('Lampe Service', () => {
  let service: LampeService;
  let httpMock: HttpTestingController;
  let elemDefault: ILampe;
  let expectedResult: ILampe | ILampe[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LampeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      libelle: 'AAAAAAA',
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

    it('should create a Lampe', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Lampe()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Lampe', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Lampe', () => {
      const patchObject = Object.assign({}, new Lampe());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Lampe', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
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

    it('should delete a Lampe', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLampeToCollectionIfMissing', () => {
      it('should add a Lampe to an empty array', () => {
        const lampe: ILampe = { id: 123 };
        expectedResult = service.addLampeToCollectionIfMissing([], lampe);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(lampe);
      });

      it('should not add a Lampe to an array that contains it', () => {
        const lampe: ILampe = { id: 123 };
        const lampeCollection: ILampe[] = [
          {
            ...lampe,
          },
          { id: 456 },
        ];
        expectedResult = service.addLampeToCollectionIfMissing(lampeCollection, lampe);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Lampe to an array that doesn't contain it", () => {
        const lampe: ILampe = { id: 123 };
        const lampeCollection: ILampe[] = [{ id: 456 }];
        expectedResult = service.addLampeToCollectionIfMissing(lampeCollection, lampe);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lampe);
      });

      it('should add only unique Lampe to an array', () => {
        const lampeArray: ILampe[] = [{ id: 123 }, { id: 456 }, { id: 80378 }];
        const lampeCollection: ILampe[] = [{ id: 123 }];
        expectedResult = service.addLampeToCollectionIfMissing(lampeCollection, ...lampeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const lampe: ILampe = { id: 123 };
        const lampe2: ILampe = { id: 456 };
        expectedResult = service.addLampeToCollectionIfMissing([], lampe, lampe2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lampe);
        expect(expectedResult).toContain(lampe2);
      });

      it('should accept null and undefined values', () => {
        const lampe: ILampe = { id: 123 };
        expectedResult = service.addLampeToCollectionIfMissing([], null, lampe, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(lampe);
      });

      it('should return initial array if no Lampe is added', () => {
        const lampeCollection: ILampe[] = [{ id: 123 }];
        expectedResult = service.addLampeToCollectionIfMissing(lampeCollection, undefined, null);
        expect(expectedResult).toEqual(lampeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
