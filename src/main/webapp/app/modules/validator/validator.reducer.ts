import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
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
    ;
  },
});

export default ValidatorSlice.reducer;