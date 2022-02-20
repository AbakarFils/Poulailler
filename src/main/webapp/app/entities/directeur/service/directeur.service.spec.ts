import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDirecteur, Directeur } from '../directeur.model';

import { DirecteurService } from './directeur.service';

describe('Directeur Service', () => {
  let service: DirecteurService;
  let httpMock: HttpTestingController;
  let elemDefault: IDirecteur;
  let expectedResult: IDirecteur | IDirecteur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DirecteurService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Directeur', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Directeur()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Directeur', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should partial update a Directeur', () => {
      const patchObject = Object.assign(
        {
          adresse: 'BBBBBB',
        },
        new Directeur()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Directeur', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Directeur', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDirecteurToCollectionIfMissing', () => {
      it('should add a Directeur to an empty array', () => {
        const directeur: IDirecteur = { id: 123 };
        expectedResult = service.addDirecteurToCollectionIfMissing([], directeur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(directeur);
      });

      it('should not add a Directeur to an array that contains it', () => {
        const directeur: IDirecteur = { id: 123 };
        const directeurCollection: IDirecteur[] = [
          {
            ...directeur,
          },
          { id: 456 },
        ];
        expectedResult = service.addDirecteurToCollectionIfMissing(directeurCollection, directeur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Directeur to an array that doesn't contain it", () => {
        const directeur: IDirecteur = { id: 123 };
        const directeurCollection: IDirecteur[] = [{ id: 456 }];
        expectedResult = service.addDirecteurToCollectionIfMissing(directeurCollection, directeur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(directeur);
      });

      it('should add only unique Directeur to an array', () => {
        const directeurArray: IDirecteur[] = [{ id: 123 }, { id: 456 }, { id: 74587 }];
        const directeurCollection: IDirecteur[] = [{ id: 123 }];
        expectedResult = service.addDirecteurToCollectionIfMissing(directeurCollection, ...directeurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const directeur: IDirecteur = { id: 123 };
        const directeur2: IDirecteur = { id: 456 };
        expectedResult = service.addDirecteurToCollectionIfMissing([], directeur, directeur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(directeur);
        expect(expectedResult).toContain(directeur2);
      });

      it('should accept null and undefined values', () => {
        const directeur: IDirecteur = { id: 123 };
        expectedResult = service.addDirecteurToCollectionIfMissing([], null, directeur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(directeur);
      });

      it('should return initial array if no Directeur is added', () => {
        const directeurCollection: IDirecteur[] = [{ id: 123 }];
        expectedResult = service.addDirecteurToCollectionIfMissing(directeurCollection, undefined, null);
        expect(expectedResult).toEqual(directeurCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
