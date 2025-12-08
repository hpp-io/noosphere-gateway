import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState } from "app/shared/reducers/reducer.utils";
import { defaultValue, IVerifier } from "app/shared/model/verifier.model";
import { ISearchVerifier } from "app/shared/model/search-verifier.model";

const initialState: EntityState<IVerifier> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/verifiers';

export const searchVerifiers = createAsyncThunk('verifier/fetch_entities',
    async (entity: ISearchVerifier, thunkAPI) => {
      const requestUrl = `${ apiUrl }/search${ entity.sort ? `?page=${ entity.page }&size=${ entity.size }&sort=${ entity.sort }&` : '?' }cacheBuster=${ new Date().getTime() }`;
      const requestBody = {
        name: entity.name,
        searchText: entity.searchText,
        verifierAddress: entity.verifierAddress,
        statusCode: entity.statusCode,
        createdByUserId: entity.createdByUserId,
      };
      return axios.post<IVerifier[]>(requestUrl, requestBody);
    });

export const createVerifier = createAsyncThunk('verifier/create_entity',
    async (entity: Omit<IVerifier, 'id'>, thunkAPI) => {
      const result = await axios.post<IVerifier>(apiUrl, entity);
      return result;
    });

export const VerifierSlice = createEntitySlice({
  name: 'verifier',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(searchVerifiers), (state, action) => {
      state.loading = false;
      state.entities = action.payload.data;
      state.totalItems = parseInt(action.payload.headers['x-total-count'], 10);
    })
    .addMatcher(isPending(searchVerifiers), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    .addMatcher(isFulfilled(createVerifier), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state.entity = action.payload.data;
    })
    .addMatcher(isPending(createVerifier), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.updating = true;
      state.loading = true;
    })
    .addMatcher(isRejected(searchVerifiers, createVerifier), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = false;
      state.errorMessage = action.error.message;
    })
    ;
  },
});

export default VerifierSlice.reducer;