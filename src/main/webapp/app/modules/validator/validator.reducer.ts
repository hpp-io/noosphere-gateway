import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState } from "app/shared/reducers/reducer.utils";
import { defaultValue, IValidator } from "app/shared/model/validator.model";
import { ISearchValidator } from "app/shared/model/search-validator.model";

const initialState: EntityState<IValidator> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/validators';

export const searchValidators = createAsyncThunk('validator/fetch_entities',
    async (entity: ISearchValidator, thunkAPI) => {
      const requestUrl = `${ apiUrl }/search${ entity.sort ? `?page=${ entity.page }&size=${ entity.size }&sort=${ entity.sort }&` : '?' }cacheBuster=${ new Date().getTime() }`;
      const requestBody = {
        name: entity.name,
        searchText: entity.searchText,
        walletAddress: entity.walletAddress,
        verifierAddress: entity.verifierAddress,
        statusCode: entity.statusCode,
        createdByUserId: entity.createdByUserId,
      };
      return axios.post<IValidator[]>(requestUrl, requestBody);
    });

export const createValidator = createAsyncThunk('validator/create_entity',
    async (entity: Omit<IValidator, 'id'>, thunkAPI) => {
      const result = await axios.post<IValidator>(apiUrl, entity);
      return result;
    });

export const ValidatorSlice = createEntitySlice({
  name: 'validator',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(searchValidators), (state, action) => {
      state.loading = false;
      state.entities = action.payload.data;
      state.totalItems = parseInt(action.payload.headers['x-total-count'], 10);
    })
    .addMatcher(isPending(searchValidators), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    .addMatcher(isFulfilled(createValidator), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state.entity = action.payload.data;
    })
    .addMatcher(isPending(createValidator), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.updating = true;
      state.loading = true;
    })
    .addMatcher(isRejected(searchValidators, createValidator), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = false;
      state.errorMessage = action.error.message;
    })
    ;
  },
});

export default ValidatorSlice.reducer;